package request;

public record JoinGameRequest(String authToken, String playerColor, Integer gameID) {
    public JoinGameRequest setAuthToken(String token) {
        return new JoinGameRequest(token, playerColor(), gameID());
    }
}
