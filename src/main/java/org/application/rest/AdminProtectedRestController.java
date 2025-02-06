package org.application.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class AdminProtectedRestController {

   @GetMapping("/hiddenmessage")
   public ResponseEntity<HiddenMessage> getAdminProtectedGreeting() {
      return ResponseEntity.ok(new HiddenMessage("this is a hidden message!"));
   }

   @GetMapping("/errormessage")
   public void redirectErrorMsg(@RequestParam("error") String error,
                                HttpServletResponse response) throws IOException {
      String encodedError = URLEncoder.encode(error, StandardCharsets.UTF_8.toString());
      response.sendRedirect("/index.html?error=" + encodedError);
   }
   private static class HiddenMessage {

      private final String message;

      private HiddenMessage(String message) {
         this.message = message;
      }

      public String getMessage() {
         return message;
      }
   }

}
