package com.financeapp.repositories;

import com.financeapp.enitities.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Matt on 19/07/2017.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findOneByUsername(String username);
}
