package response;

public record LoginResponse(String username, String authToken) implements Response {
}
