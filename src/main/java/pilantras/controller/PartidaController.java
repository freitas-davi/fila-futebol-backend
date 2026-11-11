package pilantras.controller;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
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
    @Operation(summary = "Iniciar partida", description = "Sorteia os times e inicia uma partida na sessão.")
    public Response iniciar(@PathParam("sessaoId") Long sessaoId) {
        return Response
                .status(Response.Status.CREATED)
                .entity(service.iniciar(sessaoId)).build();
    }

    @POST
    @Path("/resultado")
    @Operation(summary = "Registrar resultado", description = "Registra o resultado e atualiza os times da sessão.")
    public Response registrarResultado(@PathParam("sessaoId") Long sessaoId,
                                       ResultadoPartidaRequestDTO req)
    {
        return Response.ok(
                service.registrarResultado(sessaoId, req)).build();
    }

}
