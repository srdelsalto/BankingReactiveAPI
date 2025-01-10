package ec.com.sofka.usecases.command;

public class RegisterAdminCommand {
    private final String email;
    private final String password;

    public RegisterAdminCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
