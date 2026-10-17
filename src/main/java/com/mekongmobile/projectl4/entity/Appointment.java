package com.mekongmobile.projectl4.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="appointment")
public class Appointment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="appointment_id") private Long appointmentId;
 @Column(name="ticket_id",nullable=false) private Long ticketId;
 @Column(name="technician_id",nullable=false) private Long technicianId;
 @Column(name="appointment_type",nullable=false,length=20) private String appointmentType;
 @Column(name="start_time",nullable=false) private LocalDateTime startTime;
 @Column(name="end_time",nullable=false) private LocalDateTime endTime;
 @Column(nullable=false,length=30) private String status="PENDING";
 @Column(name="created_at",nullable=false,updatable=false) private LocalDateTime createdAt;
 @PrePersist void prePersist(){if(status==null)status="PENDING";if(createdAt==null)createdAt=LocalDateTime.now();}
 public Long getAppointmentId(){return appointmentId;} public Long getTicketId(){return ticketId;}
 public void setTicketId(Long v){ticketId=v;} public Long getTechnicianId(){return technicianId;}
 public void setTechnicianId(Long v){technicianId=v;} public String getAppointmentType(){return appointmentType;}
 public void setAppointmentType(String v){appointmentType=v;} public LocalDateTime getStartTime(){return startTime;}
 public void setStartTime(LocalDateTime v){startTime=v;} public LocalDateTime getEndTime(){return endTime;}
 public void setEndTime(LocalDateTime v){endTime=v;} public String getStatus(){return status;}
 public void setStatus(String v){status=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
