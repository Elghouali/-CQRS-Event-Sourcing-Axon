package ma.enset.tp4.account.domain;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
public record ChangeAccountStatusCommand(@TargetAggregateIdentifier String accountId, AccountStatus status) { }
