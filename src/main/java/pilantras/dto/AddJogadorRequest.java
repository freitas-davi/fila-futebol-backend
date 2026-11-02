package pilantras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pilantras.domain.enums.Categoria;

public record AddJogadorRequest(

        @NotBlank
        @Size(max = 60)
        String nome,

        Categoria categoria

) { }
