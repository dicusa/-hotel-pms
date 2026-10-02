from fastapi.testclient import TestClient
from hotel_pms.main import app
from hotel_pms.models import SessionLocal, models

client = TestClient(app)

def setup_module(module):
    db = SessionLocal()
    # Ensure a clean state
    db.query(models.Invoice).delete()
    db.query(models.Reservation).delete()
    db.query(models.Room).delete()
    db.query(models.Guest).delete()
    db.commit()
    # Add a room
    db.add(models.Room(number="101", type="single", price=100.0, is_available=1))
    db.commit()
    db.close()

def test_create_reservation():
    db = SessionLocal()
    guest = models.Guest(name="John Doe")
    db.add(guest)
    db.commit()
    db.refresh(guest)
    db.close()
    response = client.post("/reservations/", json={
        "guest_id": guest.id,
        "room_id": 1,
        "check_in": "2024-12-01",
        "check_out": "2024-12-05"
    })
    assert response.status_code == 200
    data = response.json()
    assert data["guest_id"] == guest.id
    assert data["room_id"] == 1
