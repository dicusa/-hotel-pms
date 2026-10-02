import pytest
from hotel_pms.models import init_db

@pytest.fixture(scope="session", autouse=True)
def setup_db():
    init_db()
