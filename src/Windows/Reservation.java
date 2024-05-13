package Windows;

public class Reservation {
    private int idReservation;
    private int idUser;
    private int idLab;
    private int idSchedule;
    private String purpose;
    private String status;
    private String type;

    // Constructor
    public Reservation(int idReservation, int idUser, int idLab, int idSchedule, String purpose, String status, String type) {
        this.idReservation = idReservation;
        this.idUser = idUser;
        this.idLab = idLab;
        this.idSchedule = idSchedule;
        this.purpose = purpose;
        this.status = status;
        this.type = type;
    }

    // Getters y Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdLab() {
        return idLab;
    }

    public void setIdLab(int idLab) {
        this.idLab = idLab;
    }

    public int getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(int idSchedule) {
        this.idSchedule = idSchedule;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

