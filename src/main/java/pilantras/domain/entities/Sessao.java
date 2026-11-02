package pilantras.domain.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Audited.Table(name = "sessao")
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status;

    @Column(name = "criado_em", nullable = false)
    public LocalDateTime criadoEm = LocalDateTime.now();

    public enum Status {
        ATIVA, ENCERRADA
    }

}