package com.mekongmobile.projectl4.repository;
import com.mekongmobile.projectl4.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface AppointmentRepository extends JpaRepository<Appointment,Long>{
 List<Appointment> findAllByOrderByStartTimeAsc();
 List<Appointment> findByTechnicianIdOrderByStartTimeAsc(Long technicianId);
 boolean existsByTechnicianIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(Long technicianId,LocalDateTime end,LocalDateTime start,List<String> statuses);
 boolean existsByTechnicianIdAndAppointmentIdNotAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(Long technicianId,Long appointmentId,LocalDateTime end,LocalDateTime start,List<String> statuses);
}
