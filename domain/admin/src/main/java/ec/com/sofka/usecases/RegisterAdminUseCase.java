package ec.com.sofka.usecases;

import ec.com.sofka.AdminDTO;
import ec.com.sofka.gateway.AdminRepository;
import ec.com.sofka.gateway.PasswordHasher;
import ec.com.sofka.usecases.command.RegisterAdminCommand;
import ec.com.sofka.usecases.responses.AdminResponse;
import reactor.core.publisher.Mono;

public class RegisterAdminUseCase {
    private final AdminRepository adminRepository;
    private final PasswordHasher passwordHasher;

    public RegisterAdminUseCase(AdminRepository adminRepository, PasswordHasher passwordHasher) {
        this.adminRepository = adminRepository;
        this.passwordHasher = passwordHasher;
    }

    public Mono<AdminResponse> execute(RegisterAdminCommand registerAdminCommand) {
        String hashedPassword = passwordHasher.hashPassword(registerAdminCommand.getPassword());
        return adminRepository.save(new AdminDTO(registerAdminCommand.getEmail(), hashedPassword))
                .flatMap(adminDTO -> Mono.just(new AdminResponse(adminDTO.getId(),adminDTO.getEmail(), adminDTO.getPassword())));
    }
}
