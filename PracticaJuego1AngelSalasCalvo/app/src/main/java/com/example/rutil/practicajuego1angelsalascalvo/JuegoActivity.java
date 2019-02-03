package com.example.rutil.practicajuego1angelsalascalvo;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;

public class JuegoActivity extends AppCompatActivity {

    private LinearLayout lyTablero;
    private ImageButton ibJugar;
    private Chronometer cronometro;
    private long timeWhenStopped = 0; //variable para detener el cronometro
    private TextView tvPuntos ;
    Vibrator vibService; //Para el servicio de vibración
    MediaPlayer mpCorrecto, mpError, mpSerpiente, mpTiro, mpVictoria; //Para el sonido

    private BotonHoyo [][] hoyos;
    private BotonHoyo objetivo, serpiente;

    private boolean jugando, vibrar, sonar;
    private int fil=0, col=0; //Filas y columas para el panel de hoyos
    private int numTopos; //Valor para finalizar partida
    private int cont; //Valor para contar el numero de topos pulsados
    private int posibilidadSerp; //Cuanto mayor sea este valor menos posibilidad de que aparezca una serpiente
    private int nivel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        //1. Obtener los datos pasados con el intent
        cogerDatos();
        //2. Referenciar elementos e iniciar variables del juego
        inicializar();
        //3. Generar los hoyos (botones) del tablero
        generarHoyos();
        //4. Mostrar dialogo con información de inicio de juego
        mostrarDialogo();

