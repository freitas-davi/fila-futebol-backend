package pilantras.controller;

import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import pilantras.dto.SessaoResponse;
import pilantras.service.SessaoService;

@Path("/sessoes")
@Produces(MediaType.APPLICATION_JSON)
public class SessaoController {

    private final SessaoService service;

    public SessaoController(SessaoService service) {
        this.service = service;
    }

    @POST
    public SessaoResponse criar() {
        return SessaoResponse.from(service.obterOuCriar());
    }

    @GET
    @Path("/ativa")
    public SessaoResponse ativa() {
        return SessaoResponse.from(service.buscarAtiva());
    }
}