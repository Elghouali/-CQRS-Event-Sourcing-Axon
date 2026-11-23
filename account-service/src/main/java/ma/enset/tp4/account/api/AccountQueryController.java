package ma.enset.tp4.account.api;

import ma.enset.tp4.account.query.*;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventhandling.DomainEventMessage;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountQueryController {
    private final QueryGateway gateway;
    private final EventStore eventStore;
    public AccountQueryController(QueryGateway gateway, EventStore eventStore) {
        this.gateway = gateway; this.eventStore = eventStore;
    }

    @GetMapping
    public List<AccountView> all() {
        return gateway.query(new GetAllAccountsQuery(), ResponseTypes.multipleInstancesOf(AccountView.class)).join();
    }
    @GetMapping("/{id}")
    public AccountView one(@PathVariable String id) {
        return gateway.query(new GetAccountQuery(id), ResponseTypes.instanceOf(AccountView.class)).join();
    }

    public record StoredEvent(long sequence, String type, Instant timestamp, Object payload) { }

    @GetMapping("/{id}/events")
    public List<StoredEvent> history(@PathVariable String id) {
        List<StoredEvent> result = new ArrayList<>();
        var stream = eventStore.readEvents(id);
        while (stream.hasNext()) {
            DomainEventMessage<?> event = stream.next();
            result.add(new StoredEvent(event.getSequenceNumber(), event.getPayloadType().getSimpleName(),
                    event.getTimestamp(), event.getPayload()));
        }
        return result;
    }
}
