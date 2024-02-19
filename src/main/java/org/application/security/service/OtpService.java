package org.application.security.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
public class OtpService {
    public static int generateCode() {
        long currentTime = System.currentTimeMillis();
        int randomSeed = (int) (currentTime % 10000);
        Random random = new Random(randomSeed);
        return random.nextInt(9000) + 1000;
    }
}
