package pilantras.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import pilantras.client.Sorteador;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.entities.Partida;
import pilantras.domain.entities.Sessao;
import pilantras.domain.enums.LadoTime;
import pilantras.dto.PartidaResponseDTO;
import pilantras.dto.ResultadoPartidaRequestDTO;
import pilantras.repository.JogadorSessaoRepository;
import pilantras.repository.PartidaRepository;

import java.time.LocalDateTime;
import java.util.List;


@ApplicationScoped
public class PartidaService {

    private static final int JOGADORES_EM_CAMPO = 10;
    private static final int POR_TIME = 5;

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
    public PartidaResponseDTO iniciar(Long sessaoId) {
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
        jogadorRepository.flush();

        return abrirPartida(sessao);
    }

    @Transactional
    public PartidaResponseDTO registrarResultado(Long sessaoId, Long partidaId, ResultadoPartidaRequestDTO req) {
        Sessao sessao = sessaoService.buscarSessaoAtiva(sessaoId);
        Partida partida = partidaRepository.findByIdOptional(partidaId)
                .filter(p -> p.sessao.id.equals(sessaoId))
                .orElseThrow(() -> new NotFoundException("Partida não encontrada na sessão"));

        if (partida.status != Partida.Status.EM_ANDAMENTO) {
            throw new WebApplicationException("Partida já finalizada", Response.Status.CONFLICT);
        }

        LadoTime perdedor = switch (req.resultado()) {
            case LADO_A -> LadoTime.B;
            case LADO_B -> LadoTime.A;
            case EMPATE -> throw new WebApplicationException("Empate ainda não implementado", 501);
        };

        partida.status = Partida.Status.FINALIZADA;
        partida.resultado = req.resultado();
        partida.finalizadaEm = LocalDateTime.now();

        mandarParaFimDaFila(sessao, perdedor);
        entrarEmCampo(sessao, perdedor);

        return abrirPartida(sessao);
    }

    private PartidaResponseDTO abrirPartida(Sessao sessao) {
        List<JogadorSessao> ladoA = jogadorRepository.emCampo(sessao.id, LadoTime.A);
        List<JogadorSessao> ladoB = jogadorRepository.emCampo(sessao.id, LadoTime.B);

        Partida p = new Partida();
        p.sessao = sessao;
        p.numero = partidaRepository.proximoNumero(sessao.id);
        partidaRepository.persist(p);

        return PartidaResponseDTO.from(
                p, new Sorteador.Times(
                        ladoA, ladoB
                ));
    }

    private void colocarEmCampo(JogadorSessao j, LadoTime lado) {
        j.status = JogadorSessao.Status.JOGANDO;
        j.lado = lado;
    }

    private void mandarParaFimDaFila(Sessao sessao, LadoTime lado) {
        int pos = jogadorRepository.proximaPosicao(sessao.id);
        for (JogadorSessao j : jogadorRepository.emCampo(sessao.id, lado)) {
            j.status = JogadorSessao.Status.FILA;
            j.lado = null;
            j.posicaoFila = pos++;
        }
    }

    private void entrarEmCampo(Sessao sessao, LadoTime lado) {
        jogadorRepository.primeirosProximosDaFila(sessao.id, POR_TIME)
                .forEach(j -> colocarEmCampo(j, lado));
    }
}
