package com.labquest.backend.repository;

import com.labquest.backend.entity.UserEntity;
import com.labquest.backend.entity.enums.UserType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmailIgnoreCaseAndAtivoTrue(String email);

    List<UserEntity> findByTipoAndAtivoTrueOrderByNomeAsc(UserType tipo);
}
