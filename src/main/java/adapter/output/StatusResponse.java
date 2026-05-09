package adapter.output;

public record StatusResponse(
    String processId,
    Status status,
    String message
) {
    public enum Status {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
}