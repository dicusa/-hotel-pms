package com.example.repository;

import com.example.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HotelDataModelCrudTest {

    @Autowired GuestRepository guestRepo;
    @Autowired RoomRepository roomRepo;
    @Autowired ReservationRepository reservationRepo;
    @Autowired InvoiceRepository invoiceRepo;

    @Test
    void guestCrud() {
        Guest g = new Guest("John", "Doe", "john@example.com", "1234567890");
        g = guestRepo.save(g);
        assertThat(g.getId()).isNotNull();
        Guest fetched = guestRepo.findById(g.getId()).orElseThrow();
        assertThat(fetched.getEmail()).isEqualTo("john@example.com");
        fetched.setEmail("john.doe@example.com");
        guestRepo.save(fetched);
        assertThat(guestRepo.findById(g.getId()).orElseThrow().getEmail()).isEqualTo("john.doe@example.com");
        guestRepo.delete(fetched);
        assertThat(guestRepo.findById(g.getId())).isEmpty();
    }

    @Test
    void roomCrud() {
        Room r = new Room("101", "Single", 100.0, true);
        r = roomRepo.save(r);
        assertThat(r.getId()).isNotNull();
        Room fetched = roomRepo.findById(r.getId()).orElseThrow();
        assertThat(fetched.getNumber()).isEqualTo("101");
        fetched.setAvailable(false);
        roomRepo.save(fetched);
        assertThat(roomRepo.findById(r.getId()).orElseThrow().getAvailable()).isFalse();
        roomRepo.delete(fetched);
        assertThat(roomRepo.findById(r.getId())).isEmpty();
    }

    @Test
    void reservationCrud() {
        Guest g = guestRepo.save(new Guest("Alice", "Smith", "alice@example.com", "5551234"));
        Room r = roomRepo.save(new Room("202", "Double", 150.0, true));
        Reservation res = new Reservation(g, r, new Date(), new Date(System.currentTimeMillis()+86400000), 150.0);
        res = reservationRepo.save(res);
        assertThat(res.getId()).isNotNull();
        Reservation fetched = reservationRepo.findById(res.getId()).orElseThrow();
        assertThat(fetched.getGuest().getEmail()).isEqualTo("alice@example.com");
        fetched.setTotalAmount(200.0);
        reservationRepo.save(fetched);
        assertThat(reservationRepo.findById(res.getId()).orElseThrow().getTotalAmount()).isEqualTo(200.0);
        reservationRepo.delete(fetched);
        assertThat(reservationRepo.findById(res.getId())).isEmpty();
    }

    @Test
    void invoiceCrud() {
        Guest g = guestRepo.save(new Guest("Bob", "Brown", "bob@example.com", "7778889"));
        Room r = roomRepo.save(new Room("303", "Suite", 300.0, true));
        Reservation res = reservationRepo.save(new Reservation(g, r, new Date(), new Date(System.currentTimeMillis()+86400000), 300.0));
        Invoice inv = new Invoice(res, new Date(), 300.0, false);
        inv = invoiceRepo.save(inv);
        assertThat(inv.getId()).isNotNull();
        Invoice fetched = invoiceRepo.findById(inv.getId()).orElseThrow();
        assertThat(fetched.getPaid()).isFalse();
        fetched.setPaid(true);
        invoiceRepo.save(fetched);
        assertThat(invoiceRepo.findById(inv.getId()).orElseThrow().getPaid()).isTrue();
        invoiceRepo.delete(fetched);
        assertThat(invoiceRepo.findById(inv.getId())).isEmpty();
    }
}
