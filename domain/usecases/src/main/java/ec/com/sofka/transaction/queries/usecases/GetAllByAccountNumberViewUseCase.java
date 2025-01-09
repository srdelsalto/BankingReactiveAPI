package ec.com.sofka.transaction.queries.usecases;

import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.aggregate.operation.Operation;
import ec.com.sofka.exception.NotFoundException;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseGet;
import ec.com.sofka.generics.utils.QueryResponse;
import ec.com.sofka.transaction.queries.query.GetAllByAccountNumberQuery;
import ec.com.sofka.transaction.queries.responses.TransactionResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

public class GetAllByAccountNumberViewUseCase implements IUseCaseGet<GetAllByAccountNumberQuery, TransactionResponse> {

    private final IEventStore repository;

    public GetAllByAccountNumberViewUseCase(IEventStore repository) {
        this.repository = repository;
    }

    @Override
    public Mono<QueryResponse<TransactionResponse>> get(GetAllByAccountNumberQuery cmd) {
        Flux<DomainEvent> eventsCustomer = repository.findAggregate(cmd.getCustomerId(), "customer");

        return Customer.from(cmd.getCustomerId(), eventsCustomer)
                .flatMap(customer -> Mono.justOrEmpty(
                                        customer.getAccounts().stream()
                                                .filter(account -> account.getAccountNumber().getValue().equals(cmd.getAccountNumber()))
                                                .findFirst()
                                )
                                .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
                                .flatMap(account -> {
                                    return repository.findAllAggregate("operation")
                                            .collectList()
                                            .flatMapMany(eventsOperation -> {
                                                Map<String, DomainEvent> latestEvents = eventsOperation.stream()
                                                        .collect(Collectors.toMap(
                                                                DomainEvent::getAggregateRootId,
                                                                event -> event,
                                                                (existing, replacement) -> existing.getVersion() >= replacement.getVersion() ? existing : replacement
                                                        ));

                                                return Flux.fromIterable(latestEvents.values())
                                                        .flatMap(event -> Operation.from(event.getAggregateRootId(), Flux.fromIterable(eventsOperation)));
                                            })
                                            .map(operation -> new TransactionResponse(
                                                    operation.getId().getValue(),
                                                    operation.getTransaction().getAmount().getValue(),
                                                    operation.getTransaction().getFee().getValue(),
                                                    operation.getTransaction().getNetAmount().getValue(),
                                                    operation.getTransaction().getType().getValue(),
                                                    operation.getTransaction().getTimestamp().getValue(),
                                                    operation.getTransaction().getAccountId().getValue(),
                                                    customer.getId().getValue()
                                            ))
                                            .collectList()
                                            .flatMap(transactions -> Mono.just(QueryResponse.ofMultiple(transactions)));
                                })
                );
    }
}