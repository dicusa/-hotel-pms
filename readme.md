# Hotel PMS

## Overview
A simple Hotel Property Management System (PMS) with front‑desk check‑in and check‑out functionality.

## Prerequisites
- **Node.js** (v18 or later) and **npm** installed.
- A running instance of the backend API (provides `/api/reservations`, `/api/checkin`, `/api/checkout`).

## Installation
```bash
# Clone the repository (if not already done)
git clone https://github.com/dicusa/-hotel-pms.git
cd -hotel-pms

# Install front‑end dependencies (if any)
npm ci
```

## Database Setup
1. **Run migrations** (adjust the command to match your migration tool):
   ```bash
   npm run migrate   # example command – replace with actual migration script
   ```
2. **Seed the database** with an admin user and a sample reservation arriving today:
   ```bash
   npm run seed      # should create an admin and a reservation for today
   ```
   The seed script creates a reservation with:
   - `guestName`: "John Doe"
   - `arrivalDate`: *today's date*
   - `status`: `Booked`

## Starting the Application
```bash
npm start          # serves the static files and proxies API calls
```
The web server will be available at `http://localhost:3000`.

## Front‑Desk Navigation
- **Check‑In**: `http://localhost:3000/front-desk/check-in.html`
- **Check‑Out**: `http://localhost:3000/front-desk/check-out.html`

Both routes are linked from the main navigation on `index.html`.

## Smoke Test
A simple automated smoke test is provided to verify the complete check‑in/check‑out flow.
```bash
chmod +x smoke-test.sh
./smoke-test.sh
```
The script will:
1. Fetch today's reservations.
2. Perform a check‑in for the first reservation.
3. Verify the reservation status changes to `Occupied` and a room key is assigned.
4. Perform a checkout for the same reservation.
5. Verify the final status is `needs-cleaning`.

If any step fails, the script will exit with a non‑zero status and output an error message.

## Troubleshooting
- Ensure the backend API is reachable at the URL configured in the front‑end (`/api/...`).
- Verify that the seed data was loaded correctly; you should see a reservation for today when visiting the Check‑In page.
- Check console logs in the browser for any JavaScript errors.
