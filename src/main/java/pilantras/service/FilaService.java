package pilantras.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.entities.Sessao;
import pilantras.domain.enums.Categoria;
import pilantras.dto.AddJogadorRequest;
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
    public JogadorSessao adicionar(Long sessaoId, AddJogadorRequest req) {

        Sessao sessao = sessaoService.buscarSessaoAtiva(sessaoId);

        JogadorSessao j = new JogadorSessao();
        j.sessao = sessao;
        j.nome = req.nome().trim();
        j.categoria = req.categoria();
        j.posicaoFila = jogadorRepository.proximaPosicao(sessaoId);

        jogadorRepository.persist(j);
        return j;
    }

    @Transactional
    public void remover(Long sessaoId, Long jogadorId) {
        JogadorSessao j = buscarJogador(sessaoId, jogadorId);
        if (j.status == JogadorSessao.Status.JOGANDO) {
            throw new WebApplicationException("Jogador está em campo", Response.Status.CONFLICT);
        }
        jogadorRepository.delete(j);
    }

    @Transactional
    public JogadorSessao atualizarCategoria(Long sessaoId, Long jogadorId, Categoria categoria)
    {
        JogadorSessao j = buscarJogador(sessaoId, jogadorId);
        j.categoria = categoria;
        return j;
    }

    public List<JogadorSessao> listar(Long sessaoId) {
        sessaoService.buscarSessaoAtiva(sessaoId);
        List<JogadorSessao> jogadores = jogadorRepository.listarPorSessao(sessaoId);

        if (jogadores.isEmpty()) {
            throw new NotFoundException("Nenhum jogador encontrado na sessão");
        }
        return jogadores;
    }

    private JogadorSessao buscarJogador(Long sessaoId, Long jogadorId) {
        sessaoService.buscarSessaoAtiva(sessaoId);

        return jogadorRepository.findByIdOptional(jogadorId)
                .filter(j -> j.sessao.id.equals(sessaoId))
                .orElseThrow(() -> new NotFoundException("Jogador não encontrado na sessão"));
    }

}