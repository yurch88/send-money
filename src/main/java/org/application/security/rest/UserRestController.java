package org.application.security.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.application.security.model.User;
import org.application.security.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

   private final UserService userService;

   public UserRestController(UserService userService) {
      this.userService = userService;
   }

   @GetMapping("/get")
   public ResponseEntity<User> getActualUser() {
      return ResponseEntity.ok(userService.getUserWithAuthorities().get());
   }

   @PutMapping("/update")
   public ResponseEntity<User> updateUser(@RequestBody User user) {
      return ResponseEntity.ok(userService.updateUser(user).get());
   }

}
