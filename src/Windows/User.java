package Windows;

public class User {
    private int idUser;
    private String username;
    private String password;
    private String email;
    private String role;
    private String department;

    // Constructor
    public User(int idUser, String username, String password, String email, String role, String department) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.department = department;
    }

    // Getters y Setters
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
