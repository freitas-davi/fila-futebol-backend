package pilantras.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.enums.Categoria;
import pilantras.dto.AddJogadorRequestDTO;
import pilantras.dto.JogadorResponseDTO;
import pilantras.dto.PartidaResponseDTO;
import pilantras.service.FilaService;

import java.util.List;

@Path("/sessoes/{sessaoId}/jogadores")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FilaController {

    private final FilaService service;

    public FilaController(FilaService service) {
        this.service = service;
    }

    @POST
    @Operation(summary = "Adicionar jogador", description = "Adiciona um jogador à lista da sessão.")
    public Response adicionar(@PathParam("sessaoId") Long sessaoId,
                              @Valid AddJogadorRequestDTO req) {
        JogadorSessao j = service.adicionarJogadorLista(sessaoId, req);
        return Response.status(Response.Status.CREATED)
                .entity(JogadorResponseDTO.from(j)).build();
    }

    @POST
    @Path("/iniciar")
    @Operation(summary = "Adicionar jogadores e sortear",
            description = "Adiciona a lista de jogadores e sorteia os 10 primeiros com categoria marcada.")
    public Response adicionarEmLote(@PathParam("sessaoId") Long sessaoId,
                                    @Valid @NotEmpty List<@Valid AddJogadorRequestDTO> jogadores)
    {
        PartidaResponseDTO partida = service.adicionarJogadoresEIniciar(sessaoId, jogadores);
        return Response.status(Response.Status.CREATED).entity(partida).build();
    }

    @GET
    @Operation(summary = "Listar jogadores", description = "Retorna os jogadores da sessão.")
    public List<JogadorResponseDTO> listar(@PathParam("sessaoId") Long sessaoId) {
        return service.listarJogadores(sessaoId)
                .stream().map(JogadorResponseDTO::from)
                .toList();
    }

    @DELETE
    @Path("/{jogadorId}")
    @Operation(summary = "Remover jogador", description = "Remove um jogador da lista da sessão.")
    public Response remover(@PathParam("sessaoId") Long sessaoId,
                            @PathParam("jogadorId") Long jogadorId) {
        service.removerJogadorLista(sessaoId, jogadorId);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{jogadorId}")
    @Operation(summary = "Atualizar categoria", description = "Altera a categoria de um jogador da sessão.")
    public JogadorResponseDTO atualizarCategoria(@PathParam("sessaoId") Long sessaoId,
                                                 @PathParam("jogadorId") Long jogadorId,
                                                 Categoria categoria) {
        return JogadorResponseDTO.from(
                service.atualizarCategoria(sessaoId, jogadorId, categoria)
        );
    }

}