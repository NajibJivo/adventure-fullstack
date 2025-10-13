package com.example.miniProjekt.model;

import jakarta.persistence.*;

@Entity
@Table(name = "arrangement_activity")
public class ArrangementActivity {

    @EmbeddedId
    private ArrangementActivityId id = new ArrangementActivityId();

    @ManyToOne(optional = false)
    @MapsId("arrangementId")
    @JoinColumn(name = "arrangement_id")
    private Arrangement arrangement;


    @ManyToOne(optional = false)
    @MapsId("activityId")
    @JoinColumn(name = "activity_id")
    private Activity activity;


    public ArrangementActivity() {
    }

    public ArrangementActivity(Arrangement arrangement, Activity activity) {
        this.arrangement = arrangement;
        this.activity = activity;
        this.id =  new ArrangementActivityId(arrangement.getId(), activity.getId());
    }

    public ArrangementActivityId getId() {
        return id;
    }

    public void setId(ArrangementActivityId id) {
        this.id = id;
    }

    public Arrangement getArrangement() {
        return arrangement;
    }

    public void setArrangement(Arrangement arrangement) {
        this.arrangement = arrangement;
        if (arrangement != null) this.id.setArrangementId(arrangement.getId());
    }

    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
        if (activity != null) this.id.setActivityId(activity.getId());
    }
}
