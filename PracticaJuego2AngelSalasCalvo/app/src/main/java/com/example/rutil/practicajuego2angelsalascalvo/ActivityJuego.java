package com.example.rutil.practicajuego2angelsalascalvo;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class ActivityJuego extends AppCompatActivity {

    SensorManager sensorManager;
    SensorEventListener escucharAcelerometro;
    Sensor acelerometro;
    double inclinacion;
    int cuenta, puntuacion, personajeActual;
    boolean vibrar, sonar;

    CuentaAtras cuentaAtras;
    BarraProgreso barraProgreso;

    TextView tvCuentaAtras, tvPuntuacion;
    ImageButton ibAnterior, ibSiguiente;
    ImageView ivPersonaje, ivSilueta;
    ConstraintLayout lyJuego, lyCuenta;
    ProgressBar proBar;
    MediaPlayer mpCorrecto; //Para el sonido
    Vibrator vibService; //Para el servicio de vibración


    //Arrays con los elementos graficos
    int[] fondo = new int []{
            R.drawable.f1,
            R.drawable.f2,
            R.drawable.f3,
            R.drawable.f4,
            R.drawable.f5,
    };
    int[] silueta = new int []{
            R.drawable.s1,
            R.drawable.s2,
            R.drawable.s3,
            R.drawable.s4,
            R.drawable.s5,
    };
    int[] personaje = new int []{
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p5,
    };

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        //1.Iniciar vista
        iniciar();
        //2.Establecer listener del acelerometro
        establecerListener();
        //3.Cuenta Atras de 3 segundos
        cuentaAtras = new CuentaAtras();
        cuentaAtras.execute(3);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCRITURA DEL METODO ONBACKPRESSED PARA EVITAR QUE USUARIO CIERRE EL INTENT
     */
    @Override
    public void onBackPressed() {
        //doing nothing on pressing Back key
        return;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA INICIAR TODOS LOS COMPONENTES Y COMPROBAR SI SE DISPONE DE ACELEROMETRO
     */
    public void iniciar(){
        //Referencias
        tvCuentaAtras=(TextView) findViewById(R.id.tvCuentaAtras);
        tvPuntuacion=(TextView) findViewById(R.id.tvPuntuacion);
        ibAnterior=(ImageButton) findViewById(R.id.ibAnterior);
        ibSiguiente=(ImageButton) findViewById(R.id.ibSiguiente);
        ivPersonaje=(ImageView) findViewById(R.id.ivPersonaje);
        ivSilueta=(ImageView) findViewById(R.id.ivSilueta);
        lyCuenta=(ConstraintLayout) findViewById(R.id.lyCuenta);
        lyJuego=(ConstraintLayout) findViewById(R.id.lyJuego);
        proBar=(ProgressBar) findViewById(R.id.progressBar);

        //Obtener datos del intent
        Intent i = getIntent();
        vibrar = i.getBooleanExtra(MainActivity.VIBRAR, false);
        sonar = i.getBooleanExtra(MainActivity.SONAR, false);

        //Establecer sonido y vibración si se han seleccionado
        if(sonar)
            mpCorrecto = MediaPlayer.create(this, R.raw.gota);
        if(vibrar)
            vibService=(Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        //Establecer valores variables
        puntuacion=0;

        //Objeto administrador de sensores
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Objeto del sensor especifico que vamos a utilizar
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Comprobar si el dispositivo dispone de acelerometro
        if (acelerometro == null) {
            String mensaje = "Su dispositivo no dispone de giroscopio";
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            //Cerramos el activity
            finish();
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA PONER SENSOR A LA ESCUCHA CON LA FUNCIONALIDAD ADECUADA
     */
    public void establecerListener(){
        //Instanciar objeto de escucha para el sensor con una calse anonima
        escucharAcelerometro = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //Obtener la rotacion del dispositivo con el acelerometro
                float aX = event.values[0];
                float aY = event.values[1];
                //float aZ = event.values[2];
                inclinacion = Math.atan2(aX, aY)/(Math.PI/180);

                //Rotar el personaje los grados correspondientes
                ivPersonaje.setRotation((float)inclinacion);
                //Comprobar si el personaje y la silueta estan en la misma posicion
                comprobar();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE INICIALIZA LA PARTIDA HACIENDO VISIBLES LOS ELENTO E INICIANDO LA BARRA DE PROGRESO
     */
    public void empezarPartida(){
        proBar.setProgress(300);
        //Ocultar panel de cuenta atras y mostrar el del juego
        lyCuenta.setVisibility(View.GONE);
        lyJuego.setVisibility(View.VISIBLE);

        //Poner el sensor a la escucha
        sensorManager.registerListener(escucharAcelerometro, acelerometro, SensorManager.SENSOR_DELAY_GAME);

        //Establecer personaje inicial
        personajeActual=0;
        ivPersonaje.setImageResource(personaje[0]);
        ivPersonaje.setTag(""+personajeActual);

        //generar la primera silueta
        generarSilueta();

        //Empezar progressBar
        barraProgreso = new BarraProgreso();
        barraProgreso.execute(300);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA COMPROBAR SI LA POSICIÓN DEL PERSONAJE ES IGUAL QUE LA SILUETA Y SI CORRESPONDE
     */
    public void comprobar(){
        int actual = (int)ivPersonaje.getRotation();
        int objetivo = (int)ivSilueta.getRotation();

        //Comprobar si la silueta y el personaje es el mismo
        if(ivPersonaje.getTag().equals(ivSilueta.getTag())) {
            //Comprobar si la rotacion de la silueta y el personaje es igual (con un margen de 2 grados)
            if (actual >= (objetivo - 1) && actual <= (objetivo + 1)) {
                //Vibración
                if(vibrar) vibService.vibrate(120);
                //Sonido
                if(sonar) mpCorrecto.start();

                //Actualizar marcador
                puntuacion++;
                tvPuntuacion.setText(puntuacion + "");
                generarSilueta();
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE GENERA UNA SILUETA ALEATORIA Y LA SITUA EN UNA POSICION ALEATORIA
     * @return
     */
    public void generarSilueta(){
        //Establecer una silueta aleatoria del array diferente del personaje actual
        int generado= -1;
        Random random = null;
        //do {
            random = new Random();
            generado = (random.nextInt(silueta.length));
        //}while(generado==personajeActual);

        //Establecer silueta e imagen de fondo correspondiente
        ivSilueta.setImageResource(silueta[generado]);
        ivSilueta.setTag(""+generado); //Almacenar el numero del elemento como etiqueta
        lyJuego.setBackgroundResource(fondo[generado]);

        //Rotar la silueta aleatoriamente entre (-179º y 179)
        int grados = (random.nextInt(358)-179);
        ivSilueta.setRotation((float)grados);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA EL BOTON SIGUIENTE QUE PERMITE OBTENER OTRO PERSONAJE
     * @param view
     */
    public void siguiente(View view){
        //Controlar que no se salga del array
        if(personajeActual==personaje.length-1)
            personajeActual=0;
        else
            personajeActual++;

        //Establecer la imagen del personaje
        ivPersonaje.setImageResource(personaje[personajeActual]);
        //Asignar una etiqueta al elemento
        ivPersonaje.setTag(""+personajeActual);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA EL BOTON ANTERIOR QUE PERMITE OBTENER OTRO PERSONAJE
     * @param view
     */
    public void anterior(View view){
        //Controlar que no se salga del array
        if(personajeActual==0)
            personajeActual=personaje.length-1;
        else
            personajeActual--;

        //Establecer la imagen del personaje
        ivPersonaje.setImageResource(personaje[personajeActual]);
        //Asignar una etiqueta al elemento
        ivPersonaje.setTag(""+personajeActual);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SE EJECUTA AL FINALIZAR EL TIEMPO DE PARTIDA
     */
    public void finPartida(){
        //Enviar datos al intent principal
        Intent i = new Intent();
        i.putExtra(MainActivity.PUNTOS, puntuacion);
        setResult(RESULT_OK,i);

        //finalizar el activity
        finish();
    }

//==================================================================================================

    /**
     * TAREA ASINCRONA PARA LA CUENTA ATRÁS INICIAL
     */
    //como parametros del asyncTask podemos pasar void si no queremos mandar nada
    private class CuentaAtras extends AsyncTask<Integer, Integer, Integer> {

        /**
         * METODO QUE SE EJECUTARÁ EN SEGUNDO PLANO
         * @param integers
         * @return
         */
        @Override
        protected Integer doInBackground(Integer... integers) {
            //Realizamos la cuenta atras el numero de veces indicadas
            for(; integers[0]>0 ; integers[0]--){
                cuentaAtras.publishProgress(integers[0]);
                //Esperar un segundo
                SystemClock.sleep(1000);
                if(isCancelled())
                    break;
            }
            return cuenta;
        }

        /**
         * METODO PARA PUBLICAR CAMBIOS DE PROCESO
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            //Indicar numero de la cuenta atras
            tvCuentaAtras.setText(values[0]+"");
        }

        /**
         * METODO QUE SE EJECUTA AL FINALIZAR EL PROCESO
         * @param integer
         */
        @Override
        protected  void onPostExecute(Integer integer){
            //Empezar la partida despues de la cuenta atras
            empezarPartida();
        }

        @Override
        protected void onCancelled(Integer integer){

        }
    }

//==============================================================================================

    /**
     * TAREA ASINCRONA PARA ACTUALIZAR LA BARRA DE PROGRESO
     */
    //como parametros del asyncTask podemos pasar void si no queremos mandar nada
    private class BarraProgreso extends AsyncTask<Integer, Integer, Integer> {

        /**
         * METODO QUE SE EJECUTARÁ EN SEGUNDO PLANO
         * @param integers
         * @return
         */
        @Override
        protected Integer doInBackground(Integer... integers) {
            int contador = integers[0];
            //Repetir la acción hasta llegar a 0
            while(contador>0){
                SystemClock.sleep(100);
                barraProgreso.publishProgress(contador);
                contador--;
                if(isCancelled())
                    break;
            }
            return contador;
        }

        /**
         * METODO PARA PUBLICAR CAMBIOS DE PROCESO
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            //Indicar valor de la barra de tareas (En sentido inverso, decrementando)
            proBar.setProgress(values[0]);
        }

        /**
         * METODO QUE SE EJECUTA AL FINALIZAR EL PROCESO
         * @param integer
         */
        @Override
        protected  void onPostExecute(Integer integer){
            finPartida();
        }

        @Override
        protected void onCancelled(Integer integer){

        }
    }
}