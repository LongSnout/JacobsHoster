package model;

public class Prerregistro {

    private int idPrerregistro;

    private int idAlbergue;
    private Integer idPeregrino; // puede ser null

    private String idPreregistroNube;

    private String fechaEnvio;
    private String fechaPrevistaLlegada;

    private String estadoPrerregistro; // por defecto "PENDIENTE"

    
    private String rol; // por defecto "VI"
    private String nombre;
    private String apellido1;
    private String apellido2;

    private String tipoDocumento;
    private String numeroDocumento;

    private String fechaNacimiento;
    private String nacionalidad;
    private String sexo;

    private String direccion;
    private String direccionComplementaria;

    private String codigoMunicipio;
    private String nombreMunicipio;

    private String codigoPostal;
    private String pais;

    private String telefono1;
    private String telefono2;
    private String correo;

    private String parentesco;

    public Prerregistro() {
    }

    public int getIdPrerregistro() {
        return idPrerregistro;
    }

    public void setIdPrerregistro(int idPreregistro) {
        this.idPrerregistro = idPreregistro;
    }

    public int getIdAlbergue() {
        return idAlbergue;
    }

    public void setIdAlbergue(int idAlbergue) {
        this.idAlbergue = idAlbergue;
    }

    public Integer getIdPeregrino() {
        return idPeregrino;
    }

    public void setIdPeregrino(Integer idPeregrino) {
        this.idPeregrino = idPeregrino;
    }

    public String getIdPrerregistroNube() {
        return idPreregistroNube;
    }

    public void setIdPrerregistroNube(String idPreregistroNube) {
        this.idPreregistroNube = idPreregistroNube;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getFechaPrevistaLlegada() {
        return fechaPrevistaLlegada;
    }

    public void setFechaPrevistaLlegada(String fechaPrevistaLlegada) {
        this.fechaPrevistaLlegada = fechaPrevistaLlegada;
    }

    public String getEstadoPrerregistro() {
        return estadoPrerregistro;
    }

    public void setEstadoPrerregistro(String estadoPreregistro) {
        this.estadoPrerregistro = estadoPreregistro;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDireccionComplementaria() {
        return direccionComplementaria;
    }

    public void setDireccionComplementaria(String direccionComplementaria) {
        this.direccionComplementaria = direccionComplementaria;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    @Override
    public String toString() {
        return "Prerregistro [idPrerregistro=" + idPrerregistro
                + ", idAlbergue=" + idAlbergue
                + ", idPeregrino=" + idPeregrino
                + ", estadoPrerregistro=" + estadoPrerregistro
                + ", fechaPrevistaLlegada=" + fechaPrevistaLlegada
                + ", tipoDocumento=" + tipoDocumento
                + ", numeroDocumento=" + numeroDocumento + "]";
    }
}
