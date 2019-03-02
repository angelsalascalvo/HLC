package com.example.rutil.practicajuegofinalangelsalascalvo;

import android.app.Service;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class ActivityJuego extends BaseActivity {

    private BarraCohete bcCohete1, bcCohete2, bcAstronauta, bcOcCohete1, bcOcCohete2;
    private ImageView ivBotonJug1, ivBotonJug2;
    private LinearLayout lyBotonJ1, lyBotonJ2;
    private boolean bj1Pulsado, bj2Pulsado, arrancado, cohete1Parado, cohete2Parado, fin, vibrar, sonar;
    private TextView tvMarcadorJ1, tvMarcadorJ2, tvInfo, tvNomJ1, tvNomJ2;
    private int velocidad, numRondas, ronda, distanciaJug1, distanciaJug2, contJ1, contJ2;
    private String nomJ1, nomJ2;
    private MovCohetes movCohetes;
    private MediaPlayer mpFondo, mpPulsar, mpCohete, mpResumen, mpGanador;
    private Vibrator vibService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        //Modo pantalla completa
        setModoInmersivo();
        //Referenciar los elementos de la vista
        referenciar();
        //Coger los datos pasador por el intent
        cogerDatos();
        //Audio y sonido
        sonidoVibrar();
        //Iniciar las variables del juego
        iniciarVariables();
        bcAstronauta.setEnabled(false);
        bcCohete1.setEnabled(false);
        bcCohete2.setEnabled(false);
        ronda=0;
        //Poner los botones a la escucha
        botonesEscucha();
        //Situar astronauta en posicion aleatoria
        situarAstronauta();
        //Mostrar dialogo inicial partida
        dialogoInicio();
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

    /**
     * SOBRESCRITURA DEL METODO onPause
     */
    @Override
    public void onPause(){
        //Detener la musica si esta sonando
        if(sonar || mpFondo!=null)
            mpFondo.pause();
        super.onPause();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA REFERENCIAR LOS ELEMENTOS DEL LAYOUT
     */
    public void referenciar(){
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

    /**
     * METODO PARA INICIALIZAR VARIABLES DE SONIDO Y VIBRACIÓN SI SE ACTIVA
     */
    public void sonidoVibrar(){
        //Iniciar audios
        if(sonar) {
            mpFondo = MediaPlayer.create(this, R.raw.fondo);
            mpFondo.start();
            mpFondo.setLooping(true);
            mpPulsar = MediaPlayer.create(this, R.raw.clic);
            mpCohete = MediaPlayer.create(this, R.raw.choete);
            mpResumen=MediaPlayer.create(this, R.raw.resumen);
            mpGanador=MediaPlayer.create(this, R.raw.ganador);
        }
        if(vibrar)
            vibService=(Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA ESTABLECER EL VALOR INICIAL DE TODAS LAS VARIABLES DEL JUEGO
     */
    public void iniciarVariables(){
        //Inicializar variables
        bj1Pulsado=false;
        bj2Pulsado=false;
        arrancado=false;
        cohete1Parado=true;
        cohete2Parado=true;
        fin=false;
        tvNomJ1.setText(nomJ1);
        tvNomJ2.setText(nomJ2);

        //Establecer imagen
        bcCohete1.setThumb(getResources().getDrawable(R.drawable.cohete_r_off));
        bcCohete2.setThumb(getResources().getDrawable(R.drawable.cohete_a_off));
        bcCohete1.setVisibility(View.VISIBLE);
        bcCohete2.setVisibility(View.VISIBLE);

        //Ocultar naves con interrogante
        bcOcCohete1.setVisibility(View.INVISIBLE);
        bcOcCohete2.setVisibility(View.INVISIBLE);

        //Situar las naves en posicion inicial
        bcCohete1.setProgress(0);
        bcCohete2.setProgress(0);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA MOSTRAR EL DIALOGO INICIAL AL EMPEZAR UNA PARTIDA
     */
    public void dialogoInicio(){
        //Mostrar dialogo inicial de partida
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

        //Toast.makeText(this, posicion+"", Toast.LENGTH_SHORT).show();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA ARRANCAR LAS NAVES ESPACIALES Y LLAMAR A LA TAREA ASINCRONA QUE LAS MOVERA
     */
    public void arrancarNaves(){
        //Establecer velocidad aleatoria
        Random random = new Random();
        velocidad=random.nextInt(16)+9;
        //Toast.makeText(this, velocidad+"",Toast.LENGTH_SHORT).show();
        //Indicar que los cohetes no estan parados
        cohete1Parado=false;
        cohete2Parado=false;

        //Emitir sonido
        if(sonar){
            mpCohete.seekTo(0);
            mpCohete.start();
        }
        //Vibrar
        if(vibrar)vibService.vibrate(100);

        //Establecer imagen
        bcCohete1.setThumb(getResources().getDrawable(R.drawable.cohete_r_on));
        bcCohete2.setThumb(getResources().getDrawable(R.drawable.cohete_a_on));

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
                //Vibrar
                if(vibrar)vibService.vibrate(100);
                //Cambiar color del boton
                ivBotonJug1.setBackgroundColor(getResources().getColor(R.color.jRojo));
                tvNomJ1.setBackgroundColor(getResources().getColor(R.color.jRojo));
                //Comprobar si el otro boton tambien se ha soltado
                if((!bj2Pulsado) && arrancado)
                    finRonda();
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
                //Vibrar
                if(vibrar)vibService.vibrate(100);
                //Cambiar color del boton
                ivBotonJug2.setBackgroundColor(getResources().getColor(R.color.jAzul));
                tvNomJ2.setBackgroundColor(getResources().getColor(R.color.jAzul));

                //Comprobar si el otro boton tambien se ha soltado
                if((!bj1Pulsado) && arrancado)
                    finRonda();
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SE EJECUTA CUANDO AMBOS COHETEN PARAN
     */
    public void finRonda(){
        fin=true;
        //Mostrar Cohete
        bcOcCohete1.setVisibility(View.INVISIBLE);
        bcCohete1.setVisibility(View.VISIBLE);
        bcOcCohete2.setVisibility(View.INVISIBLE);
        bcCohete2.setVisibility(View.VISIBLE);

        //Detener sonido si esta en reproduccion
        if(sonar) mpCohete.pause();
        if(sonar)mpPulsar.start();

        //Desactivar botones
        lyBotonJ1.setEnabled(false);
        lyBotonJ2.setEnabled(false);

        //Calcular distancias
        distanciaJug1 = bcAstronauta.getProgress()-bcCohete1.getProgress();
        distanciaJug2 = bcAstronauta.getProgress()-bcCohete2.getProgress();
        //Quitar simbolo negativo
        if(distanciaJug1<0) distanciaJug1*=-1;
        if(distanciaJug2<0) distanciaJug2*=-1;

        //Si no hay empate se suma el numero de ronda
        if(distanciaJug1!=distanciaJug2)
            ronda++;

        //Mostrar info
        tvInfo.setVisibility(View.VISIBLE);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA REINICIAR TODOO CARGAR LA SIGUIENTE RONDA DE LA PARTIDA
     */
    public void siguienteRonda(){
        //Comprobar si se ha finalizado el juego
        if(ronda==numRondas){
            //Reproducir sonido
            if(sonar)mpGanador.start();
            //Declarar layout para establecer datos
            LayoutInflater factory = LayoutInflater.from(this);
            View dialog = factory.inflate(R.layout.dialogo_ganador, null);

            //Referencias a los objetos del layout
            TextView tvTituloGanador = (TextView) dialog.findViewById(R.id.tvTituloGanador);
            TextView tvResumenFinal = (TextView) dialog.findViewById(R.id.tvResumenFinal);

            //Establecer valores
            if(contJ1>contJ2)
                tvTituloGanador.setText("ENHORABUENA "+nomJ1.toUpperCase()+"!");
            if(contJ2>contJ1)
                tvTituloGanador.setText("ENHORABUENA "+nomJ2.toUpperCase()+"!");

            tvResumenFinal.setText(nomJ1+" ("+contJ1+"p)  |  "+nomJ2+" ("+contJ2+"p)");


            //Mostrar dialogo con el resumen de la partida
            AlertDialog dialogoGanador = new AlertDialog.Builder(this)
                    .setView(dialog)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Al pulsar aceptar se vuelve a la pantalla principal
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        //En caso contrario reiniciamos los elementos
        }else{
            setModoInmersivo();
            //Establecer valores a los contadores
            tvMarcadorJ1.setText(""+contJ1);
            tvMarcadorJ2.setText(""+contJ2);
            //Reiniciar valores iniciales
            iniciarVariables();
            //Generar nueva posicion del astronauta
            situarAstronauta();
            //Activar botones
            lyBotonJ1.setEnabled(true);
            lyBotonJ2.setEnabled(true);

        }
        //Toast.makeText(this, ""+ronda, Toast.LENGTH_SHORT).show();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA MOSTRAR UN DIALOGO RESUMEN CON LOS DATOS DE LA RONDA JUGADA
     */
    public void dialogoResumen(){
        //Reproducir sonido
        if(sonar)mpResumen.start();
        //ocultar info
        tvInfo.setVisibility(View.INVISIBLE);

        //Declarar layout para establecer datos
        LayoutInflater factory = LayoutInflater.from(this);
        View dialog = factory.inflate(R.layout.dialogo_ronda, null);

        //Referencias a los objetos del layout
        TextView tvNumRonda = (TextView) dialog.findViewById(R.id.tvNumRonda);
        TextView tvDistanciaJ1 = (TextView) dialog.findViewById(R.id.tvDistanciaJ1);
        TextView tvDistanciaJ2 = (TextView) dialog.findViewById(R.id.tvDistanciaJ2);
        TextView tvGanadorRonda = (TextView) dialog.findViewById(R.id.tvGanadorRonda);
        TextView tvPuntuacion1 = (TextView) dialog.findViewById(R.id.tvPuntuacion1);
        TextView tvPuntuacion2 = (TextView) dialog.findViewById(R.id.tvPuntuacion2);

        //Establecer valores segun partida a los elementos del layout
        tvNumRonda.setText(ronda+"/"+numRondas);
        tvDistanciaJ1.setText(nomJ1+": "+distanciaJug1+"ft.");
        tvDistanciaJ2.setText(nomJ2+": "+distanciaJug2+"ft.");

        if(distanciaJug1<distanciaJug2){
            tvGanadorRonda.setText(nomJ1+" gana esta ronda!");
            contJ1++;
        }else if(distanciaJug2<distanciaJug1){
            tvGanadorRonda.setText(nomJ2+" gana esta ronda!");
            contJ2++;
        }else{
            tvGanadorRonda.setText("Empate, se repite la ronda!");
        }

        tvPuntuacion1.setText(""+contJ1);
        tvPuntuacion2.setText(""+contJ2);

        try {
            //Mostrar dialogo con el resumen de la partida
            AlertDialog dialogoInicio = new AlertDialog.Builder(this)
                    .setView(dialog)
                    .setPositiveButton("Siguiente", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            siguienteRonda();
                        }
                    })
                    .setCancelable(false)
                    .show();
            }
        catch (WindowManager.BadTokenException e) {
            //Capturar excepción que puede producirse al intentar mostrar el dialogo sin tener el foco la ventana
        }
    }

    //==============================================================================================

    /**
     * TAREA ASINCRONA PARA MOVER DE POSICIÓN LAS SEEKBAR DE LAS NAVES ESPACIALES
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
            //Permanecer mientras no se suelten los botones
            while(fin==false){
                SystemClock.sleep(100);
            }
            //Pausa
            SystemClock.sleep(2500);
            //Lamada al metodo onProgressUpdate para mostrar dialogo resumen
            movCohetes.publishProgress(-1);
            return null;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * METODO PARA PUBLICAR CAMBIOS DE PROCESO, TRABAJAR CON LA VISTA
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values){
            Log.d("navess", ""+values[0]);

            if(values[0]==-1) {
                dialogoResumen();
            }else {
                //Comprobar que no se ha detenido ya el cohete
                if (!cohete1Parado) {
                    bcCohete1.setProgress(values[0]);
                }

                //Comprobar que no se ha detenido ya el cohete
                if (!cohete2Parado) {
                    bcCohete2.setProgress(values[0]);
                }

                //Al llegar a 200 ocultar naves
                if (values[0] == 200) {
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
        }
    }
}