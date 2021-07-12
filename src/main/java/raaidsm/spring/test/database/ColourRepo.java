package raaidsm.spring.test.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ColourRepo extends JpaRepository<ColourEntity, Integer> {
}