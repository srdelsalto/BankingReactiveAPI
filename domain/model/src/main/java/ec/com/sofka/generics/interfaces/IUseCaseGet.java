package ec.com.sofka.generics.interfaces;

import ec.com.sofka.generics.utils.Query;
import ec.com.sofka.generics.utils.QueryResponse;

import java.util.List;
import java.util.Optional;

public interface IUseCaseGet <Q extends Query, R> {
    //R get(Q request); // For single result
    QueryResponse<R> get(Q request);  // For multiple results
}
