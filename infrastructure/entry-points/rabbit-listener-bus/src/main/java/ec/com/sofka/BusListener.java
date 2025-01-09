package ec.com.sofka;

import ec.com.sofka.account.queries.AccountSavedViewUseCase;
import ec.com.sofka.aggregate.customer.events.AccountCreated;
import ec.com.sofka.gateway.BusEventListener;
import ec.com.sofka.gateway.dto.AccountDTO;
import ec.com.sofka.generics.domain.DomainEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BusListener implements BusEventListener {

    private final AccountSavedViewUseCase accountSavedViewUseCase;

    public BusListener(AccountSavedViewUseCase accountSavedViewUseCase) {
        this.accountSavedViewUseCase = accountSavedViewUseCase;
    }

    @Override
    @RabbitListener(queues = "account.created.queue")
    public void receiveAccountCreated(DomainEvent event) {
        AccountCreated accountCreated = (AccountCreated) event;
        AccountDTO accountDTO = new AccountDTO(
                accountCreated.getId(),
                accountCreated.getAccountNumber(),
                accountCreated.getBalance(),
                accountCreated.getUserId()
        );
        System.out.println(accountCreated.getId());
        accountSavedViewUseCase.accept(accountDTO);
    }
}
