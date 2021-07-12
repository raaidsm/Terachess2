package raaidsm.spring.test.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepo extends JpaRepository<User, Integer> {
}