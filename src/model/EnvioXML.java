package model;

public class EnvioXML {

    private int idEnvioXml;

    private String fechaEnvio;
    private String rutaFicheroXml;
    private String estadoEnvio;

    public EnvioXML() {
    }

    public int getIdEnvioXml() {
        return idEnvioXml;
    }

    public void setIdEnvioXml(int idEnvioXml) {
        this.idEnvioXml = idEnvioXml;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getRutaFicheroXml() {
        return rutaFicheroXml;
    }

    public void setRutaFicheroXml(String rutaFicheroXml) {
        this.rutaFicheroXml = rutaFicheroXml;
    }

    public String getEstadoEnvio() {
        return estadoEnvio;
    }

    public void setEstadoEnvio(String estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
    }

    @Override
    public String toString() {
        return "EnvioXML [idEnvioXml=" + idEnvioXml
                + ", fechaEnvio=" + fechaEnvio
                + ", rutaFicheroXml=" + rutaFicheroXml
                + ", estadoEnvio=" + estadoEnvio + "]";
    }
}
