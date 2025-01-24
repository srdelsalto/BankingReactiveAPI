package ec.com.sofka.dto;

import ec.com.sofka.ROLE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AdminRequestDTO {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be correct format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&.#])[A-Za-z\\d@$!%*?&.#]{8,}$",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Role cannot be blank")
    @Pattern(regexp = "ADMIN|USER|GOD", message = "Invalid role")
    private String role;

    @NotBlank(message = "Document ID cannot be blank")
    @Pattern(
            regexp = "^[0-9]{10,}$",
            message = "The field must contain only numbers and be at least 10 digits long"
    )
    private String documentId;

    public AdminRequestDTO(String email, String password, String role, String documentId) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.documentId = documentId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
    public String getDocumentId() { return documentId; }
}
