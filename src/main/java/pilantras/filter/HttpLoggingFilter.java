package pilantras.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;

@Provider
@Priority(Priorities.USER)
public class HttpLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(HttpLoggingFilter.class);
    private static final String REQUEST_START = HttpLoggingFilter.class.getName() + ".requestStart";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(REQUEST_START, System.nanoTime());
        LOG.infof("Requisição iniciada: %s %s",
                requestContext.getMethod(), requestContext.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        Long requestStart = (Long) requestContext.getProperty(REQUEST_START);
        String path = requestContext.getUriInfo().getPath();
        int status = responseContext.getStatus();

        if (requestStart == null) {
            String message = "Requisição concluída: %s %s - status %d - duração indisponível";
            if (status >= 400) {
                LOG.warnf(message, requestContext.getMethod(), path, status);
            } else {
                LOG.infof(message, requestContext.getMethod(), path, status);
            }
            return;
        }

        long durationMs = (System.nanoTime() - requestStart) / 1_000_000;
        String message = "Requisição concluída: %s %s - status %d - %d ms";

        if (status >= 400) {
            LOG.warnf(message, requestContext.getMethod(), path, status, durationMs);
            return;
        }

        LOG.infof(message, requestContext.getMethod(), path, status, durationMs);
    }
}
