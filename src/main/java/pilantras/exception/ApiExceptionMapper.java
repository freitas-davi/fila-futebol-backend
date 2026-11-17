package pilantras.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import pilantras.dto.ApiErrorResponse;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(ApiExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        int status = exception instanceof WebApplicationException webException
                ? webException.getResponse().getStatus()
                : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        Throwable rootCause = rootCauseOf(exception);
        String rootCauseMessage = messageOf(rootCause);
        String message = messageOf(exception);

        if (status >= Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            LOG.errorf(exception, "Erro ao processar requisição. Causa raiz: %s", rootCauseMessage);
        } else {
            LOG.warnf("Requisição rejeitada. status=%d erro=%s causaRaiz=%s",
                    status, message, rootCauseMessage);
        }

        return Response.status(status)
                .entity(new ApiErrorResponse(
                        status,
                        exception.getClass().getSimpleName(),
                        message,
                        rootCauseMessage))
                .build();
    }

    private static Throwable rootCauseOf(Throwable exception) {
        Throwable rootCause = exception;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    private static String messageOf(Throwable exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank()
                ? exception.getClass().getSimpleName()
                : message;
    }
}
