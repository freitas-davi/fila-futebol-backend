package pilantras.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.entities.Sessao;
import pilantras.domain.enums.Categoria;
import pilantras.dto.AddJogadorRequestDTO;
import pilantras.repository.JogadorSessaoRepository;

import java.util.List;

@ApplicationScoped
public class FilaService {

    private final SessaoService sessaoService;
    private final JogadorSessaoRepository jogadorRepository;

    public FilaService(SessaoService sessaoService,
                       JogadorSessaoRepository jogadorRepository) {
        this.sessaoService = sessaoService;
        this.jogadorRepository = jogadorRepository;
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