package domain;

public class Rooms {
    private String id;
    private String capacity;

    public Rooms(String id, String capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public Rooms(String[] s) {
        this.id = s[0];
        this.capacity = s[1];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Rooms{" +
                "at1='" + id + '\'' +
                ", at2='" + capacity + '\'' +
                '}';
    }
}