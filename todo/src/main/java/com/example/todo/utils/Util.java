package com.example.todo.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Locale;

@Slf4j
public class Util {
    public static PrivateKey getPrivateKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path path = Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath() + filename);
        String privateKeyPem = new String(Files.readAllBytes(path));
        privateKeyPem = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");
        byte[] bytesKey = privateKeyPem.getBytes();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(bytesKey));
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey getPublicKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path path = Paths.get(FileSystems.getDefault().getPath("").toAbsolutePath() + filename);
        String publicKeyPem = new String(Files.readAllBytes(path));
        publicKeyPem = publicKeyPem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");
        byte[] bytesKey = publicKeyPem.getBytes();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(bytesKey));
        return keyFactory.generatePublic(keySpec);
    }

    public static String deCryptRSA(String filename, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher decrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        decrypt.init(Cipher.DECRYPT_MODE, getPrivateKey(filename));
        return new String(decrypt.doFinal(Base64.getDecoder().decode(data)), StandardCharsets.UTF_8);
    }

    public static String enCryptRSA(String filename, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encrypt.init(Cipher.ENCRYPT_MODE, getPublicKey(filename));
        return Base64.getEncoder().encodeToString(encrypt.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
