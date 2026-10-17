package com.mekongmobile.projectl4.repository;
import com.mekongmobile.projectl4.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AssignmentRepository extends JpaRepository<Assignment,Long>{
 boolean existsByTicketId(Long ticketId);
}
