package model;

public class Albergue {

    private int idAlbergue;

    private String nombre;
    private String direccion;
    private String municipio;
    private String provincia;
    private String pais;

    private String telefono;
    private String email;
    
    private String codigoEstablecimientoMir;
    private String idAlbergueNube;
    private String apiKey;
    private boolean sincronizacionActiva;
    private String fechaUltimaSincronizacion;
    
    private String apiBaseUrl;
    private String installId;
    private String installSecret;
    private String installRegisteredAt;
    
    private String horaApertura;
    private String horaCierre;
    private String fechaAperturaDesde;
    private String fechaAperturaHasta;
    private String observacionesApertura;
    

    public Albergue() {
    }


    public int getIdAlbergue() {
        return idAlbergue;
    }

    public void setIdAlbergue(int idAlbergue) {
        this.idAlbergue = idAlbergue;
    }
    

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


	public String getCodigoEstablecimientoMir() {
		return codigoEstablecimientoMir;
	}


	public void setCodigoEstablecimientoMir(String codigoEstablecimientoMir) {
		this.codigoEstablecimientoMir = codigoEstablecimientoMir;
	}


	public String getIdAlbergueNube() {
		return idAlbergueNube;
	}


	public void setIdAlbergueNube(String idAlbergueNube) {
		this.idAlbergueNube = idAlbergueNube;
	}


	public String getApiKey() {
		return apiKey;
	}


	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}


	public boolean isSincronizacionActiva() {
		return sincronizacionActiva;
	}


	public void setSincronizacionActiva(boolean sincronizacionActiva) {
		this.sincronizacionActiva = sincronizacionActiva;
	}


	public String getFechaUltimaSincronizacion() {
		return fechaUltimaSincronizacion;
	}


	public void setFechaUltimaSincronizacion(String fechaUltimaSincronizacion) {
		this.fechaUltimaSincronizacion = fechaUltimaSincronizacion;
	}

	public String getApiBaseUrl() { return apiBaseUrl; }
	public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }

	public String getInstallId() { return installId; }
	public void setInstallId(String installId) { this.installId = installId; }

	public String getInstallSecret() { return installSecret; }
	public void setInstallSecret(String installSecret) { this.installSecret = installSecret; }

	public String getInstallRegisteredAt() { return installRegisteredAt; }
	public void setInstallRegisteredAt(String installRegisteredAt) { this.installRegisteredAt = installRegisteredAt; }

	
	public String getHoraApertura() {
		return horaApertura;
	}


	public void setHoraApertura(String horaApertura) {
		this.horaApertura = horaApertura;
	}


	public String getHoraCierre() {
		return horaCierre;
	}


	public void setHoraCierre(String horaCierre) {
		this.horaCierre = horaCierre;
	}


	public String getFechaAperturaDesde() {
		return fechaAperturaDesde;
	}


	public void setFechaAperturaDesde(String fechaAperturaDesde) {
		this.fechaAperturaDesde = fechaAperturaDesde;
	}


	public String getFechaAperturaHasta() {
		return fechaAperturaHasta;
	}


	public void setFechaAperturaHasta(String fechaAperturaHasta) {
		this.fechaAperturaHasta = fechaAperturaHasta;
	}


	public String getObservacionesApertura() {
		return observacionesApertura;
	}


	public void setObservacionesApertura(String observacionesApertura) {
		this.observacionesApertura = observacionesApertura;
	}


	@Override
	public String toString() {
		return "Albergue [idAlbergue=" + idAlbergue + ", nombre=" + nombre + ", direccion=" + direccion + ", municipio="
				+ municipio + ", provincia=" + provincia + ", pais=" + pais + ", telefono=" + telefono + ", email="
				+ email + ", codigoEstablecimientoMir=" + codigoEstablecimientoMir + ", idAlbergueNube="
				+ idAlbergueNube + ", apiKey=" + apiKey + ", sincronizacionActiva=" + sincronizacionActiva
				+ ", fechaUltimaSincronizacion=" + fechaUltimaSincronizacion + ", apiBaseUrl=" + apiBaseUrl
				+ ", installId=" + installId + ", installSecret=" + installSecret + ", installRegisteredAt="
				+ installRegisteredAt + ", horaApertura=" + horaApertura + ", horaCierre=" + horaCierre
				+ ", fechaAperturaDesde=" + fechaAperturaDesde + ", fechaAperturaHasta=" + fechaAperturaHasta
				+ ", observacionesApertura=" + observacionesApertura + "]";
	}


	


}

