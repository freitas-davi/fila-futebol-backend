package pilantras.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import pilantras.client.Sorteador;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.entities.Sessao;
import pilantras.domain.enums.LadoTime;
import pilantras.dto.PartidaResponseDTO;
import pilantras.repository.JogadorSessaoRepository;
import pilantras.repository.SessaoRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SessaoService {

    private static final Duration LIMITE = Duration.ofHours(12);
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private final SessaoRepository repository;
    private final JogadorSessaoRepository jogadorRepository;

    public SessaoService(SessaoRepository repository, JogadorSessaoRepository jogadorRepository) {
        this.repository = repository;
        this.jogadorRepository = jogadorRepository;
    }

    @Transactional
    public Sessao obterOuCriarSessao() {
        Optional<Sessao> ativa = repository.buscarAtiva();

        if (ativa.isPresent()) {
            if (!expirada(ativa.get())) {
                return ativa.get();
            }
            ativa.get().status = Sessao.Status.ENCERRADA;
            repository.flush();
        }
        Sessao s = new Sessao();
        s.data = LocalDate.now(ZONA);
        s.status = Sessao.Status.ATIVA;
        repository.persist(s);
        return s;
    }

    public PartidaResponseDTO estadoTimesSessaoAtual(Long sessaoId) {
        List<JogadorSessao> ladoA = jogadorRepository.emCampo(sessaoId, LadoTime.A);
        List<JogadorSessao> ladoB = jogadorRepository.emCampo(sessaoId, LadoTime.B);
        List<JogadorSessao> fila = jogadorRepository.naFila(sessaoId);

        return PartidaResponseDTO.from(
                new Sorteador.Times(ladoA, ladoB), fila);
    }

    public Sessao buscarAtiva() {
        return repository.buscarAtiva()
                .orElseThrow(() -> new NotFoundException("Nenhuma sessão ativa"));
    }

    public Sessao buscarSessaoAtivaPorId(Long sessaoId) {
        return repository.findByIdOptional(sessaoId)
                .filter(s -> s.status == Sessao.Status.ATIVA)
                .orElseThrow(() -> new NotFoundException("Sessão ativa não encontrada"));
    }

    private boolean expirada(Sessao s) {
        return Duration.between(
                s.criadoEm, LocalDateTime.now()
        ).compareTo(LIMITE) > 0;
    }

}