package ma.enset.tp4.account.domain;
import java.math.BigDecimal;
public record AccountDebitedEvent(String accountId, BigDecimal amount) { }
