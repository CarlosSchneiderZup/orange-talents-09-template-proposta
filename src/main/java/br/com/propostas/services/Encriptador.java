package br.com.propostas.services;

import br.com.propostas.commons.exceptions.ApiErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
public class Encriptador {

    private static String password = "apiPropostaEncoder";
    private static String salt = "646f63756d656e74";

    private static SecretKey gerarSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    private static IvParameterSpec gerarIv() {
        byte[] iv = new byte[16];
        return new IvParameterSpec(iv);
    }

    public static String encriptar(String stringParaEncriptar) {
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, gerarSecretKey(), gerarIv());
            byte[] textoEncriptado = cipher.doFinal(stringParaEncriptar.getBytes());
            return Base64.getEncoder()
                    .encodeToString(textoEncriptado);
        } catch (Exception e) {
            throw new ApiErrorException("Erro interno de encriptar o documento", "documento", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static String decriptar(String textoEncriptado) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, gerarSecretKey(), gerarIv());
            byte[] textoDecriptado = cipher.doFinal(Base64.getDecoder()
                    .decode(textoEncriptado));
            return new String(textoDecriptado);
        } catch (Exception e) {
            throw new ApiErrorException("Erro interno de encriptar o documento", "documento", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
