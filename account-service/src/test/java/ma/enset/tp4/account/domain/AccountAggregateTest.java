package ma.enset.tp4.account.domain;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

class AccountAggregateTest {
    private FixtureConfiguration<AccountAggregate> fixture;
    @BeforeEach void setup() { fixture = new AggregateTestFixture<>(AccountAggregate.class); }

    @Test void createsAnAccount() {
        fixture.givenNoPriorActivity()
                .when(new CreateAccountCommand("acc-01", new BigDecimal("100.00"), "MAD"))
                .expectEvents(new AccountCreatedEvent("acc-01", new BigDecimal("100.00"), "MAD"));
    }

    @Test void creditsAnAccount() {
        fixture.given(new AccountCreatedEvent("acc-01", new BigDecimal("100.00"), "MAD"))
                .when(new CreditAccountCommand("acc-01", new BigDecimal("50.00")))
                .expectEvents(new AccountCreditedEvent("acc-01", new BigDecimal("50.00")));
    }

    @Test void refusesOverdraft() {
        fixture.given(new AccountCreatedEvent("acc-01", new BigDecimal("100.00"), "MAD"))
                .when(new DebitAccountCommand("acc-01", new BigDecimal("101.00")))
                .expectException(IllegalStateException.class);
    }

    @Test void refusesZeroCredit() {
        fixture.given(new AccountCreatedEvent("acc-01", new BigDecimal("100.00"), "MAD"))
                .when(new CreditAccountCommand("acc-01", BigDecimal.ZERO))
                .expectException(IllegalArgumentException.class);
    }

    @Test void cannotCloseNonEmptyAccount() {
        fixture.given(new AccountCreatedEvent("acc-01", new BigDecimal("100.00"), "MAD"))
                .when(new ChangeAccountStatusCommand("acc-01", AccountStatus.CLOSED))
                .expectException(IllegalStateException.class);
    }
}
