package com.example.miniProjekt.model;


import jakarta.persistence.*;

@Entity
@Table(name = "activity_equipment") // unikhed dækkes af PK
public class ActivityEquipment {
    @EmbeddedId
    private ActivityEquipmentId  id;

    // FK -> activity(id)
    @MapsId("activityId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    // FK -> equipment(id)
    @MapsId("equipmentId")
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
        this.id = new ActivityEquipmentId(activity.getId(), equipment.getId());
    }


    /** -------- getters & setters -------- */

    public ActivityEquipmentId getId() {
        return id;
    }

    public void setId(ActivityEquipmentId id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        if (activity != null) {
            if (this.id == null) this.id = new ActivityEquipmentId();
            this.id.setActivityId(activity.getId());
        }
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
        if (equipment != null) {
            if (this.id == null) this.id = new ActivityEquipmentId();
            this.id.setEquipmentId(equipment.getId());
        }
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
