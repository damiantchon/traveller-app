package tim.project.travellerapp.models;

public class Visit {
    private long id;
    private long timestamp;
    private boolean visited;
    private boolean visable;
    private long userId;
    private long placeId;


    public Visit(long id, long timestamp, boolean visited, boolean visable, long userId, long placeId) {
        this.id = id;
        this.timestamp = timestamp;
        this.visited = visited;
        this.visable = visable;
        this.userId = userId;
        this.placeId = placeId;
    }


    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean isVisable() {
        return visable;
    }

    public long getUserId() {
        return userId;
    }

    public long getPlaceId() {
        return placeId;
    }
}
