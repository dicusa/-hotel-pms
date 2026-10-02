from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from .. import models, schemas
from ..models import SessionLocal

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/", response_model=schemas.ReservationOut)
def create_reservation(res: schemas.ReservationCreate, db: Session = Depends(get_db)):
    room = db.query(models.Room).filter(models.Room.id == res.room_id).first()
    if not room or not room.is_available:
        raise HTTPException(status_code=400, detail="Room not available")
    reservation = models.Reservation(**res.dict())
    db.add(reservation)
    room.is_available = 0
    db.commit()
    db.refresh(reservation)
    return reservation

@router.get("/{reservation_id}", response_model=schemas.ReservationOut)
def get_reservation(reservation_id: int, db: Session = Depends(get_db)):
    reservation = db.query(models.Reservation).filter(models.Reservation.id == reservation_id).first()
    if not reservation:
        raise HTTPException(status_code=404, detail="Reservation not found")
    return reservation
