package model;

public class Cama {

    private int idCama;
    private int numeroHabitacion;
    private int numeroCama;
    private String estado; // LIBRE, OCUPADA, BLOQUEADA
    private boolean activa;

    public Cama() {
    }

    public int getIdCama() {
        return idCama;
    }

    public void setIdCama(int idCama) {
        this.idCama = idCama;
    }

    public int getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(int numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public int getNumeroCama() {
        return numeroCama;
    }

    public void setNumeroCama(int numeroCama) {
        this.numeroCama = numeroCama;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return "Cama{" +
                "idCama=" + idCama +
                ", numeroHabitacion=" + numeroHabitacion +
                ", numeroCama=" + numeroCama +
                ", estado='" + estado + '\'' +
                ", activa=" + activa +
                '}';
    }
}