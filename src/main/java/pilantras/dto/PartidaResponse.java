package pilantras.dto;

import pilantras.client.Sorteador;
import pilantras.domain.entities.Partida;

import java.util.List;

public record PartidaResponse(

        Long id,

        Integer numero,

        Partida.Status status,

        List<JogadorResponse> ladoA,

        List<JogadorResponse> ladoB

) {

    public static PartidaResponse from(Partida p, Sorteador.Times t) {
        return new PartidaResponse(p.id, p.numero, p.status,
                t.ladoA().stream().map(JogadorResponse::from).toList(),
                t.ladoB().stream().map(JogadorResponse::from).toList());
    }

}
