package ec.com.sofka.user;

import ec.com.sofka.aggregate.customer.events.UserCreated;
import ec.com.sofka.gateway.IEventStore;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.user.queries.usecases.GetAllUserViewUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GetAllUserUseCaseTest {

    @Mock
    private IEventStore eventStore;

    @InjectMocks
    private GetAllUserViewUseCase useCase;

    @Test
    void shouldReturnAllUsersSuccessfully() {
        DomainEvent user1Created = new UserCreated("user1", "John Doe", "DOC001");
        user1Created.setAggregateRootId("customer1");
        user1Created.setVersion(1L);

        DomainEvent user2Created = new UserCreated("user2", "Jane Doe", "DOC002");
        user2Created.setAggregateRootId("customer2");
        user2Created.setVersion(1L);

        DomainEvent user1Updated = new UserCreated("user1", "John Smith", "DOC001");
        user1Updated.setAggregateRootId("customer1");
        user1Updated.setVersion(2L);

        when(eventStore.findAllAggregate("customer"))
                .thenReturn(Flux.just(user1Created, user2Created, user1Updated));

        StepVerifier.create(useCase.get())
                .expectNextMatches(response ->
                        response.getMultipleResults().get(0).getCustomerId().equals("customer2") &&
                                response.getMultipleResults().get(0).getName().equals("Jane Doe") &&
                                response.getMultipleResults().get(0).getDocumentId().equals("DOC002") &&
                                 response.getMultipleResults().get(1).getCustomerId().equals("customer1") &&
                                 response.getMultipleResults().get(1).getName().equals("John Smith") &&
                                 response.getMultipleResults().get(1).getDocumentId().equals("DOC001")
                )
                .verifyComplete();

        verify(eventStore, times(1)).findAllAggregate("customer");
    }

    @Test
    void shouldReturnEmptyFluxWhenNoUsersExist() {
        when(eventStore.findAllAggregate("customer")).thenReturn(Flux.empty());

        StepVerifier.create(useCase.get())
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();

        verify(eventStore, times(1)).findAllAggregate("customer");
    }

    @Test
    void shouldPropagateErrorWhenEventStoreFails() {
        when(eventStore.findAllAggregate("customer"))
                .thenReturn(Flux.error(new RuntimeException("Event store error")));

        StepVerifier.create(useCase.get())
                .expectErrorMatches(ex -> ex instanceof RuntimeException && ex.getMessage().equals("Event store error"))
                .verify();

        verify(eventStore, times(1)).findAllAggregate("customer");
    }
}
