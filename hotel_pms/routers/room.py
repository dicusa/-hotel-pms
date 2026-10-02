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

@router.post("/", response_model=schemas.RoomCreate)
def create_room(room: schemas.RoomCreate, db: Session = Depends(get_db)):
    db_room = models.Room(**room.dict())
    db.add(db_room)
    db.commit()
    db.refresh(db_room)
    return room

@router.get("/", response_model=list[schemas.RoomCreate])
def list_rooms(db: Session = Depends(get_db)):
    rooms = db.query(models.Room).all()
    return [schemas.RoomCreate(number=r.number, type=r.type, price=r.price) for r in rooms]
