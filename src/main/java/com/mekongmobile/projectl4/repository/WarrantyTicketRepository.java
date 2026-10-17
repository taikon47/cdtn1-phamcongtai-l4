package com.mekongmobile.projectl4.repository;
import com.mekongmobile.projectl4.entity.WarrantyTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface WarrantyTicketRepository extends JpaRepository<WarrantyTicket,Long>{
 List<WarrantyTicket> findByStatusIgnoreCase(String status);
}
