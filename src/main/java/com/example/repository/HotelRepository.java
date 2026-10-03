package com.example.repository;

import com.example.model.Hotel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class HotelRepository {
    private final EntityManager em;

    public HotelRepository(EntityManager em) {
        this.em = em;
    }

    public Hotel save(Hotel hotel) {
        em.getTransaction().begin();
        if (hotel.getId() == null) {
            em.persist(hotel);
        } else {
            hotel = em.merge(hotel);
        }
        em.getTransaction().commit();
        return hotel;
    }

    public Optional<Hotel> findById(Long id) {
        Hotel hotel = em.find(Hotel.class, id);
        return Optional.ofNullable(hotel);
    }

    public List<Hotel> findAll() {
        TypedQuery<Hotel> query = em.createQuery("SELECT h FROM Hotel h", Hotel.class);
        return query.getResultList();
    }

    public void delete(Hotel hotel) {
        em.getTransaction().begin();
        Hotel managed = em.contains(hotel) ? hotel : em.merge(hotel);
        em.remove(managed);
        em.getTransaction().commit();
    }
}