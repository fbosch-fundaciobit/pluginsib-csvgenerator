package org.fundaciobit.plugins.csvgenerator.api;

import java.util.Locale;

import org.fundaciobit.plugins.IPlugin;

/**
 * 
 * 
 * @author anadal
 */
public interface ICSVGeneratorPlugin extends IPlugin {
  
  public static final String CSVGENERATOR_PROPERTY_BASE = IPLUGIN_BASE_PROPERTIES
      + "csvgenerator.";

  public String getName(Locale locale);

  public String generarCSV(String data, long currentTime, long randomNumber) throws Exception;

}
