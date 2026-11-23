package ma.enset.tp4.account.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ma.enset.tp4.account.domain.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountCommandController {
    private final CommandGateway gateway;
    public AccountCommandController(CommandGateway gateway) { this.gateway = gateway; }

    public record CreateRequest(@NotNull @DecimalMin("0.0") BigDecimal initialBalance,
                                @NotBlank @Pattern(regexp = "(?i)[a-z]{3}") String currency) { }
    public record AmountRequest(@NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount) { }
    public record StatusRequest(@NotNull AccountStatus status) { }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> create(@Valid @RequestBody CreateRequest request) {
        String id = UUID.randomUUID().toString();
        String aggregateId = gateway.sendAndWait(new CreateAccountCommand(id, request.initialBalance(), request.currency().toUpperCase(Locale.ROOT)));
        return Map.of("accountId", aggregateId);
    }

    @PostMapping("/{id}/credit")
    public Map<String,String> credit(@PathVariable String id, @Valid @RequestBody AmountRequest request) {
        gateway.sendAndWait(new CreditAccountCommand(id, request.amount()));
        return Map.of("status", "credited", "accountId", id);
    }

    @PostMapping("/{id}/debit")
    public Map<String,String> debit(@PathVariable String id, @Valid @RequestBody AmountRequest request) {
        gateway.sendAndWait(new DebitAccountCommand(id, request.amount()));
        return Map.of("status", "debited", "accountId", id);
    }

    @PatchMapping("/{id}/status")
    public Map<String,String> status(@PathVariable String id, @Valid @RequestBody StatusRequest request) {
        gateway.sendAndWait(new ChangeAccountStatusCommand(id, request.status()));
        return Map.of("status", request.status().toString(), "accountId", id);
    }
}
