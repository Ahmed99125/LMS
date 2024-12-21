package AdvSe.LMS.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingOTP {
    public static Integer hashOTP(Integer originalOTP, String studentId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(studentId.getBytes(StandardCharsets.UTF_8));

            int hashCode = 0;
            for (int i = 0; i < 4; i++) {
                hashCode = (hashCode << 8) | (hashBytes[i] & 0xFF);
            }

            hashCode = 100000 + (Math.abs(hashCode) % 400000); // Range: 100000-499999

            return originalOTP + hashCode;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found", e);
        }
    }
}
