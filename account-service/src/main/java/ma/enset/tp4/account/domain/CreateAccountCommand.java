package ma.enset.tp4.account.domain;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.math.BigDecimal;

public record CreateAccountCommand(
        @TargetAggregateIdentifier String accountId,
        BigDecimal initialBalance,
        String currency) { }
