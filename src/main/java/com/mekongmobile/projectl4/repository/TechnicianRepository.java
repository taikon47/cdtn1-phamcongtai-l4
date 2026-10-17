package com.mekongmobile.projectl4.repository;

import com.mekongmobile.projectl4.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {
}
