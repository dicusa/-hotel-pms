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

@router.post("/", response_model=schemas.InvoiceOut)
def create_invoice(inv: schemas.InvoiceCreate, db: Session = Depends(get_db)):
    reservation = db.query(models.Reservation).filter(models.Reservation.id == inv.reservation_id).first()
    if not reservation:
        raise HTTPException(status_code=404, detail="Reservation not found")
    invoice = models.Invoice(**inv.dict())
    db.add(invoice)
    db.commit()
    db.refresh(invoice)
    return invoice
