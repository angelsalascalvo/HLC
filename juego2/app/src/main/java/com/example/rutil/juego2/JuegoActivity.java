package com.example.rutil.juego2;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class JuegoActivity extends BaseActivity implements DialogoNombreNivel.onDialogoNombreNivel, View.OnClickListener { //Heredamos de baseActivity  para usar el modo inmersivo

    //Variables para identificar los valores del bundle
    private static final String KEY_VELOCIDAD="velocidad";
    private static final String KEY_PROGRESO="progreso";

    //Variables de logica del juego
    private int velocidad, progreso, numFase, contadorBotones;

    //Variables
    private TextView tvNombre, tvProgreso, tvFase;
    private Button boton1,boton2,boton3,boton4;
    private ProgressBar proBar;
    private boolean partidaAcabada; //Partida acabada o no
    private ArrayList<Button> arrayBotones; //Guardamos los botones
    private Bundle estado; //Para guardar el juego en Pause

    //Varaible que referencia al proceso asincrono creado como una clase diferente en este mismo archivo
    private  ControlProgresoTask contProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        //Pantalla completa
        setModoInmersivo(); //Podemos usarlo ya que extendemos de BaseActivity

        //Cogemos todos los elementos
        setView();

        //Mostramos dialogo de inicio
        mostrarDialogo();

        //Ponemos listener a los botones
        ponerListener();
    }

    //----------------------------------------------------------------------------------------------

    public void setView(){
        tvNombre=(TextView)findViewById(R.id.tvNombre);
        tvFase=(TextView)findViewById(R.id.tvFase);
        tvProgreso=(TextView)findViewById(R.id.tvProgreso);

        boton1=(Button)findViewById(R.id.btn1);
        boton2=(Button)findViewById(R.id.btn2);
        boton3=(Button)findViewById(R.id.btn3);
        boton4=(Button)findViewById(R.id.btn4);

        proBar=(ProgressBar)findViewById(R.id.proBar);
        //Fijamos su tamaño para que se vea bien alto y ancho
        proBar.setScaleX(5f);
        proBar.setScaleY(5f);

        //Añadir los botones al array
        arrayBotones=new ArrayList<Button>();
        arrayBotones.add(boton1);
        arrayBotones.add(boton2);
        arrayBotones.add(boton3);
        arrayBotones.add(boton4);

        //Rellenamos los botones con numeros de forma desordenada
        numerarBotones(desordenarBotones());
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA MOSTRAR EL DIALOGO QUE PIDE LA CONFIGURACIÓN DEL JUEGO (DIFICULTAD Y NOMBRE)
     */
    public  void mostrarDialogo(){
        //Creamos el dialogo
        DialogoNombreNivel ventEmergente = DialogoNombreNivel.newInstance();
        //Desactiar la opcion de cancelar del dialogo porque se debe introducir la configuración a la fuerza
        ventEmergente.setCancelable(false);
        //Mostrar el dialogo que pide configuracion de la partida
        ventEmergente.show(getSupportFragmentManager(), "DialogoNombreNivel"); //Tag??
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA PONER A LA ESCUCHA LOS BOTONES
     */
    public void ponerListener(){
        //Ponemos el listenar con this porque hemos implementado onClick
        boton1.setOnClickListener(this);
        boton2.setOnClickListener(this);
        boton3.setOnClickListener(this);
        boton4.setOnClickListener(this);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE  PROPORCIONA UN ARRAYLIST CON LOS NUMEROS DEL 1 AL 4 DE MODO  DESORDENADO ALEATORIAMENTE
     * @return
     */
    public static ArrayList<Integer> desordenarBotones(){
        ArrayList<Integer> array = new ArrayList<>();
        //Rellenar un arraylist con los numeros del 1 al 4
        for(int i=1; i<5; i++){
            array.add(i);
        }

        Collections.shuffle(array); //Este metodo desordena los valores del array
        return array; //Devuelve el array desordenado
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA ASIGNAR UN ARRAYLIST DE NUMEROS A LOS DIFERENTES BOTONES DEL PANEL
     * @param array
     */
    public void numerarBotones(ArrayList<Integer> array){
        //Recorremos el arraylist de botones desordenados que se le pasa por parametro
        for(int i=0; i<array.size();i++){
            //Activamos y asignamos al boton cada numero del arrayList desordenado
            arrayBotones.get(i).setEnabled(true);
            arrayBotones.get(i).setText(""+array.get(i));
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SE DEBE ESCRIBIR YA QUE ES UNA INTERFAZ DE LA CLASE DIALOGONOMBRENIVEL QUE SE HA IMPLEMENTADO
     * SE EJECUTA AL PULSAR ACEPTAR SOBRE EL DIALOGO DE CONFIGURACION Y PODEMOS OBTENER LOS DATOS INTRODUCIDOS
     * EN DICHO CUADRO CON LOS PARAMETROS DEL PROPIO METODO
     * @param nombre
     * @param velocidad
     */
    @Override
    public void onAceptarDialogo(String nombre, int velocidad) {
        partidaAcabada=false;
        this.velocidad=velocidad;
        progreso=0;
        contadorBotones=1;
        numFase=1;
        //Modo Inmersivo
        setModoInmersivo();
        //si hemos mandado un nombrre lo asignamos al textview
        if(!nombre.trim().isEmpty()){
            tvNombre.setText(nombre);
        }

        //Indicamos la fase en el textView
        tvFase.setText(getString(R.string.fase, String.valueOf(numFase)));

        //iniciamos la tarea asincrona y la ejecutamso
        contProgreso=new ControlProgresoTask();
        contProgreso.execute(this.velocidad,progreso);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SE EJECUTA AL PULSAR CUALQUIERA DE LOS BOTONES, YA QUE EN EL METODO PONERLISTENER
     * LOS HEMOS PUESTO A LA ESCUCHA CON ESTA MISMA CLASE COMO OYENTE (THIS)
     * @param v
     */
    @Override
    public void onClick(View v) {
        Button botonPulsado = (Button) v;
        //cada vez que pulsamos el boton en orden correcto incrementamos el contador de botones y
        // desactivamos el boton
        int valorBoton = Integer.valueOf(botonPulsado.getText().toString()); //Obtener valor del boton pulsado

        if(valorBoton == contadorBotones){
            botonPulsado.setEnabled(false);
            contadorBotones++;

            //Si hemos pulsado todos los botones cancelamos el proceso de la barra de progreso
            //Es decir, detenemos el incremento de la barra de progreso
            if(contadorBotones==5) contProgreso.cancel(true);
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE PERMITIRÁ EJECUTAR LA BARRA DE PROGRESO EN SEGUNDO PLANO MIENTRAS SE CONTROLA
     * EL CONTROL DE BOTONES
     */
    //como parametros del asyncTask podemos pasar void si no queremos mandar nada
    private class ControlProgresoTask extends AsyncTask<Integer, Integer, Integer>{
        //                                          Parametros, Progreso, Resultado

        /**
         * METODO QUE SE EJECUTARÁ EN SEGUNDO PLANO
         * @param integers
         * @return
         */
        @Override
        protected Integer doInBackground(Integer... integers) {
            while(progreso<100){
                SystemClock.sleep(integers[0]);
                contProgreso.publishProgress(progreso); //llama a onProgress que permitirá publicar los cambios ejecutados en segundo plano
                progreso++;
                if(isCancelled())
                    break;
            }
            return progreso;
        }

        //------------------------------------------------------------------------------------------

        /**
         * METODO PARA PUBLICAR CAMBIOS DE PROGRESO
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values){
        //Los tres puntos ... es un operador que permite pasar por parametros muchos elementos de un determinado tipo
        //similar a un array pero x

            super.onProgressUpdate(values);
            proBar.setProgress(values[0]);
            tvProgreso.setText(values[0]+"/"+proBar.getMax()); //getMax obtiene el maximo de la barra
        }

        //------------------------------------------------------------------------------------------

        /**
         * METODO QUE SE EJECUTA AL FINALIZAR EL PROGRESO (CUANDO DEJA DE ESTAR VIVO EL METODO DOINBACKGROUND)
         * @param integer
         */
        @Override
        protected  void onPostExecute(Integer integer){
            //Hemos terminado, la barra ha llegado al 100%
            partidaAcabada=true;
            //Mostramos un dialogo diciendo que hemos perdido
            new AlertDialog.Builder(JuegoActivity.this)
                    .setTitle(R.string.fin)
                    .setMessage(R.string.perder)
                    //Podemos reiniciar el juego con boton aceptar
                    .setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tvNombre.setText(R.string.random_buttons);
                            mostrarDialogo();
                            numerarBotones(desordenarBotones());
                        }
                    })
                    //Podemos finalizar el juego con el boton de cancelar
                    .setNegativeButton(R.string.inicio, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

        //------------------------------------------------------------------------------------------

        /**
         * METODO PARA CANCELAR O DETENER EL PROGRESO
         * @param integer
         */
        @Override
        protected void onCancelled(Integer integer){
            //Cuando hemos ganado, la barra de progreso se detiene y pasamos de nivel o fase
            super.onCancelled(integer);
            if(contadorBotones==5){
                //Reiniciar todoo incrementando el nivel o fase en 1
                contProgreso=new ControlProgresoTask();
                numerarBotones(desordenarBotones());
                tvFase.setText(getString(R.string.fase, String.valueOf(++numFase)));
                contadorBotones=1;
                proBar.setProgress(0);

                //aumentamos velocidad controlando que esta no sea negativa
                if(velocidad-5 <=0)
                    velocidad=1;
                else
                    velocidad-=5;

                //Ejecutamos la tarea asincrona
                contProgreso.execute(velocidad,progreso);
            }
        }

    }
}
