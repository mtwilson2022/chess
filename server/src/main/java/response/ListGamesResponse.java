package response;

import model.GameData;

import java.util.List;

public record ListGamesResponse(List<GameData> games) implements Response {
}
