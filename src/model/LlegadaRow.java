package model;

public class LlegadaRow {

    public enum TipoFila { PRERREGISTRO, ESTANCIA }

    private TipoFila tipoFila;

    // IDs
    private Integer idPrerregistro;
    private Integer idEstancia;
    private Integer idPeregrino;

    // Datos “visibles” (comunes)
    private String rol;
    private String nombre;
    private String apellido1;
    private String apellido2;

    private String tipoDocumento;
    private String numeroDocumento;

    private String nacionalidad;
    private String sexo;

    private String telefono1;
    private String correo;

    private String pais;

    // Para UI
    private boolean gris; // preregistro pendiente -> true

    public TipoFila getTipoFila() { return tipoFila; }
    public void setTipoFila(TipoFila tipoFila) { this.tipoFila = tipoFila; }

    public Integer getIdPrerregistro() { return idPrerregistro; }
    public void setIdPrerregistro(Integer idPrerregistro) { this.idPrerregistro = idPrerregistro; }

    public Integer getIdEstancia() { return idEstancia; }
    public void setIdEstancia(Integer idEstancia) { this.idEstancia = idEstancia; }

    public Integer getIdPeregrino() { return idPeregrino; }
    public void setIdPeregrino(Integer idPeregrino) { this.idPeregrino = idPeregrino; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido1() { return apellido1; }
    public void setApellido1(String apellido1) { this.apellido1 = apellido1; }

    public String getApellido2() { return apellido2; }
    public void setApellido2(String apellido2) { this.apellido2 = apellido2; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getTelefono1() { return telefono1; }
    public void setTelefono1(String telefono1) { this.telefono1 = telefono1; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public boolean isGris() { return gris; }
    public void setGris(boolean gris) { this.gris = gris; }
}
