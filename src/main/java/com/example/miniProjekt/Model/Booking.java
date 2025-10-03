package com.example.miniProjekt.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

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

    // Gemmer tidspunktet for oprettelse af bookingen.
    // Updatable = false betyder, at værdien ikke ændres ved senere opdateringer.
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ReservationType type = ReservationType.PRIVATE;

    private String notes;

    // Many reservations can belong to one activity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    // Tom constructor for nødvendig frameworks og “default” initialisering.
    public Booking() {
    }

    // Denne metode sætter automatisk createdAt, når objektet bliver gemt første gang
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    //Fuldt constructor praktisk når man manuelt vil oprette et objekt med alle værdier.
 public Booking(String customerName, String customerPhone, String customerEmail,
                   Integer participantCount, LocalDateTime reservationTime, Activity activity) {
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
}
