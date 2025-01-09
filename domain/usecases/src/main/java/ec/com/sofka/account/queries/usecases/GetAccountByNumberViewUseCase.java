package ec.com.sofka.account.queries.usecases;

import ec.com.sofka.account.queries.query.GetAccountByNumberQuery;

import ec.com.sofka.account.queries.responses.AccountResponse;
import ec.com.sofka.aggregate.customer.Customer;
import ec.com.sofka.exception.NotFoundException;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.interfaces.IUseCaseGet;
import ec.com.sofka.generics.utils.QueryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GetAccountByNumberViewUseCase implements IUseCaseGet<GetAccountByNumberQuery, AccountResponse> {
    private final IEventStore repository;

    public GetAccountByNumberViewUseCase(IEventStore repository) {
        this.repository = repository;
    }

    @Override
    public Mono<QueryResponse<AccountResponse>> get(GetAccountByNumberQuery query) {
        Flux<DomainEvent> events = repository.findAggregate(query.getAggregateId(), "customer");

        return Customer.from(query.getAggregateId(), events)
                .flatMap(customer -> Mono.justOrEmpty(
                                        customer.getAccounts().stream()
                                                .filter(account -> account.getAccountNumber().getValue().equals(query.getAccountNumber()))
                                                .findFirst()
                                )
                                .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
                                .map(account -> QueryResponse.ofSingle(new AccountResponse(
                                        customer.getId().getValue(),
                                        account.getAccountNumber().getValue(),
                                        account.getBalance().getValue(),
                                        account.getUserId().getValue()
                                )))
                );
    }
}
