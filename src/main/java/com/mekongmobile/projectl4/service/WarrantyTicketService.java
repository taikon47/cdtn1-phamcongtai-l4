package com.mekongmobile.projectl4.service;

import com.mekongmobile.projectl4.entity.WarrantyTicket;
import com.mekongmobile.projectl4.repository.WarrantyTicketRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WarrantyTicketService {
    private final WarrantyTicketRepository repository;
    public WarrantyTicketService(WarrantyTicketRepository repository) { this.repository = repository; }
    public List<WarrantyTicket> findByStatus(String status) { return repository.findByStatusIgnoreCase(status); }
}
