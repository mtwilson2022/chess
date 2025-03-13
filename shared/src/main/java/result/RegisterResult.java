package result;

public record RegisterResult(String username, String authToken) implements Result {
}
