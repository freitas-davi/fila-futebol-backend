package pilantras.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pilantras.domain.entities.JogadorSessao;
import pilantras.domain.enums.Categoria;
import pilantras.dto.AddJogadorRequest;
import pilantras.dto.JogadorResponse;
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
                              @Valid AddJogadorRequest req)
    {
        JogadorSessao j = service.adicionar(sessaoId, req);
        return Response.status(Response.Status.CREATED)
                .entity(JogadorResponse.from(j)).build();
    }

    @GET
    public List<JogadorResponse> listar(@PathParam("sessaoId") Long sessaoId) {
        return service.listar(sessaoId)
                .stream().map(JogadorResponse::from)
                .toList();
    }

    @DELETE
    @Path("/{jogadorId}")
    public Response remover(@PathParam("sessaoId") Long sessaoId,
                            @PathParam("jogadorId") Long jogadorId) {
        service.remover(sessaoId, jogadorId);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{jogadorId}")
    public JogadorResponse atualizarCategoria(@PathParam("sessaoId") Long sessaoId,
                                              @PathParam("jogadorId") Long jogadorId,
                                              Categoria categoria) {
        return JogadorResponse.from(
                service.atualizarCategoria(
                        sessaoId, jogadorId, categoria)
        );
    }

}