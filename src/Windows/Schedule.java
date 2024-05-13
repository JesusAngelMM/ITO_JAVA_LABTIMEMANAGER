package Windows;

public class Schedule {
    private int idSchedule;
    private java.sql.Date date;
    private java.sql.Time startTime;
    private java.sql.Time endTime;

    // Constructor
    public Schedule(int idSchedule, java.sql.Date date, java.sql.Time startTime, java.sql.Time endTime) {
        this.idSchedule = idSchedule;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters y Setters
    public int getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(int idSchedule) {
        this.idSchedule = idSchedule;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    public java.sql.Time getStartTime() {
        return startTime;
    }

    public void setStartTime(java.sql.Time startTime) {
        this.startTime = startTime;
    }

    public java.sql.Time getEndTime() {
        return endTime;
    }

    public void setEndTime(java.sql.Time endTime) {
        this.endTime = endTime;
    }
}

