from pydantic import BaseModel
from datetime import date

class GuestCreate(BaseModel):
    name: str

class RoomCreate(BaseModel):
    number: str
    type: str
    price: float

class ReservationCreate(BaseModel):
    guest_id: int
    room_id: int
    check_in: date
    check_out: date

class ReservationOut(BaseModel):
    id: int
    guest_id: int
    room_id: int
    check_in: date
    check_out: date
    class Config:
        orm_mode = True

class InvoiceCreate(BaseModel):
    reservation_id: int
    amount: float

class InvoiceOut(BaseModel):
    id: int
    reservation_id: int
    amount: float
    class Config:
        orm_mode = True
