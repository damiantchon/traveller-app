package tim.project.travellerapp.models;

public class Place {
    private long id;
    private String name;
    private String address;
    private String gps;
    private String description;
    private boolean accepted;
    private boolean active;
    private long userId;
    private String username;

    public Place(long id, String name, String address, String gps, String description, boolean accepted, boolean active, long userId, String username) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.gps = gps;
        this.description = description;
        this.accepted = accepted;
        this.active = active;
        this.userId = userId;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getGps() {
        return gps;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isActive() {
        return active;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
