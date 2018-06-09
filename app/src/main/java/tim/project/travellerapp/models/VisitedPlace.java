package tim.project.travellerapp.models;

public class VisitedPlace extends Place {

    private long visitId;



    private long timestamp;
    private boolean visited;
    private boolean visible;

    public VisitedPlace(long id, String name, String address, String gps, String description, boolean accepted, boolean active, long userId, String username) {
        super(id, name, address, gps, description, accepted, active, userId, username);
    }

    public VisitedPlace(long id, String name, String address, String gps, String description, boolean accepted, boolean active, long userId, String username, long visitId, long timestamp, boolean visited, boolean visible) {
        super(id, name, address, gps, description, accepted, active, userId, username);
        this.visitId = visitId;
        this.timestamp = timestamp;
        this.visited = visited;
        this.visible = visible;
    }

    public VisitedPlace(Place place) {
        super(place.getId(), place.getName(), place.getAddress(), place.getGps(), place.getDescription(), place.isAccepted(), place.isActive(), place.getUserId(), place.getUsername());
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public long getVisitId() {
        return visitId;
    }

    public void setVisitId(long visitId) {
        this.visitId = visitId;
    }
}
