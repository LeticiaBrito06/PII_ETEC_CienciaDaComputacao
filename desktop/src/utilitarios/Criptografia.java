package utilitarios;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Criptografia {
    private static final String ALGORITHM = "SHA-256";

    private Criptografia() {}

    public static String gerarSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte [16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String criptografar(String senha, String salt){
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("O campo de senha deve ser preenchido");
        }
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(senha.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar a senha", e);
        }
    }
}
