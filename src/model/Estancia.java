package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import dao.EstanciaDAO;
import dao.PeregrinoDAO;
import db.DBManager;
import exception.DatabaseException;

public class Estancia {

    private int idEstancia;

    // FKs (nullable)
    private Integer idEnvioXml;
    private Integer idCama;

    // FKs obligatorias
    private int idAlbergue;
    private int idPeregrino;

    // Fechas (ISO-8601 en String)
    private String fechaContrato;
    private String fechaEntrada;
    private String fechaSalidaPrevista;
    private String fechaSalidaReal;

    // Datos negocio
    private int numeroHabitaciones;
    private String idGrupo;
    private boolean internetIncluido;

    private String referenciaContrato;
    private String estadoEstancia;
    
    private String lugarInicioCamino;
    private String ultimoAlbergue;
    private String caminoDestino;
    private String observaciones;
    
    // Datos de pago
    private Integer numPersonasContrato;
    private String tipoPago;
    private String titularPago;
    private String caducidadTarjeta;
    private String fechaPago;
    private String medioPago;

    public Estancia() {
    }

    public int getIdEstancia() {
        return idEstancia;
    }

    public void setIdEstancia(int idEstancia) {
        this.idEstancia = idEstancia;
    }

    public Integer getIdEnvioXml() {
        return idEnvioXml;
    }

    public void setIdEnvioXml(Integer idEnvioXml) {
        this.idEnvioXml = idEnvioXml;
    }

    public Integer getIdCama() {
        return idCama;
    }

    public void setIdCama(Integer idCama) {
        this.idCama = idCama;
    }

    public int getIdAlbergue() {
        return idAlbergue;
    }

    public void setIdAlbergue(int idAlbergue) {
        this.idAlbergue = idAlbergue;
    }

    public int getIdPeregrino() {
        return idPeregrino;
    }

    public void setIdPeregrino(int idPeregrino) {
        this.idPeregrino = idPeregrino;
    }

    public String getFechaContrato() {
        return fechaContrato;
    }

    public void setFechaContrato(String fechaContrato) {
        this.fechaContrato = fechaContrato;
    }

    public String getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(String fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public String getFechaSalidaPrevista() {
        return fechaSalidaPrevista;
    }

    public void setFechaSalidaPrevista(String fechaSalidaPrevista) {
        this.fechaSalidaPrevista = fechaSalidaPrevista;
    }

    public String getFechaSalidaReal() {
        return fechaSalidaReal;
    }

    public void setFechaSalidaReal(String fechaSalidaReal) {
        this.fechaSalidaReal = fechaSalidaReal;
    }

    public int getNumeroHabitaciones() {
        return numeroHabitaciones;
    }

    public void setNumeroHabitaciones(int numeroHabitaciones) {
        this.numeroHabitaciones = numeroHabitaciones;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }

    public boolean isInternetIncluido() {
        return internetIncluido;
    }

    public void setInternetIncluido(boolean internetIncluido) {
        this.internetIncluido = internetIncluido;
    }

    public String getReferenciaContrato() {
        return referenciaContrato;
    }

    public void setReferenciaContrato(String referenciaContrato) {
        this.referenciaContrato = referenciaContrato;
    }

    public String getEstadoEstancia() {
        return estadoEstancia;
    }

    public void setEstadoEstancia(String estadoEstancia) {
        this.estadoEstancia = estadoEstancia;
    }
    
    public String getLugarInicioCamino() {
        return lugarInicioCamino;
    }

    public void setLugarInicioCamino(String lugarInicioCamino) {
        this.lugarInicioCamino = lugarInicioCamino;
    }

    public String getUltimoAlbergue() {
        return ultimoAlbergue;
    }

    public void setUltimoAlbergue(String ultimoAlbergue) {
        this.ultimoAlbergue = ultimoAlbergue;
    }

    public String getCaminoDestino() {
        return caminoDestino;
    }

    public void setCaminoDestino(String caminoDestino) {
        this.caminoDestino = caminoDestino;
    }
    
    

    public Integer getNumPersonasContrato() {
        return numPersonasContrato;
    }

    public void setNumPersonasContrato(Integer numPersonasContrato) {
        this.numPersonasContrato = numPersonasContrato;
    }

	public String getTipoPago() {
		return tipoPago;
	}

	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
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
	
	public String getObservaciones() {
	    return observaciones;
	}

	public void setObservaciones(String observaciones) {
	    this.observaciones = observaciones;
	}

	@Override
    public String toString() {
        return "Estancia [idEstancia=" + idEstancia
                + ", idEnvioXml=" + idEnvioXml
                + ", idCama=" + idCama
                + ", idAlbergue=" + idAlbergue
                + ", idPeregrino=" + idPeregrino
                + ", fechaEntrada=" + fechaEntrada
                + ", fechaSalidaPrevista=" + fechaSalidaPrevista
                + ", fechaSalidaReal=" + fechaSalidaReal
                + ", estadoEstancia=" + estadoEstancia + "]"
        		+ ", numeroHabitaciones=" + numeroHabitaciones
				+ ", idGrupo=" + idGrupo
				+ ", internetIncluido=" + internetIncluido
				+ ", referenciaContrato=" + referenciaContrato
				+ ", lugarInicioCamino=" + lugarInicioCamino
				+ ", ultimoAlbergue=" + ultimoAlbergue
				+ ", caminoDestino=" + caminoDestino
				+ ", numPersonasContrato=" + numPersonasContrato
				+ ", tipoPago=" + tipoPago
				+ ", titularPago=" + titularPago
				+ ", caducidadTarjeta=" + caducidadTarjeta
				+ ", fechaPago=" + fechaPago
				+ ", medioPago=" + medioPago
				+ "]";
    }
    
    
    public static java.util.List<model.Peregrino> listarPeregrinosPresentesPorDia(int idAlbergue, LocalDate fecha) {
        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.listarPresentesPorDia(conn, idAlbergue, fecha.toString());
        } catch (SQLException e) {
            throw new DatabaseException("Error al listar peregrinos presentes en fecha " + fecha, e);
        }
    }
    
    
}



