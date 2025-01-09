package ec.com.sofka.account.queries.usecases;

import ec.com.sofka.account.queries.query.GetAllByUserIdQuery;
import ec.com.sofka.account.queries.responses.AccountResponse;
import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseGet;
import ec.com.sofka.generics.utils.QueryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GetAllByUserIdViewUseCase implements IUseCaseGet<GetAllByUserIdQuery, AccountResponse> {
    private final IEventStore repository;

    public GetAllByUserIdViewUseCase(IEventStore repository) {
        this.repository = repository;
    }

    @Override
    public Mono<QueryResponse<AccountResponse>> get(GetAllByUserIdQuery request) {
        Flux<DomainEvent> events = repository.findAggregate(request.getAggregateId(), "customer");
        return Customer.from(request.getAggregateId(), events)
                .flatMap(customer ->
                        Flux.fromIterable(customer.getAccounts())
                                .map(account -> new AccountResponse(
                                        customer.getId().getValue(),
                                        account.getAccountNumber().getValue(),
                                        account.getBalance().getValue(),
                                        account.getUserId().getValue()
                                ))
                                .collectList()
                                .flatMap(accounts -> Mono.just(QueryResponse.ofMultiple(accounts)))
                );
    }
}
