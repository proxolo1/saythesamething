package in.saythesamething.controller;

import in.saythesamething.model.GameSession;
import in.saythesamething.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

@Autowired
    private GameService gameService;

    @GetMapping("/token/{token}")
    public ResponseEntity <GameSession > createToken(@PathVariable String token) {
        GameSession session = gameService.createToken(token);
        return ResponseEntity.ok(session);
    }
}
