package pilantras.dto;

import pilantras.client.Sorteador;
import pilantras.domain.entities.Partida;

import java.util.List;

public record PartidaResponseDTO(

        Long id,

        Integer numero,

        Partida.Status status,

        List<JogadorResponseDTO> ladoA,

        List<JogadorResponseDTO> ladoB

) {

    public static PartidaResponseDTO from(Partida p, Sorteador.Times t) {
        return new PartidaResponseDTO(p.id, p.numero, p.status,
                t.ladoA().stream().map(JogadorResponseDTO::from).toList(),
                t.ladoB().stream().map(JogadorResponseDTO::from).toList());
    }

}
