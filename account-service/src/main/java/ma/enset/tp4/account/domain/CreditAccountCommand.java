package ma.enset.tp4.account.domain;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.math.BigDecimal;
public record CreditAccountCommand(@TargetAggregateIdentifier String accountId, BigDecimal amount) { }
