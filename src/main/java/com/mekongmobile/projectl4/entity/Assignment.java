package com.mekongmobile.projectl4.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="assignment")
public class Assignment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="assignment_id") private Long assignmentId;
 @Column(name="ticket_id",nullable=false,unique=true) private Long ticketId;
 @Column(name="technician_id",nullable=false) private Long technicianId;
 @Column(name="assigned_at",nullable=false,updatable=false) private LocalDateTime assignedAt;
 @Column(nullable=false,length=30) private String status="ASSIGNED";
 @PrePersist void prePersist(){if(assignedAt==null)assignedAt=LocalDateTime.now();if(status==null)status="ASSIGNED";}
 public Long getAssignmentId(){return assignmentId;} public Long getTicketId(){return ticketId;}
 public void setTicketId(Long v){ticketId=v;} public Long getTechnicianId(){return technicianId;}
 public void setTechnicianId(Long v){technicianId=v;} public LocalDateTime getAssignedAt(){return assignedAt;}
 public String getStatus(){return status;}
}
