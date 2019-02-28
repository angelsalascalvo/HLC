package com.example.rutil.clientechatangelsalas;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements DialogoConexion.onDialogoConectar{

    Socket conex;
    String clave;
    PrintWriter salida = null;
    BufferedReader entrada = null;
    EditText etMensaje, etConversacion;
    Escucha escucha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referencias
        etMensaje = (EditText) findViewById(R.id.etMensaje);
        etConversacion = (EditText) findViewById(R.id.etConversacion);

        //Iniciar componentes y variables
        clave="cifradoseguro";
        etMensaje.requestFocus();
        etConversacion.setFocusable(false);
        etConversacion.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        etConversacion.setClickable(false);

        //PERMITIR LA CONEXIÓN DESDE EL MAIN
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Crear dialogo conexion
        DialogoConexion diaCon = DialogoConexion.newInstance();
        diaCon.show(getSupportFragmentManager(), "DialogoConexion");
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCRITURA DEL METODO onBackPressed
     * DECIDIR QUE HACER AL PULSAR EN VOLVER
     */
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        cerrarConexion();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * ESTABLECER EL MENU
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_acciones, menu);
        return true;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE AÑADE FUNCIÓN A LOS BOTONES DEL MENU
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId(); //Obtener el id de la opcion pulsada
        switch (id){
            case R.id.miDesconectar:
                cerrarConexion();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCRITURA DEL METODO onConectar
     * OBTENEMOS LA CONEXIÓN CON EL SERVIDOR
     * @param conexion
     */
    @Override
    public void onConectar(Socket conexion) {

        try {
            conex = conexion;
            //Crear flujos de entrada y salida para la conexión
            salida = new PrintWriter(conex.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(conex.getInputStream()));

            escucha = new Escucha();
            escucha.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SE EJECUTARÁ AL PULSAR EL BOTON ENVIAR
     * @param view
     */
    public void botonEnviar(View view){
        //Comprobar si se ha introducido texto en el campo
        if(etMensaje.getText().length()!=0) {
            //Obtener el texto del campo de texto
            String texto = etMensaje.getText().toString();
            enviarMensaje(texto);
            //Añadimos el texto al cuadro de texto
            etConversacion.append("\uD83D\uDCAC " + texto + "\n");
            //Autoscroll en el texto multilinea
            etConversacion.setSelection(etConversacion.length());
            //Limpiar campo texto
            etMensaje.setText("");
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA ENVIAR UN MENSAJE AL SERVIDOR CIFRADO Y CODIFICADO EN BASE 64
     * @param txt
     */
    public void enviarMensaje(String txt){
        //Cifrar el mensaje
        String cifrado = Cifrado.cifrar(clave, txt);
        //Enviar el mensaje cifrado al servidor
        salida.print(cifrado);
        salida.flush();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA CERRAR LA APLICACION
     */
    public void cerrarConexion(){
        enviarMensaje("/x");
        etMensaje.setEnabled(false);
    }

    //==============================================================================================

    /**
     * TAREA ASINCRONA PARA LA ESCUCHA DE MENSAJES
     */
    private class Escucha extends AsyncTask<Void, String, Void> {

        /**
         * METODO QUE SE EJECUTARÁ EN SEGUNDO PLANO
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {
            String cifrado;
            String descifrado="";
            do{
                try {
                    Log.d("","");
                    cifrado = entrada.readLine().trim();
                    descifrado=Cifrado.descifrar(clave, cifrado);

                    //Si el mensaje recibido del servidor es exit finalizamos este hilo
                    if(descifrado.equalsIgnoreCase("/x")){
                        break;
                    }else{
                        escucha.publishProgress(descifrado);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(isCancelled())
                    break;
            }while (descifrado.equalsIgnoreCase("/x")==false);

            return null;
        }

        /**
         * METODO PARA PUBLICAR CAMBIOS, ESCRIBIR MENSAJES
         * @param values
         */
        @Override
        protected void onProgressUpdate(String ... values){
            //Añadimos el texto descifrado al cuadro de texto
            etConversacion.append(values[0]+"\n");
            //Autoscroll en el texto multilinea
            etConversacion.setSelection(etConversacion.length());
        }
    }
}