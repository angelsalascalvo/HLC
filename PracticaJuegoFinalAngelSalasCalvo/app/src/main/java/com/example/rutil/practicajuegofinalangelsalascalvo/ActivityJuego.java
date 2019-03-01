package com.example.rutil.practicajuegofinalangelsalascalvo;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class ActivityJuego extends BaseActivity {

    private BarraCohete bcCohete1, bcCohete2, bcAstronauta, bcOcCohete1, bcOcCohete2;
    private ImageView ivBotonJug1, ivBotonJug2;
    private LinearLayout lyBotonJ1, lyBotonJ2;
    private boolean bj1Pulsado, bj2Pulsado, arrancado, cohete1Parado, cohete2Parado, fin, vibrar, sonar;
    private TextView tvMarcadorJ1, tvMarcadorJ2, tvInfo, tvNomJ1, tvNomJ2;
    private int velocidad, numRondas, ronda;
    private String nomJ1, nomJ2;
    private MovCohetes movCohetes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        //Modo pantalla completa
        setModoInmersivo();
        //Referencias
        ivBotonJug1 = (ImageView) findViewById(R.id.ivBotonJug1);
        ivBotonJug2 = (ImageView) findViewById(R.id.ivBotonJug2);
        bcCohete1 = (BarraCohete) findViewById(R.id.bcJugador1);
        bcCohete2 = (BarraCohete) findViewById(R.id.bcJugador2);
        bcAstronauta = (BarraCohete) findViewById(R.id.bcAstronauta);
        bcOcCohete1 = (BarraCohete) findViewById(R.id.bcOcJugador1);
        bcOcCohete2 = (BarraCohete) findViewById(R.id.bcOcJugador2);
        tvMarcadorJ1 = (TextView) findViewById(R.id.tvMarcadorJ1);
        tvMarcadorJ2 = (TextView) findViewById(R.id.tvMarcadorJ2);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvNomJ1 = (TextView) findViewById(R.id.tvNomJ1);
        tvNomJ2 = (TextView) findViewById(R.id.tvNomJ2);
        lyBotonJ1=(LinearLayout)findViewById(R.id.lyBotonJ1);
        lyBotonJ2=(LinearLayout)findViewById(R.id.lyBotonJ2);

        //Coger los datos pasador por el intent
        cogerDatos();

        //Inicializar variables
        bj1Pulsado=false;
        bj2Pulsado=false;
        arrancado=false;
        cohete1Parado=true;
        cohete2Parado=true;
        fin=false;
        tvNomJ1.setText(nomJ1);
        tvNomJ2.setText(nomJ2);
        ronda=1;

        //Ocultar naves con interrogante
        bcOcCohete1.setVisibility(View.INVISIBLE);
        bcOcCohete2.setVisibility(View.INVISIBLE);

        //bcAstronauta.setEnabled(false);
        //bcCohete1.setEnabled(false);
        //bcCohete2.setEnabled(false);

        //Poner los botones a la escucha
        botonesEscucha();
        //Situar astronauta en posicion aleatoria
        situarAstronauta();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA RECOGER LOS DATOS QUE LLEGAN CON EL INTENT DEL MAIN
     */
    public void cogerDatos(){
        //Creo un bundle (contenedor) para almacenar el contenido que se enviaba
        Bundle datos = getIntent().getExtras();
        //Obtener cada uno de los datos a partir del bundle
        nomJ1 = datos.getString(MainActivity.NOMJ1);
        nomJ2 = datos.getString(MainActivity.NOMJ2);
        numRondas = datos.getInt(MainActivity.NUMRONDAS);
        vibrar = datos.getBoolean(MainActivity.VIBRAR);
        sonar = datos.getBoolean(MainActivity.SONAR);
    }

    //----------------------------------------------------------------------------------------------

    public void dialogoInicioRonda(){
        //Comprobar si es la primera ronda
        if(ronda==1){

        //Comprobar si es la ultima ronda
        }else if(ronda==numRondas){

        //Si es otra ronda cualquiera
        }else{

        }
        AlertDialog dialogoInicio = new AlertDialog.Builder(this)
                .setView(R.layout.dialogo_inicio_ronda) //Layout personalizado
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
     * METODO PARA COLOCAR EL ASTRONAUTA EN UNA POSICION ALEATORIA ENTRE 300 Y 1000
     */
    public void situarAstronauta(){
        Random random = new Random();
        //Se situará entre 300 y 1000
        int posicion=random.nextInt(700)+300;
        bcAstronauta.setProgress(posicion);

        Toast.makeText(this, posicion+"", Toast.LENGTH_SHORT).show();
    }

    //----------------------------------------------------------------------------------------------

    public void arrancarNaves(){
        //Establecer velocidad aleatoria
        Random random = new Random();
        velocidad=random.nextInt(18)+9;
        Toast.makeText(this, velocidad+"",Toast.LENGTH_SHORT).show();
        //Indicar que los cohetes no estan parados
        cohete1Parado=false;
        cohete2Parado=false;

        //Creamos tarea asincrona y la ejecutamos
        movCohetes=new MovCohetes();
        movCohetes.execute();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA PONER LOS BOTONES A LA ESCUCHA Y DECIDIR LO QUE SUCEDE AL PULSARLOS
     */
    public void botonesEscucha(){
        //Al pulsar el boton del jugador1
        lyBotonJ1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Actualizar variable
                bj1Pulsado=true;
                //Cambiar color del boton
                ivBotonJug1.setBackgroundColor(getResources().getColor(R.color.colorPulsado));
                tvNomJ1.setBackgroundColor(getResources().getColor(R.color.colorPulsado));
                //Comprobar si el otro boton esta pulsado
                if(bj2Pulsado){
                    //Comprobar que no esten moviendose las naves
                    if(!arrancado) {
                        //Mover naves
                        arrancarNaves();
                        arrancado=true;
                    }
                }
                return false;
            }
        });
        //Al soltar el boton
        lyBotonJ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Actualizar variable
                bj1Pulsado=false;
                cohete1Parado=true;
                //Cambiar color del boton
                ivBotonJug1.setBackgroundColor(getResources().getColor(R.color.jRojo));
                tvNomJ1.setBackgroundColor(getResources().getColor(R.color.jRojo));
                //Comprobar si el otro boton tambien se ha soltado
                if((!bj2Pulsado) && arrancado)
                    finPartida();
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////

        //Al pulsar el boton del jugador2
        lyBotonJ2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Actualizar variable
                bj2Pulsado=true;
                //Cambiar color del boton
                ivBotonJug2.setBackgroundColor(getResources().getColor(R.color.colorPulsado));
                tvNomJ2.setBackgroundColor(getResources().getColor(R.color.colorPulsado));
                //Comprobar si el otro boton esta pulsado
                if(bj1Pulsado){
                    //Comprobar que no esten moviendose las naves
                    if(!arrancado) {
                        //Mover naves
                        arrancarNaves();
                        arrancado=true;
                    }
                }
                return false;
            }
        });

        //Al soltar el boton del jugador2
        lyBotonJ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Actualizar variable
                bj2Pulsado=false;
                cohete2Parado=true;
                //Cambiar color del boton
                ivBotonJug2.setBackgroundColor(getResources().getColor(R.color.jAzul));
                tvNomJ2.setBackgroundColor(getResources().getColor(R.color.jAzul));

                //Comprobar si el otro boton tambien se ha soltado
                if((!bj1Pulsado) && arrancado)
                    finPartida();
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SE EJECUTA CUANDO AMBOS COHETEN PARA
     */
    public void finPartida(){
        fin=true;
        //Mostrar Cohete
        bcOcCohete1.setVisibility(View.INVISIBLE);
        bcCohete1.setVisibility(View.VISIBLE);
        bcOcCohete2.setVisibility(View.INVISIBLE);
        bcCohete2.setVisibility(View.VISIBLE);

        //Mostrar info
        tvInfo.setVisibility(View.VISIBLE);
    }


    /**
     * TAREA ASINCRONA PARA ACTUALIZAR LA BARRA DE PROGRESO
     */
    private class MovCohetes extends AsyncTask<Void, Integer, Void> {

        /**
         * METODO QUE SE EJECUTARÁ EN SEGUNDO PLANO
         * @return
         */
        @Override
        protected Void doInBackground(Void... values) {
            int posicionCohete=0;
            do{
                //Dormir ejecución
                SystemClock.sleep(velocidad);
                //Actualizar posicion cohete
                posicionCohete++;
                //Llamada al metodo que realizará los cambios en la vista
                movCohetes.publishProgress(posicionCohete);
            }while ((fin==false) && posicionCohete<1000);
            //Pausa
            SystemClock.sleep(5000);

            Log.d("FINN","S");
            return null;
        }

        /**
         * METODO PARA PUBLICAR CAMBIOS DE PROCESO
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values){
            Log.d("navess", ""+values[0]);

            //Comprobar que no se ha detenido ya el cohete
            if(!cohete1Parado) {
                bcCohete1.setProgress(values[0]);
            }

            //Comprobar que no se ha detenido ya el cohete
            if(!cohete2Parado) {
                bcCohete2.setProgress(values[0]);
            }

            //Al llegar a 200 ocultar naves
            if(values[0]==200) {
                bcOcCohete1.setProgress(200);
                bcOcCohete2.setProgress(200);
                //Comprobar que no se ha detenido antes
                if (bcCohete1.getProgress() >= 200) {
                    bcOcCohete1.setVisibility(View.VISIBLE);
                    bcCohete1.setVisibility(View.INVISIBLE);
                }
                if (bcCohete2.getProgress() >= 200) {
                    bcOcCohete2.setVisibility(View.VISIBLE);
                    bcCohete2.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        protected  void onPostExecute(Void value){

        }

        @Override
        protected void onCancelled(Void value){

        }
    }
}
