package pilantras.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pilantras.domain.entities.Sessao;

import java.util.Optional;


@ApplicationScoped
public class SessaoRepository implements PanacheRepository<Sessao> {

    public Optional<Sessao> buscarAtiva() {
        return find("status", Sessao.Status.ATIVA).firstResultOptional();
    }

}