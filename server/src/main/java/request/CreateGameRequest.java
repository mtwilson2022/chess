package request;

public record CreateGameRequest(String authToken, String gameName) {
    public CreateGameRequest setAuthToken(String token) {
        return new CreateGameRequest(token, gameName());
    }
}
