package ec.com.sofka.account;

import ec.com.sofka.account.queries.query.GetAllByUserIdQuery;
import ec.com.sofka.account.queries.responses.AccountResponse;
import ec.com.sofka.account.queries.usecases.GetAllByUserIdViewUseCase;
import ec.com.sofka.aggregate.customer.events.AccountCreated;
import ec.com.sofka.aggregate.customer.events.UserCreated;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
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
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllByUserIdUseCaseTest {

    @Mock
    private IEventStore repository;

    @InjectMocks
    private GetAllByUserIdViewUseCase useCase;

    @Test
    void shouldReturnAllAccountsForUser() {
        String aggregateId = "customer123";
        String userId = "user123";
        String accountNumber1 = "ACC-001-001";
        String accountNumber2 = "ACC-002-002";

        AccountCreated account1Created = new AccountCreated(
                "account1",
                accountNumber1,
                new BigDecimal("1000.00"),
                userId
        );
        account1Created.setAggregateRootId(aggregateId);

        AccountCreated account2Created = new AccountCreated(
                "account2",
                accountNumber2,
                new BigDecimal("2000.00"),
                userId
        );
        account2Created.setAggregateRootId(aggregateId);

        List<DomainEvent> events = List.of(account1Created, account2Created);

        when(repository.findAggregate(aggregateId, "customer"))
                .thenReturn(Flux.fromIterable(events));

        GetAllByUserIdQuery request = new GetAllByUserIdQuery(aggregateId);

        useCase.get(request)
                .as(StepVerifier::create)
                .consumeNextWith(response -> {
                    List<AccountResponse> accountResponse = response.getMultipleResults();
                    assert accountResponse.get(0).getCustomerId().equals(aggregateId);
                    assert accountResponse.get(0).getAccountNumber().equals(accountNumber1);
                    assert accountResponse.get(0).getBalance().compareTo(new BigDecimal("1000.00")) == 0;
                    assert accountResponse.get(0).getUserId().equals(userId);
                    assert accountResponse.get(1).getCustomerId().equals(aggregateId);
                    assert accountResponse.get(1).getAccountNumber().equals(accountNumber2);
                    assert accountResponse.get(1).getBalance().compareTo(new BigDecimal("2000.00")) == 0;
                    assert accountResponse.get(1).getUserId().equals(userId);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoAccounts() {
        String aggregateId = "customer123";
        String userId = "user123";

        DomainEvent userCreated = new UserCreated(userId, "John Doe", "DOC123");

        userCreated.setAggregateRootId(aggregateId);
        List<DomainEvent> events = List.of(userCreated);

        when(repository.findAggregate(aggregateId, "customer"))
                .thenReturn(Flux.fromIterable(events));

        GetAllByUserIdQuery request = new GetAllByUserIdQuery(aggregateId);

        StepVerifier.create(useCase.get(request))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

}