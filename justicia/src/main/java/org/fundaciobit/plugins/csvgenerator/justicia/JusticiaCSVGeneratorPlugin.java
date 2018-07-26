package org.fundaciobit.plugins.csvgenerator.justicia;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.fundaciobit.plugins.csvgenerator.api.ICSVGeneratorPlugin;
import org.fundaciobit.plugins.utils.AbstractPluginProperties;
import org.fundaciobit.plugins.utils.Base64;

/**
 * 
 * 
 * @author anadal
 */
public class JusticiaCSVGeneratorPlugin extends AbstractPluginProperties implements
    ICSVGeneratorPlugin {

  protected final Logger logger = Logger.getLogger(getClass());

  protected static String macAddress = null;

  /**
   * An empty immutable <code>byte</code> array.
   */
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

  /**
   * 
   */
  public JusticiaCSVGeneratorPlugin() {
    super();
  }

  /**
   * @param propertyKeyBase
   * @param properties
   */
  public JusticiaCSVGeneratorPlugin(String propertyKeyBase, Properties properties) {
    super(propertyKeyBase, properties);
  }

  /**
   * @param propertyKeyBase
   */
  public JusticiaCSVGeneratorPlugin(String propertyKeyBase) {
    super(propertyKeyBase);
  }

  @Override
  public String getName(Locale locale) {
    return "Justicia (Orden JUS de 10 de enero de 2011)";
  }

  @Override
  public String generarCSV(String data, long currentTime, long randomNumber) throws Exception {

    final boolean debug = logger.isDebugEnabled();

    if (debug) {
      logger.debug("Llamada a generarCSV: " + data);
    }

    if (data == null) {
      data = "";
    }

    /*
     * Paso 1: Se generara una cadena de caracteres uniendo la direccion MAC del
     * servidor, el tiempo actual en milisegundos, un numero aleatorio y la
     * peticion recibida como cadena de caracteres.
     */
    String cadena = new StringBuffer().append(getMACAddress()).append(currentTime)
        .append(randomNumber).append(data).toString();
    if (debug) {
      logger.debug("Paso 1 - cadena de caracteres: " + cadena);
    }

    /*
     * Paso 2: Sobre esta cadena de caracteres resultante, se aplica un
     * algoritmo SHA1 para generar un hash, el cual sera truncado a 15 bytes.
     */
    byte[] hash = generarHash(cadena.getBytes());
    hash = subarray(hash, 0, 15);
    if (debug) {
      logger.debug("Paso 2 - codigo hash truncado a 15 bytes: " + hash);
    }

    /*
     * Paso 3: Una vez obtenido este codigo, se codificara en base64 con el fin
     * de obtener 20 caracteres alfanumericos.
     */
    String hashBase64 = Base64.encode(hash);
    if (debug) {
      logger.debug("Paso 3 - codigo hash truncado a 15 bytes en base64: " + hashBase64);
    }

    /*
     * G.S. 20150202 Paso 4: Remplazar los '+' por 'd'
     */
    hashBase64 = hashBase64.replaceAll(Pattern.quote("+"), "d");
    if (debug) {
      logger.debug("Paso 4 - Remplazar los '+' por 'd': " + hashBase64);
    }

    /*
     * G.S. 20150610 Paso 5: Remplazar los '\r y \n' por ''
     */
    hashBase64 = hashBase64.replaceAll(Pattern.quote("\r\n"), "");
    if (debug) {
      logger.debug("Paso 5 - Remplazar '\r\n' por '': " + hashBase64);
      logger.debug("CSV: " + hashBase64);
    }

    return hashBase64;
  }

  protected String getMACAddress() {
    if (macAddress == null) {
      synchronized (MAC_ADDRESS_SYNC) {
        InetAddress ip;
        try {

          ip = InetAddress.getLocalHost();
          if (logger.isDebugEnabled()) {
            logger.debug("Current IP address : " + ip.getHostAddress());
          }

          NetworkInterface network = NetworkInterface.getByInetAddress(ip);

          byte[] mac = network.getHardwareAddress();

          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X", mac[i]));
          }

          if (logger.isDebugEnabled()) {
            logger.debug("Current MAC address : " + sb.toString());
          }

          macAddress = sb.toString();

        } catch (Exception e) {
          getMACAddressNative();
        }
      }
    }

    return macAddress == null ? "" : macAddress;

  }

  public static final String MAC_ADDRESS_SYNC = "MAC_ADDRESS_SYNC";

  protected String getMACAddressNative() {

    if (macAddress == null) {

      {

        Process p = null;
        BufferedReader in = null;

        try {
          String osname = System.getProperty("os.name", "");

          if (osname.startsWith("Windows")) {
            p = Runtime.getRuntime().exec(new String[] { "ipconfig", "/all" }, null);
          }
          // Solaris code must appear before the generic code
          else if (osname.startsWith("Solaris") || osname.startsWith("SunOS")) {
            String hostName = getFirstLineOfCommand("uname", "-n");
            if (hostName != null) {
              p = Runtime.getRuntime().exec(new String[] { "/usr/sbin/arp", hostName }, null);
            }
          } else if (new File("/usr/sbin/lanscan").exists()) {
            p = Runtime.getRuntime().exec(new String[] { "/usr/sbin/lanscan" }, null);
          } else if (new File("/sbin/ifconfig").exists()) {
            p = Runtime.getRuntime().exec(new String[] { "/sbin/ifconfig", "-a" }, null);
          }

          if (p != null) {
            in = new BufferedReader(new InputStreamReader(p.getInputStream()), 128);
            String l = null;
            while ((l = in.readLine()) != null) {
              macAddress = parse(l);
              if (macAddress != null /* && Hex.parseShort(macAddress)!=0xff */) {
                break;
              }
            }
          }
        } catch (SecurityException ex) {
          // Ignore it.
        } catch (IOException ex) {
          // Ignore it.
        } finally {
          if (p != null) {
            if (in != null) {
              try {
                in.close();
              } catch (IOException ex) {
                // Ignore it.
              }
            }
            try {
              p.getErrorStream().close();
            } catch (IOException ex) {
              // Ignore it.
            }
            try {
              p.getOutputStream().close();
            } catch (IOException ex) {
              // Ignore it.
            }
            p.destroy();
          }
        }
      }

    }

    logger.debug("Direccion MAC: " + macAddress);

    return macAddress;
  }

  /**
   * Returns the first line of the shell command.
   * 
   * @param commands
   *          the commands to run
   * @return the first line of the command
   * @throws IOException
   */
  static String getFirstLineOfCommand(String... commands) throws IOException {

    Process p = null;
    BufferedReader reader = null;

    try {
      p = Runtime.getRuntime().exec(commands);
      reader = new BufferedReader(new InputStreamReader(p.getInputStream()), 128);

      return reader.readLine();
    } finally {
      if (p != null) {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException ex) {
            // Ignore it.
          }
        }
        try {
          p.getErrorStream().close();
        } catch (IOException ex) {
          // Ignore it.
        }
        try {
          p.getOutputStream().close();
        } catch (IOException ex) {
          // Ignore it.
        }
        p.destroy();
      }
    }
  }

  protected byte[] generarHash(byte[] contenido) throws Exception {

    byte[] hash = null;

    if (contenido != null) {
      try {
        hash = HashUtils.generateHash(contenido, HashUtils.SHA1_ALGORITHM);
      } catch (Throwable t) {
        logger.error("Error al generar el hash de la cadena de caracteres", t);
        throw new Exception("error.csv.connector.generacionCSV.generarHash");
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Hash generado: " + Base64.encode(hash));
    }

    return hash;
  }

  /**
   * Attempts to find a pattern in the given String.
   * 
   * @param in
   *          the String, may not be <code>null</code>
   * @return the substring that matches this pattern or <code>null</code>
   */
  static String parse(String in) {

    String out = in;

    // lanscan

    int hexStart = out.indexOf("0x");
    if (hexStart != -1 && out.indexOf("ETHER") != -1) {
      int hexEnd = out.indexOf(' ', hexStart);
      if (hexEnd > hexStart + 2) {
        out = out.substring(hexStart, hexEnd);
      }
    } else {
      int octets = 0;
      int lastIndex, old, end;

      if (out.indexOf('-') > -1) {
        out = out.replace('-', ':');
      }

      lastIndex = out.lastIndexOf(':');

      if (lastIndex > out.length() - 2) {
        out = null;
      } else {
        end = Math.min(out.length(), lastIndex + 3);

        ++octets;
        old = lastIndex;
        while (octets != 5 && lastIndex != -1 && lastIndex > 1) {
          lastIndex = out.lastIndexOf(':', --lastIndex);
          if (old - lastIndex == 3 || old - lastIndex == 2) {
            ++octets;
            old = lastIndex;
          }
        }

        if (octets == 5 && lastIndex > 1) {
          out = out.substring(lastIndex - 2, end).trim();
        } else {
          out = null;
        }
      }
    }

    if (out != null && out.startsWith("0x")) {
      out = out.substring(2);
    }

    return out;
  }

  /**
   * <p>
   * Produces a new <code>byte</code> array containing the elements between the
   * start and end indices.
   * </p>
   *
   * <p>
   * The start index is inclusive, the end index exclusive. Null array input
   * produces null output.
   * </p>
   *
   * @param array
   *          the array
   * @param startIndexInclusive
   *          the starting index. Undervalue (&lt;0) is promoted to 0, overvalue
   *          (&gt;array.length) results in an empty array.
   * @param endIndexExclusive
   *          elements up to endIndex-1 are present in the returned subarray.
   *          Undervalue (&lt; startIndex) produces empty array, overvalue
   *          (&gt;array.length) is demoted to array length.
   * @return a new array containing the elements between the start and end
   *         indices.
   * @since 2.1
   */
  public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
    if (array == null) {
      return null;
    }
    if (startIndexInclusive < 0) {
      startIndexInclusive = 0;
    }
    if (endIndexExclusive > array.length) {
      endIndexExclusive = array.length;
    }
    int newSize = endIndexExclusive - startIndexInclusive;
    if (newSize <= 0) {
      return EMPTY_BYTE_ARRAY;
    }

    byte[] subarray = new byte[newSize];
    System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
    return subarray;
  }

}
