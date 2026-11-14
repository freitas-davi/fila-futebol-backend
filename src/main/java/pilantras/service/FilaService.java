package pilantras.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.entities.Sessao;
import pilantras.domain.enums.Categoria;
import pilantras.dto.AddJogadorRequestDTO;
import pilantras.dto.PartidaResponseDTO;
import pilantras.repository.JogadorSessaoRepository;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FilaService {

    private static final Logger LOG = Logger.getLogger(FilaService.class);
    private static final int JOGADORES_EM_CAMPO = 10;

    private final SessaoService sessaoService;
    private final JogadorSessaoRepository jogadorRepository;
    private final PartidaService partidaService;

    public FilaService(SessaoService sessaoService,
                       JogadorSessaoRepository jogadorRepository,
                       PartidaService partidaService) {
        this.sessaoService = sessaoService;
        this.jogadorRepository = jogadorRepository;
        this.partidaService = partidaService;
    }

    @Transactional
    public JogadorSessao adicionarJogadorLista(Long sessaoId, AddJogadorRequestDTO req) {
        LOG.infof("Adicionando jogador à fila: sessaoId=%d", sessaoId);
        Sessao sessao = sessaoService.buscarSessaoAtivaPorId(sessaoId);

        JogadorSessao j = new JogadorSessao();
        j.sessao = sessao;
        j.nome = req.nome().trim();
        j.categoria = req.categoria();
        j.posicaoFila = jogadorRepository.proximaPosicao(sessaoId);

        jogadorRepository.persist(j);
        LOG.infof("Jogador adicionado à fila: sessaoId=%d jogadorId=%d", sessaoId, j.id);
        return j;
    }

    @Transactional
    public PartidaResponseDTO adicionarJogadoresEIniciar(Long sessaoId, List<AddJogadorRequestDTO> req) {
        LOG.infof("Adicionando jogadores e iniciando partida: sessaoId=%d quantidade=%d",
                sessaoId,
                req.size()
        );
        Sessao sessao = sessaoService.buscarSessaoAtivaPorId(sessaoId);

        if (jogadorRepository.count("sessao.id", sessaoId) > 0) {
            sessao.status = Sessao.Status.ENCERRADA;
            jogadorRepository.flush();

            sessao = sessaoService.criarNovaSessao();
        }

        long jogadoresComCategoria = req.stream()
                .filter(j -> j.categoria() != null)
                .count();

        if (jogadoresComCategoria < JOGADORES_EM_CAMPO) {
            throw new WebApplicationException(
                    "São necessários 10 jogadores com categoria marcada na lista",
                    Response.Status.CONFLICT);
        }

        List<JogadorSessao> jogadoresEmCampo = new ArrayList<>();
        int posicao = 1;
        for (AddJogadorRequestDTO jogadorReq : req) {
            JogadorSessao jogador = new JogadorSessao();
            jogador.sessao = sessao;
            jogador.nome = jogadorReq.nome().trim();
            jogador.categoria = jogadorReq.categoria();
            jogador.posicaoFila = posicao++;
            jogadorRepository.persist(jogador);

            if (jogadoresEmCampo.size() < JOGADORES_EM_CAMPO && jogador.categoria != null) {
                jogadoresEmCampo.add(jogador);
            }
        }
        PartidaResponseDTO partida = partidaService.iniciarComJogadores(sessao.id, jogadoresEmCampo);;
        LOG.infof("Jogadores adicionados e partida iniciada: sessaoId=%d quantidade=%d",
                sessaoId, req.size());
        return partida;
    }

    @Transactional
    public void removerJogadorLista(Long sessaoId, Long jogadorId) {
        LOG.infof("Removendo jogador da fila: sessaoId=%d jogadorId=%d", sessaoId, jogadorId);
        JogadorSessao j = buscarJogador(sessaoId, jogadorId);

        if (j.status == JogadorSessao.Status.JOGANDO) {
            substituirJogadorEmCampo(j);
        }
        jogadorRepository.delete(j);
        LOG.infof("Jogador removido da fila: sessaoId=%d jogadorId=%d", sessaoId, jogadorId);
    }

    @Transactional
    public JogadorSessao atualizarCategoria(Long sessaoId, Long jogadorId, Categoria categoria) {
        LOG.infof("Atualizando categoria do jogador: sessaoId=%d jogadorId=%d",
                sessaoId, jogadorId);
        JogadorSessao j = buscarJogador(sessaoId, jogadorId);
        j.categoria = categoria;
        LOG.infof("Categoria do jogador atualizada: sessaoId=%d jogadorId=%d",
                sessaoId, jogadorId);
        return j;
    }

    public List<JogadorSessao> listarJogadores(Long sessaoId) {
        LOG.infof("Listando jogadores da sessão: sessaoId=%d", sessaoId);
        sessaoService.buscarSessaoAtivaPorId(sessaoId);

        List<JogadorSessao> jogadores =
                jogadorRepository.listarPorSessao(sessaoId);

        if (jogadores.isEmpty()) {
            throw new NotFoundException("Nenhum jogador encontrado na sessão");
        }
        LOG.infof("Jogadores listados: sessaoId=%d quantidade=%d", sessaoId, jogadores.size());
        return jogadores;
    }

    private JogadorSessao buscarJogador(Long sessaoId, Long jogadorId) {
        sessaoService.buscarSessaoAtivaPorId(sessaoId);
        return jogadorRepository
                .findByIdOptional(jogadorId)
                .filter(j -> j.sessao.id.equals(sessaoId))
                .orElseThrow(() -> new NotFoundException("Jogador não encontrado na sessão"));
    }

    private void substituirJogadorEmCampo(JogadorSessao jogadorRemovido) {
        jogadorRepository.primeirosProximosDaFila(jogadorRemovido.sessao.id, 1)
                .stream()
                .findFirst()
                .ifPresent(proximo -> {
                    proximo.status = JogadorSessao.Status.JOGANDO;
                    proximo.lado = jogadorRemovido.lado;
                });
    }

}