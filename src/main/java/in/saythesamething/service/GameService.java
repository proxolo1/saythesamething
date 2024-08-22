package in.saythesamething.service;

import in.saythesamething.model.GameSession;
import in.saythesamething.repo.GameSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    @Autowired
    private GameSessionRepository gameSessionRepository;

    public GameSession createToken(String token) {
        GameSession gameSession = new GameSession();
        gameSession.setToken(token);
        return gameSessionRepository.save(gameSession);
    }
}
