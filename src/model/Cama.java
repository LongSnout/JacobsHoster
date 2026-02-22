package model;

public class Cama {

    private int idCama;
    private int numeroHabitacion;

    private String estado;// LIBRE, OCUPADA, BLOQUEADA


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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Cama{" +
                "idCama=" + idCama +
                ", numeroHabitacion=" + numeroHabitacion +
                ", estado='" + estado + '\'' +
                '}';
    }
}
