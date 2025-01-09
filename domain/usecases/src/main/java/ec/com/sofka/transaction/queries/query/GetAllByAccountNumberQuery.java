package ec.com.sofka.transaction.queries.query;

import ec.com.sofka.generics.utils.Query;

public class GetAllByAccountNumberQuery extends Query {
    private final String customerId;
    private final String accountNumber;

    public GetAllByAccountNumberQuery(String customerId, String accountNumber) {
        super(null);
        this.customerId = customerId;
        this.accountNumber = accountNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
