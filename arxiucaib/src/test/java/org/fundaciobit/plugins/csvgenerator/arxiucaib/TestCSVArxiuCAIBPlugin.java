package org.fundaciobit.plugins.csvgenerator.arxiucaib;

import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import org.fundaciobit.plugins.csvgenerator.arxiucaib.ArxiuCAIBCSVGeneratorPlugin;

/**
 * 
 * @author anadal
 * 
 */
public class TestCSVArxiuCAIBPlugin {

  public static void main(String[] args) {

    try {

      Properties prop = new Properties();
      
      prop.load(new FileInputStream("plugin.properties"));
      
      
      final String base = "org.fundaciobit.exemple.";
      
      ArxiuCAIBCSVGeneratorPlugin jcsv = new ArxiuCAIBCSVGeneratorPlugin(base, prop);

      jcsv.getName(new Locale("ca"));

      long time = System.currentTimeMillis();
      System.out.println("CSV: " + jcsv.generarCSV("", time, new Random(time).nextLong()));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
