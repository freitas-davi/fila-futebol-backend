package pilantras.controller;

import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import pilantras.dto.SessaoResponseDTO;
import pilantras.service.SessaoService;

@Path("/sessoes")
@Produces(MediaType.APPLICATION_JSON)
public class SessaoController {

    private final SessaoService service;

    public SessaoController(SessaoService service) {
        this.service = service;
    }

    @POST
    public SessaoResponseDTO criar() {
        return SessaoResponseDTO.from(service.obterOuCriarSessao());
    }

    @GET
    @Path("/ativa")
    public SessaoResponseDTO ativa() {
        return SessaoResponseDTO.from(service.buscarAtiva());
    }
}