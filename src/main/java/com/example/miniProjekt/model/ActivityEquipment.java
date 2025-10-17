package com.example.miniProjekt.model;


import jakarta.persistence.*;

@Entity
@Table(name = "activity_equipment", uniqueConstraints = @UniqueConstraint(
        name = "uk_activity_equipment_activity_equipment",
        columnNames = {"activity_id", "equipment_id"})
)
public class ActivityEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK -> activity(id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    // FK -> equipment(id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** JPA kræver en no-args constructor */
    public ActivityEquipment() {
    }

    /** Bekvem constructor uden id (bruges ved create) */
    public ActivityEquipment(Activity activity, Equipment equipment, Integer quantity) {
        this.activity = activity;
        this.equipment = equipment;
        this.quantity = quantity;
    }

    /** Full constructor (sjældent nødvendig i app-kode) */
    public ActivityEquipment(Long id, Activity activity, Equipment equipment, Integer quantity) {
        this.id = id;
        this.activity = activity;
        this.equipment = equipment;
        this.quantity = quantity;
    }

    /** -------- getters & setters -------- */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
