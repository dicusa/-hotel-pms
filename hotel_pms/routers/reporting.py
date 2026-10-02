from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from sqlalchemy import func
from .. import models
from ..models import SessionLocal
from datetime import date

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.get("/revenue")
def revenue_report(start: date, end: date, db: Session = Depends(get_db)):
    total = db.query(func.sum(models.Invoice.amount)).join(models.Reservation).filter(
        models.Reservation.check_in >= start,
        models.Reservation.check_out <= end
    ).scalar() or 0
    return {"start": start, "end": end, "total_revenue": total}
