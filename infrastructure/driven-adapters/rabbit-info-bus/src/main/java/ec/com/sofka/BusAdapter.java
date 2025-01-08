package ec.com.sofka;

import ec.com.sofka.gateway.BusEvent;
import ec.com.sofka.generics.domain.DomainEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


//11. BusMessage implementation, this is a service so, don't forget the annotation
@Service
public class BusAdapter implements BusEvent {

    //13. Use of RabbitTemplate to define the sendMsg method
    private final RabbitTemplate rabbitTemplate;

    public BusAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendEvent(DomainEvent event) {
        rabbitTemplate.convertAndSend("account.exchange",
                "account.routingKey",
                event);
    }


}
