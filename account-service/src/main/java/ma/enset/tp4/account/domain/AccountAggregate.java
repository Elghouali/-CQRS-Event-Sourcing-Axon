package ma.enset.tp4.account.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;
import java.util.Locale;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;

    protected AccountAggregate() { } // Needed by Axon to replay the event stream.

    @CommandHandler
    public AccountAggregate(CreateAccountCommand command) {
        if (command.accountId() == null || command.accountId().isBlank()) {
            throw new IllegalArgumentException("Account ID is required");
        }
        if (command.currency() == null || !command.currency().matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Currency must have exactly 3 letters");
        }
        if (command.initialBalance() == null || command.initialBalance().signum() < 0) {
            throw new IllegalArgumentException("Initial balance must be >= 0");
        }
        apply(new AccountCreatedEvent(command.accountId(), command.initialBalance(),
                command.currency().toUpperCase(Locale.ROOT)));
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        requireActive();
        requirePositive(command.amount());
        apply(new AccountCreditedEvent(accountId, command.amount()));
    }

    @CommandHandler
    public void handle(DebitAccountCommand command) {
        requireActive();
        requirePositive(command.amount());
        if (balance.compareTo(command.amount()) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        apply(new AccountDebitedEvent(accountId, command.amount()));
    }

    @CommandHandler
    public void handle(ChangeAccountStatusCommand command) {
        if (command.status() == null) throw new IllegalArgumentException("Status is required");
        if (status == AccountStatus.CLOSED) throw new IllegalStateException("Closed accounts cannot be reopened");
        if (command.status() == status) throw new IllegalArgumentException("Status has not changed");
        if (command.status() == AccountStatus.CLOSED && balance.signum() != 0) {
            throw new IllegalStateException("Close an account only after its balance reaches zero");
        }
        apply(new AccountStatusChangedEvent(accountId, command.status()));
    }

    private void requireActive() {
        if (status != AccountStatus.ACTIVE) throw new IllegalStateException("Account is not active");
    }

    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        accountId = event.accountId();
        balance = event.initialBalance();
        currency = event.currency();
        status = AccountStatus.ACTIVE;
    }
    @EventSourcingHandler
    public void on(AccountCreditedEvent event) { balance = balance.add(event.amount()); }
    @EventSourcingHandler
    public void on(AccountDebitedEvent event) { balance = balance.subtract(event.amount()); }
    @EventSourcingHandler
    public void on(AccountStatusChangedEvent event) { status = event.status(); }
}
