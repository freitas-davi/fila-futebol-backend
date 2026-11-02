package pilantras.domain.entities;

import jakarta.persistence.*;
import pilantras.domain.enums.Categoria;
import pilantras.domain.enums.LadoTime;


@Entity
@Table(name = "jogador_sessao")
public class JogadorSessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id")
    public Sessao sessao;

    @Column(nullable = false, length = 60)
    public String nome;

    @Enumerated(EnumType.STRING)
    public Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Status status = Status.FILA;

    @Enumerated(EnumType.STRING)
    public LadoTime lado;

    @Column(name = "posicao_fila", nullable = false)
    public Integer posicaoFila;

    public enum Status {
        FILA, JOGANDO
    }

}
