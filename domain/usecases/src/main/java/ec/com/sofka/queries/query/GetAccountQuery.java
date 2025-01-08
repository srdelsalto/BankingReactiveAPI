package ec.com.sofka.queries.query;

import ec.com.sofka.generics.utils.Query;

import java.math.BigDecimal;

//Usage of the Request class
public class GetAccountQuery extends Query {
    private BigDecimal balance;
    private String numberAcc;
    private String customerName;


    public GetAccountQuery(final String aggregateId, final String numberAcc) {
        super(aggregateId);
        this.balance = null;
        this.numberAcc = numberAcc;
        this.customerName = null;
    }

    public GetAccountQuery(){
        super(null);

    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getNumber() {
        return numberAcc;
    }

    public String getCustomerName() {
        return customerName;
    }

}
