package pilantras.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import pilantras.client.Sorteador;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.entities.Partida;
import pilantras.domain.entities.Sessao;
import pilantras.domain.enums.LadoTime;
import pilantras.dto.PartidaResponse;
import pilantras.repository.JogadorSessaoRepository;
import pilantras.repository.PartidaRepository;

import java.util.List;

@ApplicationScoped
public class PartidaService {

    private static final int JOGADORES_EM_CAMPO = 10;

    private final SessaoService sessaoService;
    private final JogadorSessaoRepository jogadorRepository;
    private final PartidaRepository partidaRepository;
    private final Sorteador sorteador;

    public PartidaService(SessaoService sessaoService,
                          JogadorSessaoRepository jogadorRepository,
                          PartidaRepository partidaRepository,
                          Sorteador sorteador)
    {
        this.sessaoService = sessaoService;
        this.jogadorRepository = jogadorRepository;
        this.partidaRepository = partidaRepository;
        this.sorteador = sorteador;
    }

    @Transactional
    public PartidaResponse iniciar(Long sessaoId) {
        Sessao sessao = sessaoService.buscarSessaoAtiva(sessaoId);

        if (partidaRepository.buscarEmAndamento(sessaoId).isPresent()) {
            throw new WebApplicationException("Já existe uma partida em andamento", Response.Status.CONFLICT);
        }

        List<JogadorSessao> escolhidos =
                jogadorRepository.primeirosDaFilaComCategoria(sessaoId, JOGADORES_EM_CAMPO);

        if (escolhidos.size() < JOGADORES_EM_CAMPO) {
            throw new WebApplicationException(
                    "São necessários %d jogadores com categoria marcada na fila".formatted(JOGADORES_EM_CAMPO),
                    Response.Status.CONFLICT);
        }

        Sorteador.Times times = sorteador.sortear(escolhidos);
        times.ladoA().forEach(j -> colocarEmCampo(j, LadoTime.A));
        times.ladoB().forEach(j -> colocarEmCampo(j, LadoTime.B));

        Partida partida = new Partida();
        partida.sessao = sessao;
        partida.numero = partidaRepository.proximoNumero(sessaoId);
        partidaRepository.persist(partida);

        return PartidaResponse.from(partida, times);
    }

    private void colocarEmCampo(JogadorSessao j, LadoTime lado) {
        j.status = JogadorSessao.Status.JOGANDO;
        j.lado = lado;
    }
}
