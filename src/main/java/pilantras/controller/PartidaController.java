package pilantras.controller;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pilantras.dto.ResultadoPartidaRequestDTO;
import pilantras.service.PartidaService;


@Path("/sessoes/{sessaoId}/partidas")
@Produces(MediaType.APPLICATION_JSON)
public class PartidaController {

    private final PartidaService service;

    public PartidaController(PartidaService service) {
        this.service = service;
    }

    @POST
    @Path("/iniciar")
    public Response iniciar(@PathParam("sessaoId") Long sessaoId) {
        return Response
                .status(Response.Status.CREATED)
                .entity(service.iniciar(sessaoId)).build();
    }

    @POST
    @Path("/{partidaId}/resultado")
    public Response registrarResultado(@PathParam("sessaoId") Long sessaoId,
                                       @PathParam("partidaId") Long partidaId,
                                       ResultadoPartidaRequestDTO req)
    {
        return Response.ok(
                service.registrarResultado(sessaoId, partidaId, req)
        ).build();
    }

}
