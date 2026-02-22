package model;

public class Habitacion {

    private int numeroHabitacion;
    private int idAlbergue;

    private int numeroCamas;
    private String estadoHabitacion;

    
    public Habitacion() {
    }

    
    public int getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(int numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public int getIdAlbergue() {
        return idAlbergue;
    }

    public void setIdAlbergue(int idAlbergue) {
        this.idAlbergue = idAlbergue;
    }

    public int getNumeroCamas() {
        return numeroCamas;
    }

    public void setNumeroCamas(int numeroCamas) {
        this.numeroCamas = numeroCamas;
    }

    public String getEstadoHabitacion() {
        return estadoHabitacion;
    }

    public void setEstadoHabitacion(String estadoHabitacion) {
        this.estadoHabitacion = estadoHabitacion;
    }

    @Override
    public String toString() {
        return "Habitacion [numeroHabitacion=" + numeroHabitacion
                + ", idAlbergue=" + idAlbergue
                + ", numeroCamas=" + numeroCamas
                + ", estadoHabitacion=" + estadoHabitacion + "]";
    }
}
