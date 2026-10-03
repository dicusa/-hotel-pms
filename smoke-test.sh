#!/usr/bin/env bash
set -euo pipefail

API_BASE="http://localhost:3000/api"

# Helper to pretty‑print JSON responses
pp() { python -c 'import sys, json; print(json.dumps(json.load(sys.stdin), indent=2))' ; }

echo "Fetching today's reservations..."
TODAY_RES=$(curl -s -f "${API_BASE}/reservations/today")
RES_ID=$(echo "$TODAY_RES" | python -c "import sys, json; data=json.load(sys.stdin); print(data[0]['id'] if data else '')")
if [[ -z "$RES_ID" ]]; then
  echo "No reservation found for today. Ensure seed data is loaded." >&2
  exit 1
fi

echo "Reservation ID: $RES_ID"

# Check‑In
echo "Performing check‑in..."
CHECKIN_RESP=$(curl -s -f -X POST "${API_BASE}/checkin" \
  -H "Content-Type: application/json" \
  -d "{\"reservationId\": \"$RES_ID\"}")
STATUS=$(echo "$CHECKIN_RESP" | python -c "import sys, json; print(json.load(sys.stdin)['status'])")
if [[ "$STATUS" != "Occupied" ]]; then
  echo "Check‑in failed or unexpected status: $STATUS" >&2
  exit 1
fi

echo "Check‑in successful. Status: $STATUS"

# Check‑Out
echo "Performing check‑out..."
CHECKOUT_RESP=$(curl -s -f -X POST "${API_BASE}/checkout" \
  -H "Content-Type: application/json" \
  -d "{\"reservationId\": \"$RES_ID\"}")
FINAL_STATUS=$(echo "$CHECKOUT_RESP" | python -c "import sys, json; print(json.load(sys.stdin)['status'])")
if [[ "$FINAL_STATUS" != "needs-cleaning" ]]; then
  echo "Check‑out failed or unexpected final status: $FINAL_STATUS" >&2
  exit 1
fi

echo "Check‑out successful. Final status: $FINAL_STATUS"

echo "Smoke test completed successfully."
