package ec.com.sofka.account.queries.query;

import ec.com.sofka.generics.utils.Query;

public class GetAllByUserIdQuery extends Query {
    public GetAllByUserIdQuery(String aggregateId) {
        super(aggregateId);
    }
}
