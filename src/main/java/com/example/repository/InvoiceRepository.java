package com.example.repository;

import com.example.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT COALESCE(SUM(i.totalAmount),0) FROM Invoice i WHERE i.paymentDate = :date")
    double sumTotalAmountByPaymentDate(@Param("date") LocalDate date);
}
