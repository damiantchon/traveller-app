package tim.project.travellerapp.models.request_body_models;

public class NewVisit {
    private long userId;
    private long placeId;

    public NewVisit(long userId, long placeId) {
        this.userId = userId;
        this.placeId = placeId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }
}
