package ec.com.sofka.usecases.responses;

public class AdminResponse {
    private final String id;
    private final String email;
    private final String token;
    private final String documentId;

    public AdminResponse(String id, String email, String token, String documentId) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.documentId = documentId;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }
    public String getDocumentId() { return documentId; }
}
