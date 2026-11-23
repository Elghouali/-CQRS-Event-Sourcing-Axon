package ma.enset.tp4.account.domain;
public record AccountStatusChangedEvent(String accountId, AccountStatus status) { }
