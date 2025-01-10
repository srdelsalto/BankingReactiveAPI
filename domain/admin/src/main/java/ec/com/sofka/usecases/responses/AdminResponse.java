package ec.com.sofka.usecases.responses;

public class AdminResponse {
    private final String id;
    private final String email;
    private final String password;

    public AdminResponse(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
