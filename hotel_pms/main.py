from fastapi import FastAPI
from .routers import reservation, room, billing, reporting

app = FastAPI(title="Hotel PMS API")

app.include_router(reservation.router, prefix="/reservations", tags=["reservations"])
app.include_router(room.router, prefix="/rooms", tags=["rooms"])
app.include_router(billing.router, prefix="/billing", tags=["billing"])
app.include_router(reporting.router, prefix="/reporting", tags=["reporting"])

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("hotel_pms.main:app", host="0.0.0.0", port=8000, reload=True)
