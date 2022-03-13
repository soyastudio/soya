package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.CommandOption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public abstract class AESCommand extends ResourceCommand {

    public static final String DIGEST_ALGORITHM_MD5 = "MD5";
    public static final String DIGEST_ALGORITHM_SHA1 = "SHA-1";
    public static final String DIGEST_ALGORITHM_SHA256 = "SHA-256";

    public static final String[] ALGORITHMS = {
            "AES/CBC/PKCS5Padding",
            "AES/ECB/PKCS5Padding",
            "DES/CBC/PKCS5Padding",
            "DES/ECB/PKCS5Padding",
            "DESede/CBC/PKCS5Padding",
            "DESede/ECB/PKCS5Padding",
            "RSA/ECB/PKCS1Padding",
            "RSA/ECB/OAEPWithSHA-1AndMGF1Padding",
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    };

    @CommandOption(option = "k", longOption = "secret", required = true)
    protected String secret;

    protected String encrypt(String message, String secret) throws Exception {

        MessageDigest sha = null;
        byte[] key;
        SecretKeySpec secretKey = setKey(secret);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
    }

    protected String decrypt(String strToDecrypt, String secret) throws Exception {

        MessageDigest sha = null;
        byte[] key;
        SecretKeySpec secretKey = setKey(secret);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }

    private SecretKeySpec setKey(String secret) throws Exception {
        MessageDigest sha = null;
        byte[] key;
        SecretKeySpec secretKey;

        key = secret.getBytes("UTF-8");
        sha = MessageDigest.getInstance(DIGEST_ALGORITHM_SHA1);
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        secretKey = new SecretKeySpec(key, "AES");

        return secretKey;
    }


}
