package org.application.security.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.application.security.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

   @EntityGraph(attributePaths = "authorities")
   Optional<User> findOneWithAuthoritiesByUsername(String username);

   @EntityGraph(attributePaths = "authorities")
   Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

   @EntityGraph(attributePaths = "authorities")
   Optional<User> findOneWithAuthoritiesByPhonenumber(String phoneNumber);

   @Transactional
   @Modifying
   @Query("UPDATE User u SET u.OTP = :otp WHERE u.id = :id")
   void updateOTP(@Param("id") Long userId, @Param("otp") String otp);

   @Transactional
   @Modifying
   @Query("UPDATE User u SET u.amount = :amount WHERE u.id = :id")
   void updateAmount(@Param("id") Long userId, @Param("amount") Float amount);
}
