package tim.project.travellerapp.models.request_body_models;

public class NewPlace {

    private String name;
    private String address;
    private String gps;
    private String description;
    private long userId;

    public NewPlace(String name, String address, String gps, String description, long userId) {
        this.name = name;
        this.address = address;
        this.gps = gps;
        this.description = description;
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
