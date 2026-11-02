package pilantras.dto;

import pilantras.domain.entities.Sessao;
import java.time.LocalDate;

public record SessaoResponse(

        Long id,

        LocalDate data,

        Sessao.Status status

) {

    public static SessaoResponse from(Sessao s) {
        return new SessaoResponse(
                s.id,
                s.data,
                s.status
        );
    }

}