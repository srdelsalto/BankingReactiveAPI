package ec.com.sofka.transaction;


import ec.com.sofka.NotFoundException;
import ec.com.sofka.aggregate.customer.events.AccountCreated;
import ec.com.sofka.aggregate.operation.events.TransactionCreated;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.transaction.queries.query.GetAllByAccountNumberQuery;
import ec.com.sofka.transaction.queries.usecases.GetAllByAccountNumberViewUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllByAccountNumberUseCaseTest {

    @Mock
    private IEventStore repository;

    @InjectMocks
    private GetAllByAccountNumberViewUseCase useCase;

    @Test
    void shouldThrowNotFoundExceptionWhenAccountNotFound() {
        String customerId = "customer-1";
        String accountNumber = "12345678";

        when(repository.findAggregate(customerId, "customer")).thenReturn(Flux.empty());

        GetAllByAccountNumberQuery request = new GetAllByAccountNumberQuery(customerId, accountNumber);

        StepVerifier.create(useCase.get(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Account not found"))
                .verify();

        verify(repository, times(1)).findAggregate(customerId, "customer");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTransactionsSuccessfully() {
        String customerId = "customer-1";
        String accountNumber = "12345678";
        String operationId = "operation-1";
        String userId = "user-1";

        AccountCreated accountEvent = new AccountCreated(
                "account1",
                accountNumber,
                new BigDecimal("1500.00"),
                userId
        );
        accountEvent.setAggregateRootId(customerId);

        TransactionCreated transactionCreated = new TransactionCreated(
                operationId,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(90),
                TransactionType.BRANCH_DEPOSIT,
                LocalDateTime.now(),
                accountNumber
        );
        transactionCreated.setAggregateRootId(operationId);

        when(repository.findAggregate(customerId, "customer")).thenReturn(Flux.just(accountEvent));
        when(repository.findAllAggregate("operation")).thenReturn(Flux.just(transactionCreated));

        GetAllByAccountNumberQuery request = new GetAllByAccountNumberQuery(customerId, accountNumber);

        StepVerifier.create(useCase.get(request))
                .expectNextMatches(response ->
                        response.getMultipleResults().get(0).getOperationId().equals(operationId) &&
                                response.getMultipleResults().get(0).getAmount().equals(BigDecimal.valueOf(100)) &&
                                response.getMultipleResults().get(0).getFee().equals(BigDecimal.valueOf(10)) &&
                                response.getMultipleResults().get(0).getNetAmount().equals(BigDecimal.valueOf(90)) &&
                                response.getMultipleResults().get(0).getType() == TransactionType.BRANCH_DEPOSIT &&
                                response.getMultipleResults().get(0).getAccountId().equals(accountNumber) &&
                                response.getMultipleResults().get(0).getCustomerId().equals(customerId)
                )
                .verifyComplete();

        verify(repository, times(1)).findAggregate(customerId, "customer");
        verify(repository, times(1)).findAllAggregate("operation");
    }

    @Test
    void shouldReturnEmptyWhenNoTransactionsExist() {
        String customerId = "customer-1";
        String accountNumber = "12345678";
        String userId = "user-1";

        AccountCreated accountEvent = new AccountCreated(
                "account1",
                accountNumber,
                new BigDecimal("1500.00"),
                userId
        );
        accountEvent.setAggregateRootId(customerId);

        when(repository.findAggregate(customerId, "customer")).thenReturn(Flux.just(accountEvent));
        when(repository.findAllAggregate("operation")).thenReturn(Flux.empty());

        GetAllByAccountNumberQuery request = new GetAllByAccountNumberQuery(customerId, accountNumber);

        StepVerifier.create(useCase.get(request))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        verify(repository, times(1)).findAggregate(customerId, "customer");
        verify(repository, times(1)).findAllAggregate("operation");
    }

}