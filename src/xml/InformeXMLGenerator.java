package xml;

import model.Albergue;
import model.Estancia;
import model.Peregrino;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InformeXMLGenerator {

    /**
     * Genera el XML de alta de parte de hospedaje según el formato RD 933/2021
     * del Ministerio de Interior, para todos los peregrinos que entraron en una fecha.
     * Agrupa en una misma <comunicacion> a los peregrinos que comparten referencia de grupo.
     * Los peregrinos sin referencia de grupo van cada uno en su propia <comunicacion>.
     */
    public static String generarXMLParteDia(Albergue albergue,
                                             List<Estancia> estancias,
                                             List<Peregrino> peregrinos) {

        String codigoEstablecimiento = RD933Mapper.mapCodigoEstablecimiento(albergue);

        // Agrupar por referencia de grupo
        // Clave: referencia de grupo (o ID único para los sin grupo)
        // Valor: lista de pares (estancia, peregrino)
        Map<String, List<int[]>> grupos = new LinkedHashMap<>();

        for (int i = 0; i < estancias.size(); i++) {
            String refGrupo = estancias.get(i).getNumPersonasContrato();

            String clave;
            if (refGrupo == null || refGrupo.isBlank()) {
                // Sin grupo → clave única para que vaya solo
                clave = "__individual__" + i;
            } else {
                clave = refGrupo.trim();
            }

            grupos.computeIfAbsent(clave, k -> new ArrayList<>()).add(new int[]{i});
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<ns2:peticion xmlns:ns2=\"http://www.neg.hospedajes.mir.es/altaParteHospedaje\">\n");
        sb.append("  <solicitud>\n");
        sb.append("    <codigoEstablecimiento>").append(codigoEstablecimiento).append("</codigoEstablecimiento>\n");

        for (Map.Entry<String, List<int[]>> entry : grupos.entrySet()) {
            List<int[]> indices = entry.getValue();

            // Usamos la estancia y peregrino del primero del grupo para el contrato
            Estancia eRef = estancias.get(indices.get(0)[0]);
            int numPersonas = indices.size();

            sb.append("    <comunicacion>\n");

            // Contrato — datos del primero del grupo
            sb.append("      <contrato>\n");
            sb.append("        <referencia>").append(RD933Mapper.safe(eRef.getReferenciaContrato())).append("</referencia>\n");
            sb.append("        <fechaContrato>").append(RD933Mapper.mapFecha(eRef.getFechaContrato())).append("</fechaContrato>\n");
            sb.append("        <fechaEntrada>").append(RD933Mapper.mapFechaHora(eRef.getFechaEntrada())).append("</fechaEntrada>\n");

            String fechaSalida = eRef.getFechaSalidaReal() != null
                    ? eRef.getFechaSalidaReal()
                    : eRef.getFechaSalidaPrevista();
            sb.append("        <fechaSalida>").append(RD933Mapper.mapFechaHora(fechaSalida)).append("</fechaSalida>\n");

            sb.append("        <numPersonas>").append(numPersonas).append("</numPersonas>\n");
            sb.append("        <numHabitaciones>").append(RD933Mapper.mapNumHabitaciones(eRef)).append("</numHabitaciones>\n");
            sb.append("        <internet>").append(eRef.isInternetIncluido()).append("</internet>\n");

            // Pago
            sb.append("        <pago>\n");
            sb.append("          <tipoPago>").append(RD933Mapper.mapTipoPago(eRef)).append("</tipoPago>\n");
            if (!RD933Mapper.safe(eRef.getFechaPago()).isBlank()) {
                sb.append("          <fechaPago>").append(RD933Mapper.mapFecha(eRef.getFechaPago())).append("</fechaPago>\n");
            }
            if (!RD933Mapper.safe(eRef.getMedioPago()).isBlank()) {
                sb.append("          <medioPago>").append(RD933Mapper.safe(eRef.getMedioPago())).append("</medioPago>\n");
            }
            if (!RD933Mapper.safe(eRef.getTitularPago()).isBlank()) {
                sb.append("          <titular>").append(RD933Mapper.safe(eRef.getTitularPago())).append("</titular>\n");
            }
            if (!RD933Mapper.safe(eRef.getCaducidadTarjeta()).isBlank()) {
                sb.append("          <caducidadTarjeta>").append(RD933Mapper.safe(eRef.getCaducidadTarjeta())).append("</caducidadTarjeta>\n");
            }
            sb.append("        </pago>\n");
            sb.append("      </contrato>\n");

            // Una <persona> por cada miembro del grupo
            for (int[] idx : indices) {
                Peregrino p = peregrinos.get(idx[0]);
                String pais = RD933Mapper.safe(p.getPais());
                boolean esEspanya = "ESP".equalsIgnoreCase(pais);

                sb.append("      <persona>\n");
                sb.append("        <rol>").append(RD933Mapper.mapRol(p)).append("</rol>\n");
                sb.append("        <nombre>").append(RD933Mapper.safe(p.getNombre())).append("</nombre>\n");
                sb.append("        <apellido1>").append(RD933Mapper.safe(p.getApellido1())).append("</apellido1>\n");
                if (!RD933Mapper.safe(p.getApellido2()).isBlank()) {
                    sb.append("        <apellido2>").append(RD933Mapper.safe(p.getApellido2())).append("</apellido2>\n");
                }
                sb.append("        <tipoDocumento>").append(RD933Mapper.safe(p.getTipoDocumento())).append("</tipoDocumento>\n");
                sb.append("        <numeroDocumento>").append(RD933Mapper.safe(p.getNumeroDocumento())).append("</numeroDocumento>\n");
                if (!RD933Mapper.safe(p.getSoporteDocumento()).isBlank()) {
                    sb.append("        <soporteDocumento>").append(RD933Mapper.safe(p.getSoporteDocumento())).append("</soporteDocumento>\n");
                }
                sb.append("        <fechaNacimiento>").append(RD933Mapper.mapFecha(p.getFechaNacimiento())).append("</fechaNacimiento>\n");
                sb.append("        <nacionalidad>").append(RD933Mapper.safe(p.getNacionalidad())).append("</nacionalidad>\n");
                sb.append("        <sexo>").append(RD933Mapper.safe(p.getSexo())).append("</sexo>\n");

                // Dirección
                sb.append("        <direccion>\n");
                if (!RD933Mapper.safe(p.getDireccion()).isBlank()) {
                    sb.append("          <direccion>").append(RD933Mapper.safe(p.getDireccion())).append("</direccion>\n");
                }
                if (!RD933Mapper.safe(p.getDireccionComplementaria()).isBlank()) {
                    sb.append("          <direccionComplementaria>").append(RD933Mapper.safe(p.getDireccionComplementaria())).append("</direccionComplementaria>\n");
                }
                if (esEspanya && !RD933Mapper.safe(p.getCodigoMunicipio()).isBlank()) {
                    sb.append("          <codigoMunicipio>").append(RD933Mapper.safe(p.getCodigoMunicipio())).append("</codigoMunicipio>\n");
                }
                if (!esEspanya && !RD933Mapper.safe(p.getNombreMunicipio()).isBlank()) {
                    sb.append("          <nombreMunicipio>").append(RD933Mapper.safe(p.getNombreMunicipio())).append("</nombreMunicipio>\n");
                }
                if (!RD933Mapper.safe(p.getCodigoPostal()).isBlank()) {
                    sb.append("          <codigoPostal>").append(RD933Mapper.safe(p.getCodigoPostal())).append("</codigoPostal>\n");
                }
                sb.append("          <pais>").append(RD933Mapper.safe(p.getPais())).append("</pais>\n");
                sb.append("        </direccion>\n");

                if (!RD933Mapper.safe(p.getTelefono1()).isBlank()) {
                    sb.append("        <telefono>").append(RD933Mapper.safe(p.getTelefono1())).append("</telefono>\n");
                }
                if (!RD933Mapper.safe(p.getTelefono2()).isBlank()) {
                    sb.append("        <telefono2>").append(RD933Mapper.safe(p.getTelefono2())).append("</telefono2>\n");
                }
                if (!RD933Mapper.safe(p.getCorreo()).isBlank()) {
                    sb.append("        <correo>").append(RD933Mapper.safe(p.getCorreo())).append("</correo>\n");
                }
                if (!RD933Mapper.safe(p.getParentesco()).isBlank()) {
                    sb.append("        <parentesco>").append(RD933Mapper.safe(p.getParentesco())).append("</parentesco>\n");
                }
                sb.append("      </persona>\n");
            }

            sb.append("    </comunicacion>\n");
        }

        sb.append("  </solicitud>\n");
        sb.append("</ns2:peticion>\n");

        return sb.toString();
    }
}