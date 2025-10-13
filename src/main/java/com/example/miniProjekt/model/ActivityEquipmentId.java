package com.example.miniProjekt.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ActivityEquipmentId implements Serializable {
    private Long activityId;
    private Long equipmentId;

    public ActivityEquipmentId() {
    }

    public ActivityEquipmentId(Long activityId, Long equipmentId) {
        this.activityId = activityId;
        this.equipmentId = equipmentId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActivityEquipmentId that)) return false;
        return Objects.equals(activityId, that.activityId)
                && Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, equipmentId);
    }


}
