package ec.com.sofka.router;

import ec.com.sofka.handler.AdminHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AdminRouter {

    private final AdminHandler adminHandler;

    public AdminRouter(AdminHandler adminHandler) {
        this.adminHandler = adminHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> adminRoutes() {
        return RouterFunctions
                .route(RequestPredicates.POST("/admin/register"), adminHandler::create)
                .andRoute(RequestPredicates.POST("/admin/login"), adminHandler::login);
    }
}
