package ec.com.sofka.user.queries.usecases;

import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseEmptyGet;
import ec.com.sofka.generics.utils.QueryResponse;
import ec.com.sofka.user.queries.responses.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

public class GetAllUserViewUseCase implements IUseCaseEmptyGet<UserResponse> {
    private final IEventStore repository;

    public GetAllUserViewUseCase(IEventStore repository) {
        this.repository = repository;
    }

    @Override
    public Mono<QueryResponse<UserResponse>> get() {
        return repository.findAllAggregate("customer")
                .collectList()
                .flatMapMany(events -> {
                    Map<String, DomainEvent> latestEvents = events.stream()
                            .collect(Collectors.toMap(
                                    DomainEvent::getAggregateRootId,
                                    event -> event,
                                    (existing, replacement) -> existing.getVersion() >= replacement.getVersion() ? existing : replacement
                            ));

                    return Flux.fromIterable(latestEvents.values())
                            .flatMap(event -> Customer.from(event.getAggregateRootId(), Flux.fromIterable(events)));
                })
                .map(customer -> new UserResponse(
                        customer.getId().getValue(),
                        customer.getUser().getName().getValue(),
                        customer.getUser().getDocumentId().getValue())
                )
                .collectList()
                .flatMap(users -> Mono.just(QueryResponse.ofMultiple(users)));
    }
}
