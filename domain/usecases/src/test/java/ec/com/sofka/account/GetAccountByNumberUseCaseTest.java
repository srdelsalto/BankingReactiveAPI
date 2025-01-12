package ec.com.sofka.account;

import ec.com.sofka.NotFoundException;
import ec.com.sofka.account.queries.query.GetAccountByNumberQuery;
import ec.com.sofka.account.queries.responses.AccountResponse;
import ec.com.sofka.account.queries.usecases.GetAccountByNumberViewUseCase;
import ec.com.sofka.aggregate.customer.events.AccountCreated;

import ec.com.sofka.gateway.IEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAccountByNumberUseCaseTest {

    @Mock
    private IEventStore repository;

    @InjectMocks
    private GetAccountByNumberViewUseCase useCase;

    @Test
    void shouldReturnAccountByNumber() {
        String aggregateId = "customer123";
        String userId = "user123";
        String accountNumber = "ACC-001-001";

        AccountCreated accountCreated = new AccountCreated(
                "account1",
                accountNumber,
                new BigDecimal("1500.00"),
                userId
        );
        accountCreated.setAggregateRootId(aggregateId);

        when(repository.findAggregate(aggregateId, "customer"))
                .thenReturn(Flux.just(accountCreated));

        GetAccountByNumberQuery request = new GetAccountByNumberQuery(aggregateId, accountNumber);

        useCase.get(request)
                .as(StepVerifier::create)
                .consumeNextWith(response -> {
                    AccountResponse accountResponse = response.getSingleResult().get();
                    assert accountResponse.getCustomerId().equals(aggregateId);
                    assert accountResponse.getAccountNumber().equals(accountNumber);
                    assert accountResponse.getBalance().compareTo(new BigDecimal("1500.00")) == 0;
                    assert accountResponse.getUserId().equals(userId);
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowNotFoundExceptionWhenAccountNotExists() {
        String aggregateId = "customer123";
        String userId = "user123";
        String accountNumber = "ACC-001-001";

        AccountCreated accountCreated = new AccountCreated(
                "account2",
                "ACC-002-002",
                new BigDecimal("1000.00"),
                userId
        );
        accountCreated.setAggregateRootId(aggregateId);

        when(repository.findAggregate(aggregateId, "customer"))
                .thenReturn(Flux.just(accountCreated));

        GetAccountByNumberQuery request = new GetAccountByNumberQuery(aggregateId, accountNumber);

        useCase.get(request)
                .as(StepVerifier::create)
                .expectErrorMatches(ex -> ex instanceof NotFoundException && ex.getMessage().equals("Account not found"))
                .verify();
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNoEventsExist() {
        String aggregateId = "customer123";
        String accountNumber = "ACC-001-001";

        when(repository.findAggregate(aggregateId, "customer"))
                .thenReturn(Flux.empty());

        GetAccountByNumberQuery request = new GetAccountByNumberQuery(aggregateId, accountNumber);

        useCase.get(request)
                .as(StepVerifier::create)
                .expectErrorMatches(ex -> ex instanceof NotFoundException && ex.getMessage().equals("Account not found"))
                .verify();
    }
}

