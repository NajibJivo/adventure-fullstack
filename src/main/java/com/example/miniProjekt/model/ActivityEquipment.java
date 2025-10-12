package com.example.miniProjekt.model;


import jakarta.persistence.*;

@Entity
@Table(name = "activity_equipment")
public class ActivityEquipment {
    @EmbeddedId
    private ActivityEquipmentId id = new ActivityEquipmentId();

    @ManyToOne(optional = false)
    @MapsId("activityId")
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @ManyToOne(optional = false)
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    public ActivityEquipment() {
    }

    public ActivityEquipment( Activity activity, Equipment equipment) {
        this.activity = activity;
        this.equipment = equipment;
        this.id = new ActivityEquipmentId(activity.getId(), equipment.getId());
    }

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
        if (activity != null) this.id.setActivityId(activity.getId());

    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
        if (equipment != null) this.id.setEquipmentId(equipment.getId());
    }
}
