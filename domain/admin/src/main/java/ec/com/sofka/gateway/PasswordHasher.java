package ec.com.sofka.gateway;

public interface PasswordHasher {
    String hashPassword(String password);
}
