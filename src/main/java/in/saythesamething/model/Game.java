package in.saythesamething.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "games")
public class Game {
    @Id
    private String id;
    private String gameId;
    private List<String> player1Guess = new ArrayList<>();
    private List<String> player2Guess = new ArrayList<>();

    // Constructors, getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public List<String> getPlayer1Guess() {
        return player1Guess;
    }

    public void setPlayer1Guess(List<String> player1Guess) {
        this.player1Guess = player1Guess;
    }

    public List<String> getPlayer2Guess() {
        return player2Guess;
    }

    public void setPlayer2Guess(List<String> player2Guess) {
        this.player2Guess = player2Guess;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", gameId='" + gameId + '\'' +
                ", player1Guess=" + player1Guess +
                ", player2Guess=" + player2Guess +
                '}';
    }
}

