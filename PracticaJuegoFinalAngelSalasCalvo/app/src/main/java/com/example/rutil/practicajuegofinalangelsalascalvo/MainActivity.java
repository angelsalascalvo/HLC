package com.example.rutil.practicajuegofinalangelsalascalvo;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends BaseActivity implements DialogoAjustes.onDialogoAjustes{

    //Variables que pasaremos con el intent
    public final static String NOMJ1="nomJ1";
    public final static String NOMJ2="nomJ2";
    public final static String NUMRONDAS="numRondas";
    public final static String VIBRAR="vibrar";
    public final static String SONAR="sonar";

    private ImageView ivLogo;
    private EditText etNomJ1, etNomJ2;
    private int numRondas;
    private boolean vibrar, sonar;
    private MediaPlayer mpFondo, mpPulsar;
    private Vibrator vibService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Poner en pantalla completa
        setModoInmersivo();
        //Referencias
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        etNomJ1 = (EditText) findViewById(R.id.etNombreJ1);
        etNomJ2 = (EditText) findViewById(R.id.etNombreJ2);

        //Empezar animacion del logotipo
        animacionLogo();

        //Iniciar variables
        numRondas=3;
        vibrar=true;
        sonar=true;
        //Audio y sonido
        sonidoVibrar();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE ACTUARÁ CUANDO LA APLICACIÓN SE REANUDA
     */
    @Override
    public void onResume() {
        super.onResume();
        setModoInmersivo();
        //Continuar con la musica
        if(mpFondo!=null && sonar)
            mpFondo.start();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onPause(){
        //Detener la musica si esta sonando
        if(mpFondo.isPlaying())
            mpFondo.pause();
        super.onPause();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA INICIALIZAR VARIABLES DE SONIDO Y VIBRACIÓN SI SE ACTIVA
     */
    public void sonidoVibrar(){
        //Iniciar audios
        if(sonar) {
            mpFondo = MediaPlayer.create(this, R.raw.fondo);
            if(!mpFondo.isPlaying()) {
                mpFondo.start();
                mpFondo.setLooping(true);
            }
            mpPulsar = MediaPlayer.create(this, R.raw.clic);
        }else{
            if(mpFondo!=null)
                mpFondo.pause();
        }
        if(vibrar)
            vibService=(Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * METODO PARA ABRIR EL ACTIVITY DE JUEGO Y EMPEZAR LA PARTIDA
     * @param view
     */
    public void jugar(View view){
        Intent i = new Intent(this, ActivityJuego.class);
        //Crear bundle para pasar muchos datos
        Bundle datos = new Bundle();

        //Almacenar datos en el bundle
        if(etNomJ1.getText().length()==0)
            datos.putString(NOMJ1,"Piloto1");
        else
            datos.putString(NOMJ1,etNomJ1.getText().toString());

        if(etNomJ2.getText().length()==0)
            datos.putString(NOMJ2,"Piloto2");
        else
            datos.putString(NOMJ2,etNomJ2.getText().toString());

        datos.putBoolean(VIBRAR, vibrar);
        datos.putBoolean(SONAR, sonar);
        datos.putInt(NUMRONDAS, numRondas);

        //Enviar bundle con el intent
        i.putExtras(datos);
        //Sonar
        if(sonar) mpPulsar.start();
        //Vibrar
        if(vibrar)vibService.vibrate(100);
        //Abrir la activity
        startActivity(i);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA ABRIR LA VENTANA DE CONFIGURACION
     * @param view
     */
    public void abrirConfiguracion(View view) {
        //Creamos el dialogo
        DialogoAjustes ventEmergente = DialogoAjustes.newInstance(numRondas, sonar, vibrar);
        //Mostrar el dialogo que pide configuracion de la partida
        ventEmergente.show(getSupportFragmentManager(), "DialogoAjustes"); //Tag??
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCRITURA DEL METODO QUE SE EJECUTA AL PULSAR BOTON DEL DIALOGO DE CONFIGURACION
     * @param numRondas
     * @param sonar
     * @param vibrar
     */
    @Override
    public void onAceptarDialogo(int numRondas, boolean sonar, boolean vibrar) {
        setModoInmersivo();
        //Establecer configuración las variables
        this.numRondas=numRondas;
        this.sonar=sonar;
        this.vibrar=vibrar;
        sonidoVibrar();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA MOSTRAR EL DIALOGO DE INFORMACIÓN
     * @param view
     */
    public void mostrarInfo(View view){
        //Mostrar dialogo de informacion
        AlertDialog dialogoInicio = new AlertDialog.Builder(this)
            .setView(R.layout.dialogo_info) //Layout personalizado
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setModoInmersivo();
                }
            })
            .setCancelable(false)
            .show();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA ESTABLECER LA ANIMACIÓN DEL LOGOTIPO PRINCIPAL
     */
    public void animacionLogo(){
        //Declarar las animaciones
        final Animation animacionEncoger = AnimationUtils.loadAnimation(this, R.anim.encoger);
        final Animation animacionAgrandar = AnimationUtils.loadAnimation(this, R.anim.agrandar);

        //Establecer funcionamiento en bucle, al acabar una, empieza otra
        animacionEncoger.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            //Cuando se finaliza la ejecución se vuelve a ejecutar
            @Override
            public void onAnimationEnd(Animation animation) {
                ivLogo.startAnimation(animacionAgrandar);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animacionAgrandar.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivLogo.startAnimation(animacionEncoger);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Empezar la animacion
        ivLogo.startAnimation(animacionEncoger);
    }


}