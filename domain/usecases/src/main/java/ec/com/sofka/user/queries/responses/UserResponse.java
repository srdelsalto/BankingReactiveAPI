package ec.com.sofka.user.queries.responses;

public class UserResponse {
    private final String id;
    private final String name;
    private final String documentId;

    public UserResponse(String id, String name, String documentId) {
        this.id = id;
        this.name = name;
        this.documentId = documentId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDocumentId() {
        return documentId;
    }
}
