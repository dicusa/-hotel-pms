package com.example.repository;

import com.example.model.Hotel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HotelRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private HotelRepository repo;

    @BeforeAll
    static void initAll() {
        emf = Persistence.createEntityManagerFactory("hotelPU");
    }

    @AfterAll
    static void closeAll() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void init() {
        em = emf.createEntityManager();
        repo = new HotelRepository(em);
    }

    @AfterEach
    void cleanup() {
        if (em != null) {
            em.close();
        }
    }

    @Test
    void crudOperations() {
        // Create
        Hotel hotel = new Hotel("Grand Plaza", "123 Main St");
        repo.save(hotel);
        assertNotNull(hotel.getId());

        // Read
        Optional<Hotel> fetched = repo.findById(hotel.getId());
        assertTrue(fetched.isPresent());
        assertEquals("Grand Plaza", fetched.get().getName());

        // Update
        fetched.get().setName("Grand Plaza Updated");
        repo.save(fetched.get());
        Optional<Hotel> updated = repo.findById(hotel.getId());
        assertTrue(updated.isPresent());
        assertEquals("Grand Plaza Updated", updated.get().getName());

        // Find all
        List<Hotel> all = repo.findAll();
        assertFalse(all.isEmpty());

        // Delete
        repo.delete(updated.get());
        Optional<Hotel> afterDelete = repo.findById(hotel.getId());
        assertTrue(afterDelete.isEmpty());
    }
}