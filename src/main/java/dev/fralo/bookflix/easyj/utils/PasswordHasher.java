package dev.fralo.bookflix.easyj.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String DELIMITER = ":";

    /**
     * Hashes a password using SHA-256 with a random salt.
     * @param password The password to hash
     * @return A string containing the salt and hash, separated by a delimiter
     */
    public String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Create hash
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert both salt and hash to Base64 for storage
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashString = Base64.getEncoder().encodeToString(hash);

            // Combine salt and hash with delimiter
            return saltString + DELIMITER + hashString;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password against a stored hash.
     * @param password The password to verify
     * @param storedHash The stored hash to verify against (including salt)
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String password, String storedHash) {
        try {
            // Split stored hash into salt and hash components
            String[] parts = storedHash.split(DELIMITER);
            if (parts.length != 2) {
                return false;
            }

            // Decode the stored salt and hash
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] originalHash = Base64.getDecoder().decode(parts[1]);

            // Generate new hash with the same salt
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(salt);
            byte[] newHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Compare the hashes
            return MessageDigest.isEqual(originalHash, newHash);
        } catch (Exception e) {
            return false;
        }
    }
}