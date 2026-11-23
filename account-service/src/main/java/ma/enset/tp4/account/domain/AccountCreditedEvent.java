package ma.enset.tp4.account.domain;
import java.math.BigDecimal;
public record AccountCreditedEvent(String accountId, BigDecimal amount) { }
