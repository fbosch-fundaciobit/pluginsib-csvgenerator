package org.fundaciobit.plugins.csvgenerator.justicia;

import java.util.Locale;
import java.util.Random;

import org.fundaciobit.plugins.csvgenerator.justicia.JusticiaCSVGeneratorPlugin;

/**
 * 
 * @author anadal
 * 
 */
public class TestCSVJusticiaPlugin {

  public static void main(String[] args) {

    try {

      JusticiaCSVGeneratorPlugin jcsv = new JusticiaCSVGeneratorPlugin();

      jcsv.getName(new Locale("ca"));

      long time = System.currentTimeMillis();
      System.out.println("CSV: " + jcsv.generarCSV("", time, new Random(time).nextLong()));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
