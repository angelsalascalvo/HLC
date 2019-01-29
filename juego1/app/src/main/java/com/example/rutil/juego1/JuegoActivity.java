package com.example.rutil.juego1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JuegoActivity extends AppCompatActivity {

    //En android studio las imagenes se identifican con un numero entero identificador, por esta razón
    //podemos crear un array con la imagenes con un array de enteros (int)
    int[] numeros = new int []{
            R.drawable.n1,
            R.drawable.n2,
            R.drawable.n3,
            R.drawable.n4,
            R.drawable.n5,
            R.drawable.n6
    };

    int[] colores = new int[]{
            R.drawable.ic_1c,
            R.drawable.ic_2c,
            R.drawable.ic_3c,
            R.drawable.ic_4c,
            R.drawable.ic_5c,
            R.drawable.ic_6c
    };

    //Variable para cargar una copia del array de colores o numeros segun escoja el usuario
    //Trabajaremos con este array en la funcionalidad
    protected  int [] tablero;
    protected int [][] valoresCeldas; //Contendrá el valor de cada una de las celdas
    int [][] idCeldas; // Crear un id para cada celda

    MatrizJuego miMatriz; //Clase Matrizjuego.java
    LinearLayout layoutTablero; //Referencia al layout de juego
    int filas, columnas, nElementos, vibrar, esNumero, sonar, numClick, altura;

    Chronometer cronometro;
    TextView tvClicks; //Numero de clicks
    Vibrator vibService; //Para el servicio de vibración
    MediaPlayer mp; //Para el sonido

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        //REFERENCIAS
        cronometro = (Chronometer) findViewById(R.id.cronometro);
        tvClicks = (TextView) findViewById(R.id.tvClicks);

        layoutTablero = (LinearLayout) findViewById(R.id.layout_tablero);
        layoutTablero.removeAllViews(); //Vacia el layout si tiene algo previamente

        //Llamar al metodo que se encarga de recoger el contenido que envia el activity main.
        cogerDatos();

        //Si hemos seleccionado vibrar, iniciamos el servicio
        //IMPORTANTE! Dar permisos de vibración en el manifest
        if(vibrar==1){
            vibService=(Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        }

        //Si hemos seleccionado sonido inicializamos el objeto media player
        if(sonar==1){
            mp=MediaPlayer.create(this, R.raw.touch);
        }

        idCeldas= new int[filas][columnas];

        //Inicializar la matriz de juego
        miMatriz = new MatrizJuego(filas,columnas,nElementos);
        valoresCeldas=miMatriz.getMatriz();

        //Elegimos colores o numeros en funcion de si es numero o no
        //(en funcion del radioButton numero)
        if(esNumero==1){
            tablero=numeros;
        }else {
            tablero=colores;
        }

        //A partir de aqui nos olvidamos de numeros o colores y actuamos directamente con tablero.


        //Obtenemos el tamaño en altura de cada fila de botones o celdas
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //Obtenemos el tamaño de la pantalla, restamos 180 que equivale con la barra del cronometro y restamos
        //la cantidad de pixeles entre el numero de filas. Obteniendo el tamaño en altura de cada una de las filas
        altura=(dm.heightPixels-180)/filas;
        Log.d("DATOS FILAS", "filas= "+filas+ " Columnas= "+columnas+ " elementos=" +
                ""+nElementos+ " Altura="+altura+"\n");

        numClick=0;
        iniciarJuego();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA RECOGER LOS DATOS QUE LLEGAN CON EL INTENT DEL MAIN
     */
    public void cogerDatos(){
        //Creo un bundle (contenedor) para almacenar el contenido que se enviaba
        Bundle datos = getIntent().getExtras();
        //Obtener cada uno de los datos a partir del bundle
        filas = datos.getInt(MainActivity.FILAS);
        columnas = datos.getInt(MainActivity.COLUMNAS);
        nElementos = datos.getInt(MainActivity.ELEMENTOS);
        esNumero = datos.getInt(MainActivity.NUMEROS);
        vibrar = datos.getInt(MainActivity.VIBRAR);
        sonar = datos.getInt(MainActivity.SONAR);

    }

    //----------------------------------------------------------------------------------------------

    public void iniciarJuego(){
        int indiceBoton=0;
        int valor=0;

        for(int i=0; i<filas; i++){
            //Creamos un layout por fila
            LinearLayout lyFila = new LinearLayout(this);
            lyFila.setOrientation(LinearLayout.HORIZONTAL);
            //Añado los diferentes botones para cada uno de las filas
            for(int j=0; j<columnas; j++){
                //Obtengo el valor equivalente al
                valor = valoresCeldas[i][j];
                //Creo cada uno de los botones
                Celdas celda = new Celdas(this,++indiceBoton,nElementos,valor,tablero[valor],i,j);
                Log.d("DATOS FILAS", "filas= "+valor);
                //Establecer identificador interno al boton creado
                celda.setId(indiceBoton);
                idCeldas[i][j]=indiceBoton;
                celda.setLayoutParams(new LinearLayout.LayoutParams(0,altura,1.0f)); //El 1.0 es el peso o prioridad que tendrá cada boton (todos seran iguales)

                //Establecer listener al boton para actuar al pulsar el boton
                celda.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Es necesario obtener el valor de la fila y la columna porque realmente celda es un elemento
                        // de una clase diferente a esta. No podemos utilizar directamente las variables i - j
                        //El casteo es debido a que el view realmente es de tipo Celdas
                        pulsarCelda(((Celdas)v).fila, ((Celdas)v).columna);
                    }
                });

                //Añadimos al layout fila la celda creada.
                lyFila.addView(celda);
            }

            //Añadimos al tablero la fila creada
            layoutTablero.addView(lyFila);
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA ACTUAR AL PULSAR UN BOTÓN
     * @param f
     * @param c
     */
    public void pulsarCelda(int f, int c){
        cronometro.start();
        //Reproducir sonido
        if(sonar==1){
            mp.start();
        }
        //Vibrar telefono
        if (vibrar==1){
            vibService.vibrate(80);
        }
        //Aumentar en 1 el numero de clic y escribirlo
        numClick++;
        tvClicks.setText(""+numClick);

        //CODIGO PARA RECORRER LOS BOTONES COLINDANTES DEL PULSADO EN FORMA DE +

        //Bucle para recorrer HORIZONTALMENTE los botones colindantes sin salirse del panel
        for(int i =maximo(0,f-1); i<=minimo(filas-1, f+1); i++){
            cambiar(i, c);
           // Log.d("FILASSS", "Mandando celda: " +i)
        }

        //Bucle para recorrer VERTICALMENTE los botones colindantes sin salirse del panel
        for(int i =maximo(0,c-1); i<=minimo(c+1, columnas-1); i++){
            //Condicional para evitar que la celda central vuelva a cambiar
            if (i!=c) {
                cambiar(f, i);
            }
        }

        //Llamar al metodo para comprobar si se ha ganado la partida
        checkGanar();

    }
    //----------------------------------------------------------------------------------------------

    public void cambiar(int f, int c){
        //ID de la celda a cambiar
        int idCelda = idCeldas[f][c];

        //Recuperar la celda
        Celdas celda = (Celdas) findViewById(idCelda);

        //Cojo el nuevo fondo
        int nuevoValor = celda.getSiguienteFondo();

        //Asigno el nuevo valor de fondo al array de los valores de las celdas
        //Esta variable se utilizará para comprobar si se ha ganado la partida en checkGanar();
        valoresCeldas[f][c]= nuevoValor;

        //Cambiar el boton con el nuevo fondo
        celda.setBackgroundResource(tablero[nuevoValor]);
        celda.invalidate(); //Necesario para evitar errores al pintar el fondo
    }

    //----------------------------------------------------------------------------------------------

    public void  checkGanar(){
        //Almacenar el valor de la primera celda
        int valorSeleccionado = valoresCeldas[0][0];

        //Recorrer la matriz para comprobar que todos los valores sean iguales que el de la primera celda
        for (int i=0; i<filas; i++){
            for (int j=0; j<columnas; j++){
                if(valoresCeldas[i][j]!=valorSeleccionado)
                    return;
            }
        }

        //Si llegamos a este punto significa que hemos ganado

        Intent i = new Intent();
        //Pasar por el activity el total de click al main result
        i.putExtra(MainActivity.TOTAL_CLICK, numClick);
        //Enviamos la señal de que el resultado ha sido correcto
        setResult(RESULT_OK,i);
        //finalizar el activity
        finish();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA CALCULAR EL MÁXIMO DE 2 NUMEROS
     * @param a
     * @param b
     * @return
     */
    public int maximo(int a, int b){
        if(a>b)
            return a;
        else
            return b;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA CALCULAR EL MINIMO DE 2 NUMEROS
     * @param a
     * @param b
     * @return
     */
    public int minimo (int a, int b){
        if(a<b)
            return a;
        else
            return b;
    }
}
