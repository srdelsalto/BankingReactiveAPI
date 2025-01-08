package ec.com.sofka;


import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.gateway.BusEventListener;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.queries.usecases.AccountSavedViewUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

//20. Create the BusListener class
@Service
public class BusListener implements BusEventListener {

    private final AccountSavedViewUseCase accountSavedViewUseCase;

    public BusListener(AccountSavedViewUseCase accountSavedViewUseCase) {
        this.accountSavedViewUseCase = accountSavedViewUseCase;
    }

    @Override
    @RabbitListener(queues = "account.created.queue")
    public void receiveAccountCreated(DomainEvent event) {
        AccountCreated accountEvent = (AccountCreated) event;
        accountSavedViewUseCase.accept(new AccountDTO(
                accountEvent.getAccountId(),
                accountEvent.getName(),
                accountEvent.getAccountNumber(),
                accountEvent.getAccountBalance(),
                accountEvent.getStatus()
        ));

    }

    /*@Override
    @RabbitListener(queues = "account.updated.queue")
    public void receiveAccountUpdated(DomainEvent event) {

    }*/


}
