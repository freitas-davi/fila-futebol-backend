package pilantras.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pilantras.domain.entities.Partida;

import java.util.Optional;


@ApplicationScoped
public class PartidaRepository implements PanacheRepository<Partida> {

    public Optional<Partida> buscarEmAndamento(Long sessaoId) {
        return find(
                "sessao.id = ?1 and status = ?2",
                sessaoId,
                Partida.Status.EM_ANDAMENTO).firstResultOptional();
    }

    public int proximoNumero(Long sessaoId) {
        Integer max = getEntityManager()
                .createQuery("select max(p.numero) from Partida p where p.sessao.id = :id", Integer.class)
                .setParameter("id", sessaoId)
                .getSingleResult();

        return max == null ? 1 : max + 1;
    }

}