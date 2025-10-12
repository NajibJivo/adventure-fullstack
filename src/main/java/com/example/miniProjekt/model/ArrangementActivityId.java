package com.example.miniProjekt.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
/** Komposit nøgle **/
@Embeddable
public class ArrangementActivityId implements Serializable {
    private Long arrangementId;
    private Long activityId;

    public ArrangementActivityId() {}

    public ArrangementActivityId(Long arrangementId, Long activityId) {
        this.arrangementId = arrangementId;
        this.activityId = activityId;
    }

    public Long getArrangementId() {
        return arrangementId;
    }
    public void setArrangementId(Long arrangementId) {
        this.arrangementId = arrangementId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrangementActivityId that)) return false;
        return Objects.equals(arrangementId, that.arrangementId)
                && Objects.equals(activityId, that.activityId);
    }
    @Override public int hashCode() { return Objects.hash(arrangementId, activityId); }
}
