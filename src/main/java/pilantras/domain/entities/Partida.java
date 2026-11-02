package pilantras.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "partida")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id")
    public Sessao sessao;

    @Column(nullable = false)
    public Integer numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status = Status.EM_ANDAMENTO;

    @Enumerated(EnumType.STRING)
    public Resultado resultado;

    @Column(name = "iniciada_em", nullable = false)
    public LocalDateTime iniciadaEm = LocalDateTime.now();

    @Column(name = "finalizada_em")
    public LocalDateTime finalizadaEm;

    public enum Status {
        EM_ANDAMENTO,
        FINALIZADA
    }

    public enum Resultado {
        LADO_A,
        LADO_B,
        EMPATE
    }

}