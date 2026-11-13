package pilantras.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
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
        Sessao sessao = sessaoService.buscarSessaoAtivaPorId(sessaoId);

        JogadorSessao j = new JogadorSessao();
        j.sessao = sessao;
        j.nome = req.nome().trim();
        j.categoria = req.categoria();
        j.posicaoFila = jogadorRepository.proximaPosicao(sessaoId);

        jogadorRepository.persist(j);
        return j;
    }

    @Transactional
    public PartidaResponseDTO adicionarJogadoresEIniciar(Long sessaoId, List<AddJogadorRequestDTO> req) {
        Sessao sessao = sessaoService.buscarSessaoAtivaPorId(sessaoId);
        long jogadoresComCategoria = req.stream()
                .filter(j -> j.categoria() != null)
                .count();

        if (jogadoresComCategoria < JOGADORES_EM_CAMPO) {
            throw new WebApplicationException(
                    "São necessários 10 jogadores com categoria marcada na lista",
                    Response.Status.CONFLICT);
        }

        List<JogadorSessao> jogadoresEmCampo = new ArrayList<>();
        int posicao = jogadorRepository.proximaPosicao(sessaoId);
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
        return partidaService.iniciarComJogadores(sessaoId, jogadoresEmCampo);
    }

    @Transactional
    public void removerJogadorLista(Long sessaoId, Long jogadorId) {
        JogadorSessao j = buscarJogador(sessaoId, jogadorId);

        if (j.status == JogadorSessao.Status.JOGANDO) {
            substituirJogadorEmCampo(j);
        }
        jogadorRepository.delete(j);
    }

    @Transactional
    public JogadorSessao atualizarCategoria(Long sessaoId, Long jogadorId, Categoria categoria) {
        JogadorSessao j = buscarJogador(sessaoId, jogadorId);
        j.categoria = categoria;
        return j;
    }

    public List<JogadorSessao> listarJogadores(Long sessaoId) {
        sessaoService.buscarSessaoAtivaPorId(sessaoId);

        List<JogadorSessao> jogadores =
                jogadorRepository.listarPorSessao(sessaoId);

        if (jogadores.isEmpty()) {
            throw new NotFoundException("Nenhum jogador encontrado na sessão");
        }
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