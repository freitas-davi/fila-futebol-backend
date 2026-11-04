package pilantras.dto;

import pilantras.domain.entities.Sessao;
import java.time.LocalDate;

public record SessaoResponseDTO(

        Long id,

        LocalDate data,

        Sessao.Status status

) {

    public static SessaoResponseDTO from(Sessao s) {
        return new SessaoResponseDTO(
                s.id,
                s.data,
                s.status
        );
    }

}