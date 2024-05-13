package Windows;

public class Material {
    private int idMaterial;
    private String name;
    private int quantity;
    private int idLab;

    // Constructor
    public Material(int idMaterial, String name, int quantity, int idLab) {
        this.idMaterial = idMaterial;
        this.name = name;
        this.quantity = quantity;
        this.idLab = idLab;
    }

    // Getters y Setters
    public int getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(int idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getIdLab() {
        return idLab;
    }

    public void setIdLab(int idLab) {
        this.idLab = idLab;
    }
}
