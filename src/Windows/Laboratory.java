package Windows;

public class Laboratory {
    private int idLab;
    private String name;
    private String location;
    private int capacity;
    private String type;

    // Constructor
    public Laboratory(int idLab, String name, String location, int capacity, String type) {
        this.idLab = idLab;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.type = type;
    }

    // Getters y Setters
    public int getIdLab() {
        return idLab;
    }

    public void setIdLab(int idLab) {
        this.idLab = idLab;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

