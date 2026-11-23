package ma.enset.tp4.account.query;

import ma.enset.tp4.account.domain.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.config.ProcessingGroup;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("account-projection")
public class AccountProjector {
    private final AccountProjectionRepository repository;
    public AccountProjector(AccountProjectionRepository repository) { this.repository = repository; }

    @EventHandler
    public void on(AccountCreatedEvent event) {
        if (!repository.existsById(event.accountId())) {
            repository.save(new AccountProjection(event.accountId(), event.initialBalance(), event.currency(), AccountStatus.ACTIVE));
        }
    }
    @EventHandler
    public void on(AccountCreditedEvent event) {
        AccountProjection p = require(event.accountId());
        p.setBalance(p.getBalance().add(event.amount()));
        repository.save(p);
    }
    @EventHandler
    public void on(AccountDebitedEvent event) {
        AccountProjection p = require(event.accountId());
        p.setBalance(p.getBalance().subtract(event.amount()));
        repository.save(p);
    }
    @EventHandler
    public void on(AccountStatusChangedEvent event) {
        AccountProjection p = require(event.accountId());
        p.setStatus(event.status());
        repository.save(p);
    }
    private AccountProjection require(String id) {
        return repository.findById(id).orElseThrow(() -> new IllegalStateException("Missing read model: " + id));
    }
}
