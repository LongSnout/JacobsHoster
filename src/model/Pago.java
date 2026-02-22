package model;

public class Pago {

    private int idPago;
    private int idEstancia;

    private String tipoPago;        // EFECTIVO, TARJETA, etc.
    private String fechaPago;       // ISO-8601 (String)

    private String medioPago;
    private String titularPago;
    private String caducidadTarjeta; // mm/aaaa (String)

    public Pago() {
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdEstancia() {
        return idEstancia;
    }

    public void setIdEstancia(int idEstancia) {
        this.idEstancia = idEstancia;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getTitularPago() {
        return titularPago;
    }

    public void setTitularPago(String titularPago) {
        this.titularPago = titularPago;
    }

    public String getCaducidadTarjeta() {
        return caducidadTarjeta;
    }

    public void setCaducidadTarjeta(String caducidadTarjeta) {
        this.caducidadTarjeta = caducidadTarjeta;
    }

    @Override
    public String toString() {
        return "Pago [idPago=" + idPago
                + ", idEstancia=" + idEstancia
                + ", tipoPago=" + tipoPago
                + ", fechaPago=" + fechaPago
                + ", medioPago=" + medioPago + "]";
    }
}

