package pilantras.dto;

import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.enums.Categoria;
import pilantras.domain.enums.LadoTime;

public record JogadorResponse(

        String nome,

        Categoria categoria,

        JogadorSessao.Status status,

        LadoTime lado,

        Integer posicaoFila
) {

    public static JogadorResponse from(JogadorSessao j) {
        return new JogadorResponse(
                j.nome, j.categoria, j.status, j.lado, j.posicaoFila
        );
    }

}
