package tim.project.travellerapp.models.request_body_models;

public class VisitVisited {
    private String date;
    private long visitId;
    private long userId;

    public VisitVisited(String date, long visitId, long userId) {
        this.date = date;
        this.visitId = visitId;
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public long getVisitId() {
        return visitId;
    }

    public long getUserId() {
        return userId;
    }
}
