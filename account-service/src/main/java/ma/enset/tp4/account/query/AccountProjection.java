package ma.enset.tp4.account.query;

import jakarta.persistence.*;
import ma.enset.tp4.account.domain.AccountStatus;
import java.math.BigDecimal;

@Entity
@Table(name = "account_projections")
public class AccountProjection {
    @Id
    private String id;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @Column(nullable = false, length = 3)
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    protected AccountProjection() { }

    public AccountProjection(String id, BigDecimal balance, String currency, AccountStatus status) {
        this.id = id; this.balance = balance; this.currency = currency; this.status = status;
    }
    public String getId() { return id; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
}
