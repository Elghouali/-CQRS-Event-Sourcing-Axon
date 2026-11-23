"""Run after starting account-service and gateway-service (Python standard library only)."""
import json
from urllib.request import Request, urlopen
from urllib.error import HTTPError
BASE = "http://localhost:8080/api/accounts"

def call(method, path, data=None):
    payload = json.dumps(data).encode() if data is not None else None
    req = Request(BASE + path, payload, {"Content-Type": "application/json"}, method=method)
    try:
        with urlopen(req, timeout=10) as resp:
            return resp.status, json.load(resp)
    except HTTPError as exc:
        return exc.code, json.load(exc)

status, body = call("POST", "", {"initialBalance": 1000, "currency": "MAD"})
assert status == 201, (status, body)
account = body["accountId"]
assert call("POST", f"/{account}/credit", {"amount": 500})[0] == 200
assert call("POST", f"/{account}/debit", {"amount": 200})[0] == 200
status, read = call("GET", f"/{account}")
assert status == 200 and float(read["balance"]) == 1300, (status, read)
status, events = call("GET", f"/{account}/events")
assert status == 200 and len(events) == 3, (status, events)
status, refused = call("POST", f"/{account}/debit", {"amount": 5000})
assert status == 409, (status, refused)
print("SUCCESS: account created, credited, debited; projection and event history consistent.")
print("Account ID:", account)
print("History:", json.dumps(events, indent=2))