        //5. Poner boton de jugar a la escucha para comenzar partida
        ibJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si no se esta ya en una partida iniciada
                if(!jugando) {
                    if(sonar) mpTiro.start();
                    jugando = true;
                    topoAleatorio(); //Generar primer topo
                    ibJugar.setImageResource(R.drawable.empezar2); //Establecer imagen boton

                    //Iniciar cronometro
                    cronometro.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    cronometro.start();
                }
            }
        });

    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA OBTENER LOS DATOS DE LA PARTIDA PASADOS CON EL INTENT
     */
    public void cogerDatos(){
        Bundle datos = getIntent().getExtras();
        //Obtener los datos del bundle pasado con el intent
        nivel=datos.getInt(MainActivity.NIVEL);
        numTopos=datos.getInt(MainActivity.NUM_TOPOS);
        vibrar=datos.getBoolean(MainActivity.VIBRAR);
        sonar=datos.getBoolean(MainActivity.SONAR);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA INICIALIZAR VISTA Y VARIABLES DE LA LOGICA DEL JUEGO
     */
    public void inicializar(){
        ibJugar=(ImageButton) findViewById(R.id.ibJugar);
        cronometro=(Chronometer) findViewById(R.id.cronometro);
        lyTablero=(LinearLayout) findViewById(R.id.lyTablero);
        tvPuntos= (TextView) findViewById(R.id.tvPuntos);
        //Pausar cronometro antes de empezar
        timeWhenStopped = cronometro.getBase() - SystemClock.elapsedRealtime();
        cronometro.stop();

        //Iniciar audios
        if(sonar) {
            mpCorrecto = MediaPlayer.create(this, R.raw.topo);
            mpError = MediaPlayer.create(this, R.raw.hoyo);
            mpSerpiente = MediaPlayer.create(this, R.raw.serpiente);
            mpTiro = MediaPlayer.create(this, R.raw.tiro);
            mpVictoria = MediaPlayer.create(this, R.raw.victoria);
        }
        //Iniciar servicio de vibrar
        if(vibrar)
            vibService=(Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        cont=0;
        serpiente=null;
        jugando=false;

        //En funcion de la dificultad establecemos valores de juego
        switch (nivel){
            case 1:
                fil=2;
                col=2;
                posibilidadSerp =7;
                break;
            case 2:
                fil=3;
                col=2;
                posibilidadSerp =6;
                break;
            case 3:
                fil=3;
                col=3;
                posibilidadSerp =3;
                break;
        }

        //Establecer puntos en activity
        tvPuntos.setText(String.format("%02d", cont)+"/"+ numTopos);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO ENCARGADO DE GENERAR LOS HOYOS (BOTONES) DEL ACTIVITY
     */
    public void generarHoyos(){
        //inicializar array botones
        hoyos = new BotonHoyo[fil][col];
        //Variable con altura y anchura como match_parent para ajustar botones y filas generadas al tablero
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f);

        for(int i=0 ; i<fil;i++){
            //crear un layout por fila
            LinearLayout lyFila = new LinearLayout(this);
            lyFila.setOrientation(LinearLayout.HORIZONTAL);
            //Establecer los parametros de tamaño relativos al padre para ajustarlos al maximo
            lyFila.setLayoutParams(lp);

            for (int j=0; j<col;j++){
                //Crear boton
                BotonHoyo buttonHoyo = new BotonHoyo(this);
                //Establecer los parametros de tamaño a los botones para ajustarlos al maximo
                buttonHoyo.setLayoutParams(lp);

                buttonHoyo.setBackgroundColor(Color.WHITE);
                buttonHoyo.setImageResource(R.drawable.hoyo);
                buttonHoyo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                buttonHoyo.setPadding(6,6,6,6);

                //Establecer metodo para cuando se pulse un boton
                buttonHoyo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pulsadoHoyo(v);
                    }
                });

                hoyos[i][j]=buttonHoyo; //incluir el boton en array
                lyFila.addView(buttonHoyo); //añadir el boton al layout del tablero de juego
            }

            //añadir la fila creada al layout
            lyTablero.addView(lyFila);
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA MOSTRAR EL DIALOGO CON INSTRUCCIONES DE INICIO
     */
    public void mostrarDialogo(){
        new AlertDialog.Builder(this)
                .setView(R.layout.dialogo_inicio)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .show();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE ACTUARÁ AL PULSAR UN BOTON/HOYO
     * En funcion del hoyo pulsado se decide la acción
     * @param v
     */
    public void pulsadoHoyo(View v){
        //Comprobar si la partida esta iniciada
        if(jugando) {
            //Vibración
            if(vibrar) vibService.vibrate(70);

            //Si se pulsa el boton correcto
            if (v == objetivo) {
                cont++;
                tvPuntos.setTextColor(Color.BLACK);
                if(sonar) mpCorrecto.start();
                //Reiniciar hoyos
                objetivo.reiniciar();
                if(serpiente!=null) {
                    serpiente.reiniciar();
                    serpiente=null;
                }

                //Al pulsar el boton comprobamos si se ha llegado al objetivo de pulsaciones
                if(cont== numTopos) {
                    if (sonar) mpVictoria.start();
                    finPartida(true); //Indicando que se ha ganado (true)
                }else {
                    //Generar nuevo topo en hoyo
                    topoAleatorio();
                    //Decidir aleatoriamente si se genera serpiente
                    if(generarSerpiente())
                        serpienteAleatoria();
                }
            }
            //Si se pulsa una serpiente
            else if (v == serpiente){
                if(sonar) mpSerpiente.start();
                finPartida(false); //Indicando que se ha perdido (false)
            }
            //Si se pulsa hoyo vacio
            else{
                if(sonar) mpError.start();
                if(cont-1>=0) {
                    tvPuntos.setTextColor(Color.RED);
                    cont--;
                }
            }

            //Actualizar texto en activity
            tvPuntos.setText(String.format("%02d", cont)+"/"+ numTopos);
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE DEVUELVE UN HOYO/BOTON DEL ARRAY DE FORMA ALEATORIA
     * @return
     */
    public void topoAleatorio(){
        Random r = new Random(System.currentTimeMillis());
        //Numeros Aleatorios para las filas y columnas desde 0 hasta fila/columnas
        int aleatF = r.nextInt(fil);
        int aleatC = r.nextInt(col);

        //Establecer imagen topo
        hoyos[aleatF][aleatC].setImageResource(R.drawable.topo);


        //Devolver el boton del array correspondiente
        objetivo = hoyos[aleatF][aleatC];
    }

    //----------------------------------------------------------------------------------------------

    /**
     * DECIDIR ALEATORIAMENTE SI SE GENERA UNA SERPIENTE A NO
     * @return
     */
    public boolean generarSerpiente(){
        //En función de una variable que depende del nivel
        // habrá mas o menos posibilidades de que aparezca una serpiente a la vez
        Random r = new Random(System.currentTimeMillis());
        int aleatorio = r.nextInt(posibilidadSerp);

        //Si el aleatorio es igual a 0 se generará serpiente
        if(aleatorio==0) {
            return true;
        }else {
            return false;
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE DEVUELVE UN HOYO/BOTON DEL ARRAY DE FORMA ALEATORIA PARA ESTABLECER SERPIENTE
     * DIFERENTE AL QUE CORRESPONDE CON EL TOPO
     * @return
     */
    public void serpienteAleatoria(){
        int aleatF = 0;
        int aleatC = 0;

        //Repetir si el boton generado es el que ya tiene un topo
        do {
            Random r = new Random(System.currentTimeMillis());
            //Numeros Aleatorios para las filas y columnas desde 0 hasta fila/columnas
            aleatF = r.nextInt(fil);
            aleatC = r.nextInt(col);
        }while(hoyos[aleatF][aleatC]==objetivo);

        //Establecer fondo
        hoyos[aleatF][aleatC].setImageResource(R.drawable.serpiente);
        //Devolver el boton del array correspondiente
        serpiente = hoyos[aleatF][aleatC];
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SE EJECUTAR AL FINALIZAR LA PERDIDA
     * @param ganado Dato booleano indicando si se ha perdido o ganado
     */
    public void finPartida(boolean ganado){
        //Detener juego
        jugando=false;
        cronometro.stop();
        //Obtener el tiempo en minutos y segundos
        long milisegundos = SystemClock.elapsedRealtime() - cronometro.getBase(); //Obtener tiempo en milisegundos
        int seconds = (int) (milisegundos / 1000) % 60 ;
        int minutes = (int) ((milisegundos / (1000*60)) % 60);
        String tiempo = String.format("%02d", minutes)+":"+String.format("%02d", seconds);

        //Enviar datos al intent principal
        Intent i = new Intent();
        i.putExtra(MainActivity.TIEMPO, tiempo); //Tiempo empleado
        i.putExtra(MainActivity.TOPOSATRAPADOS, cont);
        i.putExtra(MainActivity.GANADO, ganado);
        setResult(RESULT_OK,i);

        //finalizar el activity
        finish();
    }
}