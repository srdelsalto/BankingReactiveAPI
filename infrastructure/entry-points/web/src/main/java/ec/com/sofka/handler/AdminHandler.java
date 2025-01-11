package ec.com.sofka.handler;

import ec.com.sofka.dto.AccountRequestDTO;
import ec.com.sofka.mapper.AccountMapper;
import ec.com.sofka.usecases.LoginAdminUseCase;
import ec.com.sofka.usecases.RegisterAdminUseCase;
import ec.com.sofka.usecases.command.LoginAdminCommand;
import ec.com.sofka.usecases.command.RegisterAdminCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AdminHandler {
    private final RegisterAdminUseCase registerAdminUseCase;
    private final LoginAdminUseCase loginAdminUseCase;

    public AdminHandler(RegisterAdminUseCase registerAdminUseCase, LoginAdminUseCase loginAdminUseCase) {
        this.registerAdminUseCase = registerAdminUseCase;
        this.loginAdminUseCase = loginAdminUseCase;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(RegisterAdminCommand.class)
                .flatMap(registerAdminUseCase::execute)
                .flatMap(adminResponseDTO -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(adminResponseDTO));
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginAdminCommand.class)
                .flatMap(loginAdminUseCase::execute)
                .flatMap(adminResponseDTO -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(adminResponseDTO));
    }
}
