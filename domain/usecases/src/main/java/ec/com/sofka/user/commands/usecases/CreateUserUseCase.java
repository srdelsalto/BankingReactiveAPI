package ec.com.sofka.user.commands.usecases;

import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.exception.ConflictException;
import ec.com.sofka.gateway.BusEvent;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.gateway.UserRepository;
import ec.com.sofka.generics.interfaces.IUseCase;
import ec.com.sofka.generics.interfaces.IUseCaseExecute;
import ec.com.sofka.user.commands.CreateUserCommand;
import ec.com.sofka.user.queries.responses.UserResponse;
import reactor.core.publisher.Mono;

public class CreateUserUseCase implements IUseCaseExecute<CreateUserCommand, UserResponse> {
    private final IEventStore repository;
    private final UserRepository userRepository;
    private final BusEvent busEvent;


    public CreateUserUseCase(IEventStore repository, UserRepository userRepository, BusEvent busEvent) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.busEvent = busEvent;
    }

    @Override
    public Mono<UserResponse> execute(CreateUserCommand cmd) {
        return userRepository.findByDocumentId(cmd.getDocumentId())
                .flatMap(existingUser -> Mono.error(new ConflictException("User with document ID already exists")))
                .then(Mono.defer(() -> {

                    Customer customer = new Customer();

                    customer.createUser(cmd.getName(), cmd.getDocumentId());

                    customer.getUncommittedEvents()
                            .stream()
                            .map(repository::save)
                            .forEach(busEvent::sendEventUserCreated);

                    customer.markEventsAsCommitted();

                    return Mono.just(new UserResponse(
                            customer.getId().getValue(),
                            customer.getUser().getName().getValue(),
                            customer.getUser().getDocumentId().getValue()
                    ));
                }));
    }
}
