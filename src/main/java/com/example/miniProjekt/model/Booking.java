package com.example.miniProjekt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
//Angiver præcis, hvilken tabel klassen skal mappes til.
@Table(name = "bookings")

public class Booking {
    //primærnøgle
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "participant_count", nullable = false)
    private Integer participantCount;

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ReservationType type = ReservationType.PRIVATE;

    private String notes;

    //Many bookings can belong to one activity
    //Lazy fetch: activity data hentes først, når det eksplicit bruges
    @ManyToOne(fetch = FetchType.LAZY)
    //Peger på den kolonne i "bookings"-tabellen, der fungerer som fremmednøgle til "activities"-tabellen.
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    // Constructors
    public Booking() {
        this.createdAt = LocalDateTime.now();
    }

    public Booking(String customerName, String customerPhone, String customerEmail,
                   Integer participantCount, LocalDateTime reservationTime, Activity activity) {
        this();
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.participantCount = participantCount;
        this.reservationTime = reservationTime;
        this.activity = activity;
    }

    // Getters og Setters
    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }

    public String getCustomerName() {
        return customerName; }
    public void setCustomerName(String customerName) {
        this.customerName = customerName; }

    public String getCustomerPhone() {
        return customerPhone; }
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone; }

    public String getCustomerEmail() {
        return customerEmail; }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail; }

    public Integer getParticipantCount() {
        return participantCount; }
    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount; }

    public LocalDateTime getReservationTime() {
        return reservationTime; }
    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime; }

    public LocalDateTime getCreatedAt() {
        return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt; }

    public ReservationType getType() {
        return type; }
    public void setType(ReservationType type) {
        this.type = type; }

    public String getNotes() {
        return notes; }
    public void setNotes(String notes) {
        this.notes = notes; }

    public Activity getActivity() {
        return activity; }
    public void setActivity(Activity activity) {
        this.activity = activity; }

    // For bedre logging og debugging
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", reservationTime=" + reservationTime +
                ", type=" + type +
                '}';
    }

}

