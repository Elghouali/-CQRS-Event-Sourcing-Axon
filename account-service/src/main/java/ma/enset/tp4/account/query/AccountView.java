package ma.enset.tp4.account.query;
import ma.enset.tp4.account.domain.AccountStatus;
import java.math.BigDecimal;
public record AccountView(String accountId, BigDecimal balance, String currency, AccountStatus status) {
    public static AccountView from(AccountProjection account) {
        return new AccountView(account.getId(), account.getBalance(), account.getCurrency(), account.getStatus());
    }
}
