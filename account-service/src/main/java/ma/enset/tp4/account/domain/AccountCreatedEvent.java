package ma.enset.tp4.account.domain;
import java.math.BigDecimal;
public record AccountCreatedEvent(String accountId, BigDecimal initialBalance, String currency) { }
