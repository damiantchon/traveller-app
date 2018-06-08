package tim.project.travellerapp.models;

public class Visit {
    private long id;
    private long date;
    private boolean visited;
    private boolean visible;
    private long userId;
    private long placeId;


    public Visit(long id, long timestamp, boolean visited, boolean visable, long userId, long placeId) {
        this.id = id;
        this.date = timestamp;
        this.visited = visited;
        this.visible = visable;
        this.userId = userId;
        this.placeId = placeId;
    }


    public long getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean isVisible() {
        return visible;
    }

    public long getUserId() {
        return userId;
    }

    public long getPlaceId() {
        return placeId;
    }
}
