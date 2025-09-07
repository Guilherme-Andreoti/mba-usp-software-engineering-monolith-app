package mba.usp.monolith.domain.repositories;

import mba.usp.monolith.processing.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SensorDataRepository extends MongoRepository<SensorData, String> {
    List<SensorData> findByDateTimeBetween(Instant start, Instant end);
}