package pilantras.dto;

import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.enums.Categoria;
import pilantras.domain.enums.LadoTime;

public record JogadorResponseDTO(

        String nome,

        Categoria categoria,

        JogadorSessao.Status status,

        LadoTime lado,

        Integer posicaoFila
) {

    public static JogadorResponseDTO from(JogadorSessao j) {
        return new JogadorResponseDTO(
                j.nome, j.categoria, j.status, j.lado, j.posicaoFila
        );
    }

}
