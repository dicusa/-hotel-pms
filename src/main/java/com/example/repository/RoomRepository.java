package com.example.repository;

import com.example.model.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class RoomRepository {
    private final EntityManager em;

    public RoomRepository(EntityManager em) {
        this.em = em;
    }

    public Room save(Room room) {
        em.getTransaction().begin();
        if (room.getId() == null) {
            em.persist(room);
        } else {
            room = em.merge(room);
        }
        em.getTransaction().commit();
        return room;
    }

    public Optional<Room> findById(Long id) {
        Room room = em.find(Room.class, id);
        return Optional.ofNullable(room);
    }

    public Optional<Room> findByNumber(String number) {
        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r WHERE r.number = :number", Room.class);
        query.setParameter("number", number);
        List<Room> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<Room> findAll() {
        TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
        return query.getResultList();
    }

    public void delete(Room room) {
        em.getTransaction().begin();
        Room managed = em.contains(room) ? room : em.merge(room);
        em.remove(managed);
        em.getTransaction().commit();
    }
}
