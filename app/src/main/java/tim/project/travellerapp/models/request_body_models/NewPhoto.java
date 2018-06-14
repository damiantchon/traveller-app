package tim.project.travellerapp.models.request_body_models;

public class NewPhoto {
    private byte[] file;
    private String date;
    private long userId;
    private long placeId;

    public NewPhoto(byte[] file, String date, long userId, long placeId) {
        this.file = file;
        this.date = date;
        this.userId = userId;
        this.placeId = placeId;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }
}
