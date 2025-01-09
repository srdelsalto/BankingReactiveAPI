package ec.com.sofka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.com.sofka.gateway.BusEvent;
import ec.com.sofka.generics.domain.DomainEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BusAdapter implements BusEvent {

    private final RabbitTemplate rabbitTemplate;

    public BusAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendEventAccountCreated(Mono<DomainEvent> event) {
        event.subscribe(accountCreated -> {
                    if (!accountCreated.getEventType().equals("ACCOUNT_CREATED")) return;
                    rabbitTemplate.convertAndSend("account.created.exchange", "account.created.event", accountCreated);
                }
        );
    }
}
