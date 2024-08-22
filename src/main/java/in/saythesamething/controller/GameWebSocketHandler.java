package in.saythesamething.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.saythesamething.config.WebSocketConfig;
import in.saythesamething.model.Game;
import in.saythesamething.repo.GameRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, Map<String, WebSocketSession>> gameSessions = new HashMap<>();  // Maps gameId to players
    private final Map<String, Map<String, String>> playerGuesses = new HashMap<>();  // Maps gameId to player guesses

    @Autowired
    private GameRepository gameRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private WebSocketSession realTimeSession;
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);
        String gameId = jsonNode.get("gameId").asText();

        gameSessions.putIfAbsent(gameId, new HashMap<>());
        playerGuesses.putIfAbsent(gameId, new HashMap<>());

        if (payload.contains("connect_player1")) {
            connectPlayer(session, "player1", gameId);
        } else if (payload.contains("connect_player2")) {
            connectPlayer(session, "player2", gameId);
        } else if (payload.contains("guess_player1")) {
            String guess = jsonNode.get("guess_player1").asText();
            if (realTimeSession != null ) {
                realTimeSession.sendMessage(new TextMessage(guess));
            }
            handleGuess(session, guess, "player1", gameId);
        } else if (payload.contains("guess_player2")) {
            String guess = jsonNode.get("guess_player2").asText();
            if (realTimeSession != null ) {
                realTimeSession.sendMessage(new TextMessage(guess));
            }
            handleGuess(session, guess, "player2", gameId);
        } else if (payload.contains("fetch_data")) {
            Game game = gameRepository.findByGameId(gameId).get();
            realTimeSession = session;
            session.sendMessage(new TextMessage(game.getPlayer1Guess().toString()));
            session.sendMessage(new TextMessage(game.getPlayer2Guess().toString()));
        }
    }


    private void connectPlayer(WebSocketSession session, String player, String gameId) throws Exception {
        if (gameSessions.get(gameId).containsKey(player)) {
            session.sendMessage(new TextMessage("Session for " + player + " already exists"));
            session.close();
            return;
        }
        gameSessions.get(gameId).put(player, session);
        session.sendMessage(new TextMessage("Connected as " + player));

        if (player.equals("player1")) {
            session.sendMessage(new TextMessage("Waiting for Player 2..."));
        } else {
            notifyPlayer1GameStart(gameId);
        }
    }

    private void notifyPlayer1GameStart(String gameId) throws Exception {
        WebSocketSession player1Session = gameSessions.get(gameId).get("player1");
        if (player1Session != null) {
            player1Session.sendMessage(new TextMessage("Player 2 has connected. You can start guessing!"));
        }
    }

    private void handleGuess(WebSocketSession session, String guess, String player, String gameId) throws Exception {
        playerGuesses.get(gameId).put(player, guess);

        // Store guess in MongoDB
        saveGuessToMongoDB(gameId, player, guess);

        // Notify the other player
        String otherPlayer = player.equals("player1") ? "player2" : "player1";
        WebSocketSession otherPlayerSession = gameSessions.get(gameId).get(otherPlayer);

        if (otherPlayerSession != null) {
            otherPlayerSession.sendMessage(new TextMessage(player + " guessed: " + guess));
        }

        // Check guesses
        if (playerGuesses.get(gameId).containsKey("player1") && playerGuesses.get(gameId).containsKey("player2")) {
            checkGuesses(gameId);
        }
    }

    private void saveGuessToMongoDB(String gameId, String player, String guess) {
        Game game = gameRepository.findByGameId(gameId).orElse(new Game());
        game.setGameId(gameId);

        if (player.equals("player1")) {
            game.getPlayer1Guess().add(guess);
        } else {
            game.getPlayer2Guess().add(guess);
        }

        gameRepository.save(game);
    }

    private void checkGuesses(String gameId) throws Exception {
        String player1Guess = playerGuesses.get(gameId).get("player1");
        String player2Guess = playerGuesses.get(gameId).get("player2");

        WebSocketSession player1Session = gameSessions.get(gameId).get("player1");
        WebSocketSession player2Session = gameSessions.get(gameId).get("player2");

        if (player1Guess.equalsIgnoreCase(player2Guess)) {
            if (player1Session != null) {
                player1Session.sendMessage(new TextMessage("Both guesses match! You win!"));
            }
            if (player2Session != null) {
                player2Session.sendMessage(new TextMessage("Both guesses match! You win!"));
            }
        } else {
            if (player1Session != null) {
                player1Session.sendMessage(new TextMessage("Guesses do not match. Keep guessing!"));
            }
            if (player2Session != null) {
                player2Session.sendMessage(new TextMessage("Guesses do not match. Keep guessing!"));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        gameSessions.values().forEach(map -> map.values().remove(session));
        playerGuesses.values().forEach(map -> map.values().remove(session));
    }
}
