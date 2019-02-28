package com.example.rutil.clientechatangelsalas;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static java.util.Arrays.copyOf;
import android.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Angel Salas Calvo
 */
public class Cifrado {

    /**
     * METODO PARA COFICAR UN ARRAY DE BYTES EN BASE64
     * @param a
     * @return
     */
    public static String codBase64(byte [] a) {
        String b = Base64.encodeToString(a,Base64.DEFAULT);
        return b;
    }

    //--------------------------------------------------------------------------

    /**
     * METODO PARA DECODIFICAR UN STRING BASE64 A ARRAY DE BYTES
     * @param a
     * @return
     */
    public static byte [] decodBase64(String a){
        byte[] decodedByteArray = Base64.decode(a, Base64.DEFAULT);
        return decodedByteArray;
    }

    //--------------------------------------------------------------------------

    /**
     * METODO PARA CIFRAR UN MENSAJE Y DEVOLVERLO CODIFICADO EN BASE64
     * @param clave
     * @param texto
     * @return
     */
    public static String cifrar(String clave, String texto){
        byte[] fraseBytes, fraseEncriptada;
        byte[] claveBytes, auxiliar;
        SecretKey claveSimetrica;
        Cipher cifrador;

        //PROCESO DE CIFRADO
        try{
            //Convertir la clave en un array de bytes de longitud 32 bytes
            auxiliar = clave.getBytes("UTF-8");
            claveBytes = copyOf(auxiliar, 32);

            //Crear objeto de clave simetrica con el algoritmo indicado
            claveSimetrica = new SecretKeySpec(claveBytes, "AES");

            //Convertir la frase a codificar en un arry de byte
            fraseBytes = texto.getBytes("UTF-8");

            //Instanciar el objeto cifrador
            cifrador=Cipher.getInstance("AES/ECB/PKCS5padding");
            //Establecer el cifrador en modo de encriptacion asignando la clave simetrica
            cifrador.init(Cipher.ENCRYPT_MODE, claveSimetrica);
            //Encriptar la frase con el método adecuado y almacenarla
            fraseEncriptada = cifrador.doFinal(fraseBytes);

            //Devolver frase encriptada codificada en base64
            return codBase64(fraseEncriptada);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error de cifrado";
    }

    //--------------------------------------------------------------------------

    /**
     * METODO QUE A PARTIR DEL TEXTO EN BASE 64 RECIBIDO POR PARAMETRO,
     * LO DECODIFICA Y LO DEVUELVE DESENCRIPTADO
     * @param clave
     * @param texto
     * @return
     */
    public static String descifrar(String clave, String texto){
        byte[] fraseDesencriptada, fraseEncriptada;
        byte[] claveBytes, auxiliar;
        SecretKey claveSimetrica;
        Cipher cifrador;

        //PROCESO DE DESCIFRADO
        try{
            //Decodificar la frase recuperada en base64 para obtener array de bytes
            fraseEncriptada = decodBase64(texto);

            //Convertir la clave en un array de bytes de longitud 24 bytes
            auxiliar = clave.getBytes("UTF-8");
            claveBytes = copyOf(auxiliar, 32);

            //Crear objeto de clave simetrica con el algoritmo indicado
            claveSimetrica = new SecretKeySpec(claveBytes, "AES");

            //Instanciar el objeto cifrador
            cifrador=Cipher.getInstance("AES/ECB/PKCS5padding");
            //Establecer el cifrador en modo de desencriptacion asignando la clave simetrica
            cifrador.init(Cipher.DECRYPT_MODE, claveSimetrica);
            //Desencriptar la frase con el método adecuado y almacenarla
            fraseDesencriptada = cifrador.doFinal(fraseEncriptada);

            //Devolver el array de bytes con la frase desencriptada en formato string
            return new String(fraseDesencriptada);

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error al descifrar";
    }
}