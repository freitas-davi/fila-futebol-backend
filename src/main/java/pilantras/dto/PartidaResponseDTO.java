package pilantras.dto;

import pilantras.client.Sorteador;
import pilantras.domain.entities.JogadorSessao;

import java.util.List;

public record PartidaResponseDTO(

        Long sessaoId,

        List<JogadorResponseDTO> ladoA,

        List<JogadorResponseDTO> ladoB,

        List<JogadorResponseDTO> proximos

) {

    public static PartidaResponseDTO from(Sorteador.Times t, List<JogadorSessao> fila) {
        return new PartidaResponseDTO(
                t.sessaoId(),
                t.ladoA().stream().map(JogadorResponseDTO::from).toList(),
                t.ladoB().stream().map(JogadorResponseDTO::from).toList(),
                fila.stream().map(JogadorResponseDTO::from).toList()
        );
    }

}
