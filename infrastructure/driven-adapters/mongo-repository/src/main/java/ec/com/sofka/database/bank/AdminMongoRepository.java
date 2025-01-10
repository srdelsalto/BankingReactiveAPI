package ec.com.sofka.database.bank;

import ec.com.sofka.data.AdminEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AdminMongoRepository extends ReactiveMongoRepository<AdminEntity, String> {
}
