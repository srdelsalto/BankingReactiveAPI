package ec.com.sofka.gateway;

import ec.com.sofka.generics.domain.DomainEvent;

public interface BusEvent {
    void sendEvent(DomainEvent event);
}
