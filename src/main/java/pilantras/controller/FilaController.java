package pilantras.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.enums.Categoria;
import pilantras.dto.AddJogadorRequestDTO;
import pilantras.dto.JogadorResponseDTO;
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
    public Response adicionar(@PathParam("sessaoId") Long sessaoId,
                              @Valid AddJogadorRequestDTO req) {
        JogadorSessao j = service.adicionarJogadorLista(sessaoId, req);
        return Response.status(Response.Status.CREATED)
                .entity(JogadorResponseDTO.from(j)).build();
    }

    @GET
    public List<JogadorResponseDTO> listar(@PathParam("sessaoId") Long sessaoId) {
        return service.listarJogadores(sessaoId)
                .stream().map(JogadorResponseDTO::from)
                .toList();
    }

    @DELETE
    @Path("/{jogadorId}")
    public Response remover(@PathParam("sessaoId") Long sessaoId,
                            @PathParam("jogadorId") Long jogadorId) {
        service.removerJogadorLista(sessaoId, jogadorId);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{jogadorId}")
    public JogadorResponseDTO atualizarCategoria(@PathParam("sessaoId") Long sessaoId,
                                                 @PathParam("jogadorId") Long jogadorId,
                                                 Categoria categoria) {
        return JogadorResponseDTO.from(
                service.atualizarCategoria(sessaoId, jogadorId, categoria)
        );
    }

}