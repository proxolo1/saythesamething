package in.saythesamething.repo;

import in.saythesamething.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface GameRepository extends MongoRepository<Game, String> {
    Optional<Game> findByGameId(String gameId);
}
