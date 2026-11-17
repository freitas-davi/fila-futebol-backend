package pilantras.dto;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String rootCause
) {
}
