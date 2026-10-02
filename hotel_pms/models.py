from sqlalchemy import Column, Integer, String, Date, Float, ForeignKey, create_engine, func
from sqlalchemy.orm import declarative_base, relationship, sessionmaker

Base = declarative_base()
engine = create_engine("sqlite:///./hotel.db", connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

class Guest(Base):
    __tablename__ = "guests"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)

class Room(Base):
    __tablename__ = "rooms"
    id = Column(Integer, primary_key=True, index=True)
    number = Column(String, unique=True, nullable=False)
    type = Column(String, nullable=False)
    price = Column(Float, nullable=False)
    is_available = Column(Integer, default=1)

class Reservation(Base):
    __tablename__ = "reservations"
    id = Column(Integer, primary_key=True, index=True)
    guest_id = Column(Integer, ForeignKey("guests.id"))
    room_id = Column(Integer, ForeignKey("rooms.id"))
    check_in = Column(Date, nullable=False)
    check_out = Column(Date, nullable=False)
    guest = relationship("Guest")
    room = relationship("Room")

class Invoice(Base):
    __tablename__ = "invoices"
    id = Column(Integer, primary_key=True, index=True)
    reservation_id = Column(Integer, ForeignKey("reservations.id"))
    amount = Column(Float, nullable=False)
    reservation = relationship("Reservation")

def init_db():
    Base.metadata.create_all(bind=engine)
