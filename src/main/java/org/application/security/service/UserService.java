package org.application.security.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.application.security.SecurityUtils;
import org.application.security.model.User;
import org.application.security.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional
public class UserService {

   private final UserRepository userRepository;

   public UserService(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   @Transactional(readOnly = true)
   public Optional<User> getUserWithAuthorities() {
      return SecurityUtils.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
   }
//   @Transactional(readOnly = true)
   public Optional<User> updateUser(User user) {
      user.setId(getUserWithAuthorities().get().getId());
      userRepository.updateFirstname(user.getId(), user.getFirstname());
      return getUserWithAuthorities();
   }
}
