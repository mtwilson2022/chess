package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String playerName, Session session) {
        var connection = new Connection(playerName, session);

        if (connections.get(gameID) == null) {
            var map = new ConcurrentHashMap<String, Connection>();
            map.put(playerName, connection);
            connections.put(gameID, map);
        }
        else {
            connections.get(gameID).put(playerName, connection);
        }
    }

    public void remove(Integer gameID, String playerName) {
        connections.get(gameID).remove(playerName);
        if (connections.get(gameID) == null) {
            connections.remove(gameID);
        }
    }

    public void broadcastToAll(Integer gameID, ServerMessage msg) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID).values()) {
            if (c.session.isOpen()) {
                c.send(serialize(msg));
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c.playerName);
        }
    }

    public void broadcastToOthers(Integer gameID, String excludePlayerName, ServerMessage msg) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID).values()) {
            if (c.session.isOpen()) {
                if (!c.playerName.equals(excludePlayerName)) {
                    c.send(serialize(msg));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c.playerName);
        }
    }

    public void broadcastToRoot(Integer gameID, String rootUsername, ServerMessage msg) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID).values()) {
            if (c.session.isOpen()) {
                if (c.playerName.equals(rootUsername)) {
                    c.send(serialize(msg));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c.playerName);
        }
    }

    private String serialize(ServerMessage msg) {
        var gson = new Gson();
        return gson.toJson(msg);
    }
}