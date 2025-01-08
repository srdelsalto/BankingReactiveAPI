package ec.com.sofka.generics.interfaces;

import ec.com.sofka.generics.utils.Command;

public interface IUseCaseAccept<AccountDTO, Void> {
    void accept(AccountDTO accountDTO);
}
