package pilantras.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import pilantras.domain.entities.Sessao;
import pilantras.repository.SessaoRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@ApplicationScoped
public class SessaoService {

    private static final Duration LIMITE = Duration.ofHours(12);
    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");

    private final SessaoRepository repository;

    public SessaoService(SessaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Sessao obterOuCriar() {
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

    public Sessao buscarAtiva() {
        return repository.buscarAtiva()
                .orElseThrow(() -> new NotFoundException("Nenhuma sessão ativa"));
    }

    private boolean expirada(Sessao s) {
        return Duration.between(
                s.criadoEm, LocalDateTime.now()
        ).compareTo(LIMITE) > 0;
    }

}