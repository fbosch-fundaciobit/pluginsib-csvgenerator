package org.fundaciobit.plugins.csvgenerator.justicia;

import java.io.InputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.apache.log4j.Logger;
import org.fundaciobit.plugins.utils.Base64;

/**
 * Clase de utilidad para la generacion y verificacion de codigos hash.
 * 
 * @author Iecisa
 * @version $Revision: 1.1 $
 * 
 */
public class HashUtils {

    /**
     * Logger de la clase
     */
    private static final Logger logger = Logger.getLogger(HashUtils.class);

    /**
     * Algoritmo de encriptacion SHA-1
     */
    public static final String SHA1_ALGORITHM = "SHA1";

    /**
     * Algoritmo de encriptacion MD5
     */
    public static final String MD5_ALGORITHM = "MD5";

    /**
     * Constructor.
     */
    private HashUtils() {

    }

    /**
     * Genera el codigo hash de un binario.
     * 
     * @param content
     *            Binario.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @param provider
     *            Proveedor.
     * @return Codigo hash.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     */
    public static byte[] generateHash(byte[] content, String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {

        if (logger.isDebugEnabled()) {
            logger.debug("generateHash: algorithm=[" + algorithm + "], provider=[" + provider + "]");
        }

        MessageDigest md = null;

        if (isBlank(provider)) {
            md = MessageDigest.getInstance(algorithm);
        }
        else {
            md = MessageDigest.getInstance(algorithm, provider);
        }

        md.update(content);
        byte[] hash = md.digest();

        return hash;
    }

    /**
     * Genera el codigo hash de un binario.
     * 
     * @param content
     *            Binario.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @return Codigo hash.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     */
    public static byte[] generateHash(byte[] content, String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {

        return generateHash(content, algorithm, null);
    }

    /**
     * Genera el codigo hash de un binario utilizando el algoritmo SHA1 y el
     * proveedor BC.
     * 
     * @param content
     *            Binario.
     * @return Codigo hash.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     */
    public static byte[] generateHash(byte[] content) throws NoSuchAlgorithmException, NoSuchProviderException {

        return generateHash(content, SHA1_ALGORITHM, null);
    }

    /**
     * Genera el codigo hash de un binario.
     * 
     * @param content
     *            Binario.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @param provider
     *            Proveedor.
     * @return Codigo hash.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     * @throws IOException
     *             si ocurre algun error al leer el contenido.
     */
    public static byte[] generateHash(InputStream content, String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException,
                                                                                             IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("generateHash: algorithm=[" + algorithm + "], provider=[" + provider + "]");
        }

        MessageDigest md = null;

        if (isBlank(provider)) {
            md = MessageDigest.getInstance(algorithm);
        }
        else {
            md = MessageDigest.getInstance(algorithm, provider);
        }

        byte[] buffer = new byte[1024];
        int read;
        while ((read = content.read(buffer)) != -1) {
            md.update(buffer, 0, read);
            read = content.read(buffer);
        }

        byte[] hash = md.digest();

        return hash;
    }

    /**
     * Genera el codigo hash de un binario.
     * 
     * @param content
     *            Binario.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @return Codigo hash.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     * @throws IOException
     *             si ocurre algun error al leer el contenido.
     */
    public static byte[] generateHash(InputStream content, String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {

        return generateHash(content, algorithm, null);
    }

    /**
     * Genera el codigo hash de un binario utilizando el algoritmo SHA1 y el
     * proveedor BC.
     * 
     * @param content
     *            Binario.
     * @return Codigo hash.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     * @throws IOException
     *             si ocurre algun error al leer el contenido.
     */
    public static byte[] generateHash(InputStream content) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {

        return generateHash(content, SHA1_ALGORITHM, null);
    }

    /**
     * Genera el codigo hash en Base64 de un binario.
     * 
     * @param content
     *            Binario.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @param provider
     *            Proveedor.
     * @return Codigo hash en Base64.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     */
    public static String generateHashBase64(byte[] content, String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        return new String(Base64.encode(generateHash(content, algorithm, provider)));
    }

    /**
     * Genera el codigo hash en Base64 de un binario.
     * 
     * @param content
     *            Binario.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @return Codigo hash en Base64.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     */
    public static String generateHashBase64(byte[] content, String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {

        return generateHashBase64(content, algorithm, null);
    }

    /**
     * Genera el codigo hash en Base64 de un binario utilizando el algoritmo
     * SHA1 y el proveedor BC.
     * 
     * @param content
     *            Binario.
     * @return Codigo hash en Base64.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     */
    public static String generateHashBase64(byte[] content) throws NoSuchAlgorithmException, NoSuchProviderException {

        return generateHashBase64(content, SHA1_ALGORITHM, null);
    }

    /**
     * Genera el codigo hash en Base64 de un binario.
     * 
     * @param content
     *            Binario.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @param provider
     *            Proveedor.
     * @return Codigo hash en Base64.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     * @throws IOException
     *             si ocurre algun error al leer el contenido.
     */
    public static String generateHashBase64(InputStream content, String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException,
                                                                                                   IOException {

        
        return new String(Base64.encode(generateHash(content, algorithm, provider)));
    }

    /**
     * Genera el codigo hash en Base64 de un binario.
     * 
     * @param content
     *            Binario.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @return Codigo hash en Base64.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     * @throws IOException
     *             si ocurre algun error al leer el contenido.
     */
    public static String generateHashBase64(InputStream content, String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {

        return generateHashBase64(content, algorithm, null);
    }

    /**
     * Genera el codigo hash en Base64 de un binario utilizando el algoritmo
     * SHA1 y el proveedor BC.
     * 
     * @param content
     *            Binario.
     * @return Codigo hash en Base64.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     * @throws IOException
     *             si ocurre algun error al leer el contenido.
     */
    public static String generateHashBase64(InputStream content) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {

        return generateHashBase64(content, SHA1_ALGORITHM, null);
    }

    /**
     * Verifica el codigo hash de un binario.
     * 
     * @param content
     *            Binario.
     * @param hashBase64
     *            Codigo hash en Base64.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @param provider
     *            Proveedor.
     * @return true si el codigo hash es correcto, false en caso contrario.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     */
    public static boolean validateHash(byte[] content, String hashBase64, String algorithm, String provider) throws NoSuchAlgorithmException,
                                                                                                            NoSuchProviderException {

        return equalsString(generateHashBase64(content, algorithm, provider), hashBase64);
    }

    /**
     * Verifica el codigo hash de un binario.
     * 
     * @param content
     *            Binario.
     * @param hashBase64
     *            Codigo hash en Base64.
     * @param algorithm
     *            Algoritmo de encriptacion.
     * @return true si el codigo hash es correcto, false en caso contrario.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     */
    public static boolean validateHash(byte[] content, String hashBase64, String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {

        return equalsString(generateHashBase64(content, algorithm), hashBase64);
    }

    /**
     * Verifica el codigo hash de un binario.
     * 
     * @param content
     *            Binario.
     * @param hashBase64
     *            Codigo hash en Base64.
     * @return true si el codigo hash es correcto, false en caso contrario.
     * @throws NoSuchProviderException
     *             si el proveedor no esta soportado.
     * @throws NoSuchAlgorithmException
     *             si el algoritmo no esta soportado.
     */
    public static boolean validateHash(byte[] content, String hashBase64) throws NoSuchAlgorithmException, NoSuchProviderException {

        return equalsString(generateHashBase64(content), hashBase64);
    }
    
    
    public static boolean  isBlank(String str ) { 
      return (str == null || str.trim().length() == 0);
    }

    public static boolean equalsString(String str1, String str2) {
      if (str1 == null) {
          return str2 == null;
      } else {
        return str1.equals(str2);
      }
    }
    
}
