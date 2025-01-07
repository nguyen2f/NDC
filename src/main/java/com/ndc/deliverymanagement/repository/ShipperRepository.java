package com.ndc.deliverymanagement.repository;

import com.ndc.deliverymanagement.model.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    Shipper findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    List<Shipper> findByStatus(String status);
}
