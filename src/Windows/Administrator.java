package Windows;

public class Administrator {
    private int idAdmin;
    private String name;
    private String email;

    // Constructor
    public Administrator(int idAdmin, String name, String email) {
        this.idAdmin = idAdmin;
        this.name = name;
        this.email = email;
    }

    // Getters y Setters
    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


