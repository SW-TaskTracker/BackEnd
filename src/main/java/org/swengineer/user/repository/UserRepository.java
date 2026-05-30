package org.swengineer.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swengineer.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRoutinerId(String routinerId);
    boolean existsByRoutinerId(String routinerId);
    //활동중인 유저 찾기
    List<User> findAllByDeletedAtIsNull();
}