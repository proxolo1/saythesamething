package in.saythesamething.repo;
import in.saythesamething.model.GameSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionRepository extends MongoRepository<GameSession, String> {
}
