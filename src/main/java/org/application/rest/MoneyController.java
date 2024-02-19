package org.application.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.application.security.jwt.JWTFilter;
import org.application.security.jwt.TokenProvider;
import org.application.security.model.User;
import org.application.security.repository.UserRepository;
import org.application.security.rest.AuthenticationRestController;
import org.application.security.rest.dto.OrderDto;
import org.application.security.rest.dto.OtpDto;
import org.application.security.rest.dto.SendMoneyDto;
import org.application.security.service.OtpService;
import org.application.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/money")
public class MoneyController {
    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;


    public MoneyController(UserService userService, OtpService otpService, TokenProvider tokenProvider, UserRepository userRepository) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    private User getUser() {
        return userService.getUserWithAuthorities().get();
    }

    private Float getUserAmount() {
        return getUser().getAmount();
    }

    @GetMapping("/amount")
    public ResponseEntity<Float> getAmount() {
        return ResponseEntity.ok(getUserAmount());
    }

    @PostMapping("/send")
    public ResponseEntity<AuthenticationRestController.JWTToken> sendMoney(@Valid @RequestBody SendMoneyDto sendMoneyDto) throws Exception {
        int compare = Float.compare(sendMoneyDto.getAmount(), getUserAmount());

        // check amount for sending
        if (compare >= 0) {
            throw new Exception("You don't have enough money");
        }

        // check exist of user by phone number
        User recipient = findUserByPhoneNumber(sendMoneyDto.getPhoneNumber());
        if (recipient == null) {
            throw new Exception("User didn't find");
        }

        String OTP = Integer.toString(OtpService.generateCode());
        User user = getUser();
        userRepository.updateOTP(user.getId(), OTP);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwt = tokenProvider.addOrderInToken(authentication, createOrder(sendMoneyDto));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new AuthenticationRestController.JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/confirm")
    public ResponseEntity<AuthenticationRestController.JWTToken> confirmSending(@Valid @RequestBody OtpDto otpDto, @RequestHeader(JWTFilter.AUTHORIZATION_HEADER) String jwt) throws Exception {
        User sender = getUser();
        JsonNode body = tokenProvider.getAttributes(jwt);
        JsonNode order = body.get("order");
        Float amount = order.get("amount").floatValue();
        String phoneNumber = order.get("phoneNumber").asText();

        if (sender.getOTP().equals(otpDto.getOtp())) {
            User recipient = findUserByPhoneNumber(phoneNumber);

            // recipient get money, sender lost money
            recipient.setAmount(
                    recipient.getAmount() + amount
            );
            sender.setAmount(
                    sender.getAmount() - amount
            );

            log.info(recipient.getAmount().toString());
            log.info(sender.getAmount().toString());

            userRepository.updateAmount(recipient.getId(), recipient.getAmount());
            userRepository.updateAmount(sender.getId(), sender.getAmount());

            //set null otp
            userRepository.updateOTP(sender.getId(), null);

            // clear token of order
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String newJwt = tokenProvider.deleteOrderInToken(authentication);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + newJwt);

            return new ResponseEntity<>(new AuthenticationRestController.JWTToken(newJwt), httpHeaders, HttpStatus.OK);
        } else {
            throw new Exception("Incorrect OTP");
        }
    }

    public OrderDto createOrder(SendMoneyDto sendMoneyDto) {
        return new OrderDto(
                sendMoneyDto.getPhoneNumber(),
                sendMoneyDto.getAmount(),
                "WAIT"
        );
    }

    public User findUserByPhoneNumber(String phoneNumber) throws Exception {
        try {
            return userRepository.findOneWithAuthoritiesByPhonenumber(phoneNumber).get();
        } catch (Exception e) {
            log.trace("Invalid user's phone number: {}", e);
        }
        return null;
    }
}
