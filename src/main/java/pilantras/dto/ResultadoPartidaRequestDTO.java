package pilantras.dto;

import jakarta.validation.constraints.NotNull;
import pilantras.domain.entities.Partida;

public record ResultadoPartidaRequestDTO(

        @NotNull
        Partida.Resultado resultado

) { }
