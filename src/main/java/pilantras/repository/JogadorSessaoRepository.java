package pilantras.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.enums.LadoTime;

import java.util.List;

@ApplicationScoped
public class JogadorSessaoRepository implements PanacheRepository<JogadorSessao> {

    public int proximaPosicao(Long sessaoId) {
        Integer max = getEntityManager().createQuery(
                        "select max(j.posicaoFila) from JogadorSessao j where j.sessao.id = :id",
                        Integer.class)
                .setParameter("id", sessaoId)
                .getSingleResult();

        return max == null
                ? 1
                : max + 1;
    }

    public List<JogadorSessao> listarPorSessao(Long sessaoId) {
        return list(
                "sessao.id = ?1 order by posicaoFila",
                sessaoId
        );
    }

    public List<JogadorSessao> primeirosDaFilaComCategoria(Long sessaoId, int limite) {
        return find(
                "sessao.id = ?1 and status = ?2 and categoria is not null order by posicaoFila",
                sessaoId, JogadorSessao.Status.FILA)
                .page(0, limite)
                .list();
    }

    public List<JogadorSessao> primeirosProximosDaFila(Long sessaoId, int limite) {
        return find("sessao.id = ?1 and status = ?2 order by posicaoFila",
                sessaoId, JogadorSessao.Status.FILA).page(0, limite).list();
    }

    public List<JogadorSessao> naFila(Long sessaoId) {
        return find("sessao.id = ?1 and status = ?2 order by posicaoFila",
                sessaoId, JogadorSessao.Status.FILA).list();
    }

    public List<JogadorSessao> emCampo(Long sessaoId, LadoTime lado) {
        return list(
                "sessao.id = ?1 and status = ?2 and lado = ?3 order by posicaoFila",
                sessaoId, JogadorSessao.Status.JOGANDO, lado);
    }

}
