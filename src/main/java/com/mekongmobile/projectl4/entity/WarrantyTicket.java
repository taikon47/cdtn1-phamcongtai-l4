package com.mekongmobile.projectl4.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="warranty_ticket")
public class WarrantyTicket {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="ticket_id") private Long ticketId;
 @Column(name="ticket_code",nullable=false,unique=true,length=30) private String ticketCode;
 @Column(name="customer_name",nullable=false,length=120) private String customerName;
 @Column(name="device_name",nullable=false,length=120) private String deviceName;
 @Column(name="issue_description") private String issueDescription;
 @Column(name="required_skill",nullable=false,length=100) private String requiredSkill;
 @Column(name="service_area",nullable=false,length=100) private String serviceArea;
 @Column(nullable=false,length=30) private String status="UNASSIGNED";
 @Column(name="created_at",nullable=false,updatable=false) private LocalDateTime createdAt;
 @PrePersist void prePersist(){if(status==null)status="UNASSIGNED";if(createdAt==null)createdAt=LocalDateTime.now();}
 public Long getTicketId(){return ticketId;} public String getTicketCode(){return ticketCode;}
 public String getCustomerName(){return customerName;} public String getDeviceName(){return deviceName;}
 public String getIssueDescription(){return issueDescription;} public String getRequiredSkill(){return requiredSkill;}
 public String getServiceArea(){return serviceArea;} public String getStatus(){return status;}
 public void setStatus(String v){status=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
