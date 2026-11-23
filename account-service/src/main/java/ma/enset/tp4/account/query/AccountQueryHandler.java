package ma.enset.tp4.account.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class AccountQueryHandler {
    private final AccountProjectionRepository repository;
    public AccountQueryHandler(AccountProjectionRepository repository) { this.repository = repository; }

    @QueryHandler
    public AccountView handle(GetAccountQuery query) {
        return repository.findById(query.accountId()).map(AccountView::from)
                .orElseThrow(() -> new NoSuchElementException("Account not found: " + query.accountId()));
    }
    @QueryHandler
    public List<AccountView> handle(GetAllAccountsQuery query) {
        return repository.findAll().stream().map(AccountView::from).toList();
    }
}
