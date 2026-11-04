package pilantras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pilantras.domain.enums.Categoria;

public record AddJogadorRequestDTO(

        @NotBlank
        @Size(max = 60)
        String nome,

        Categoria categoria

) { }
