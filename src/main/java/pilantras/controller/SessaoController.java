package pilantras.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import pilantras.dto.PartidaResponseDTO;
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
    @Operation(summary = "Criar ou obter sessão", description = "Cria uma sessão ativa ou retorna a sessão ativa existente.")
    public SessaoResponseDTO criarOuObter() {
        return SessaoResponseDTO.from(service.obterOuCriarSessao());
    }

    @GET
    @Path("/ativa")
    @Operation(summary = "Consultar sessão ativa", description = "Retorna a sessão ativa atual.")
    public SessaoResponseDTO ativa() {
        return SessaoResponseDTO.from(service.buscarAtiva());
    }

    @GET
    @Path("/{sessaoId}/estado-times")
    @Operation(summary = "Consultar estado dos times", description = "Retorna os times e a fila da sessão.")
    public PartidaResponseDTO estadoTimes(@PathParam("sessaoId") Long sessaoId) {
        return service.estadoTimesSessaoAtual(sessaoId);
    }

}