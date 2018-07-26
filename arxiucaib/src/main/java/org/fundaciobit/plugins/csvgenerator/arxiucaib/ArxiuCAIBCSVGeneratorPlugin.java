package org.fundaciobit.plugins.csvgenerator.arxiucaib;

import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.fundaciobit.plugins.csvgenerator.api.ICSVGeneratorPlugin;
import org.fundaciobit.plugins.utils.AbstractPluginProperties;

import es.caib.arxiudigital.apirest.ApiArchivoDigital;
import es.caib.arxiudigital.apirest.facade.pojos.CabeceraPeticion;
import es.caib.arxiudigital.apirest.facade.resultados.Resultado;

/**
 * 
 * 
 * @author anadal
 */
public class ArxiuCAIBCSVGeneratorPlugin extends AbstractPluginProperties implements
    ICSVGeneratorPlugin {

  protected final Logger log = Logger.getLogger(getClass());

  public static final String ARXIUCAIB_PROPERTY_BASE = CSVGENERATOR_PROPERTY_BASE + "arxiucaib.";

  /**
   * 
   */
  public ArxiuCAIBCSVGeneratorPlugin() {
    super();
  }

  /**
   * @param propertyKeyBase
   * @param properties
   */
  public ArxiuCAIBCSVGeneratorPlugin(String propertyKeyBase, Properties properties) {
    super(propertyKeyBase, properties);
  }

  /**
   * @param propertyKeyBase
   */
  public ArxiuCAIBCSVGeneratorPlugin(String propertyKeyBase) {
    super(propertyKeyBase);
  }

  @Override
  public String getName(Locale locale) {
    if ("es".equals(locale.getLanguage())) {
      return "Generador de CSV del Archivo Digital CAIB";
    }
    return "Generador de CSV de l´Arxiu Digital CAIB";
  }

  @Override
  public String generarCSV(String data, long currentTime, long randomNumber) throws Exception {

    String ignoreServerCertificates = getProperty(ARXIUCAIB_PROPERTY_BASE + "connection.ignoreservercertificates");
    
    if ("true".equals(ignoreServerCertificates)) {
      org.fundaciobit.plugins.utils.XTrustProvider.install();
    }
    
    CabeceraPeticion cabecera = new CabeceraPeticion();
    // intern api
    cabecera.setServiceVersion(ApiArchivoDigital.VERSION_SERVICIO);
    // aplicacio
    cabecera.setCodiAplicacion(getPropertyRequired(ARXIUCAIB_PROPERTY_BASE + "aplicacio_client")); // TEST
    cabecera.setUsuarioSeguridad(getPropertyRequired(ARXIUCAIB_PROPERTY_BASE + "connection.username")); // app1
    cabecera.setPasswordSeguridad(getPropertyRequired(ARXIUCAIB_PROPERTY_BASE + "connection.password")); // app1
    cabecera.setOrganizacion(getPropertyRequired(ARXIUCAIB_PROPERTY_BASE + "organitzacio")); // CAIB
    // info login
    cabecera.setNombreSolicitante(getPropertyRequired(ARXIUCAIB_PROPERTY_BASE + "nom_solicitant")); // Víctor
                                                                          // Herrera
    cabecera.setDocumentoSolicitante(getPropertyRequired(ARXIUCAIB_PROPERTY_BASE + "document_solicitant")); // 123456789C
    cabecera.setNombreUsuario(getPropertyRequired(ARXIUCAIB_PROPERTY_BASE + "nom_usuari")); // u104848
    // info peticio
    cabecera.setNombreProcedimiento(getPropertyRequired(ARXIUCAIB_PROPERTY_BASE + "nom_procedimient")); // subvenciones
                                                                              // empleo

    String urlBase = getPropertyRequired(ARXIUCAIB_PROPERTY_BASE + "connection.endPoint"); // https://afirmades.caib.es:4430/esb

    ApiArchivoDigital apiArxiu = new ApiArchivoDigital(urlBase, cabecera);

    apiArxiu.setTrazas(false);

      Resultado<String> csv = apiArxiu.generarCSV();

    if (log.isDebugEnabled()) {
      log.debug(" CSV: " + csv.getElementoDevuelto());
    }

    return csv.getElementoDevuelto();

  }

}
