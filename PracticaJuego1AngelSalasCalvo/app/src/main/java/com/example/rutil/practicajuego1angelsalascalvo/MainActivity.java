package com.example.rutil.practicajuego1angelsalascalvo;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LinearLayout lyTablero;
    private Button [][] hoyos;
    private ImageButton ibJugar;
    private Chronometer cronometro;
    private boolean jugando;
    private int fil=0, col=0; //Filas y columas para el panel de hoyos
    private int meta; //Valor para finalizar partida

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializar();
        generarHoyos();

    }

    public void inicializar(){
        ibJugar=(ImageButton) findViewById(R.id.ibJugar);
        cronometro=(Chronometer) findViewById(R.id.cronometro);
        lyTablero=(LinearLayout) findViewById(R.id.lyTablero);

        int dificultad=2;
        jugando=true;

        //En funcion de la dificultad establecemos valores de juego
        switch (dificultad){
            case 1:
                fil=2;
                col=2;
                meta=10;
                break;
            case 2:
                fil=3;
                col=2;
                meta=15;
                break;
            case 3:
                fil=3;
                col=3;
                meta=30;
                break;
        }
    }

    public void generarHoyos(){
        //inicializar array botones
        hoyos = new Button[fil][col];
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
                Button buttonHoyo = new Button(this);
                //Establecer los parametros de tamaño a los botones para ajustarlos al maximo
                buttonHoyo.setLayoutParams(lp);

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


    public void pulsadoHoyo(View v){
        //Comprobar si la partida esta iniciada
        if(jugando) {
            if (v == hoyos[0][0])
                hoyoAleatorio().setBackgroundColor(Color.RED);
        }
    }


    /**
     * METODO QUE DEVUELVE UN HOYO/BOTON DEL ARRAY DE FORMA ALEATORIA
     * @return
     */
    public Button hoyoAleatorio(){
        Random r = new Random(System.currentTimeMillis());
        //Numero Aleatorio entre 0 y numFilas
        int aleatF = r.nextInt(fil);
        //Numero Aleatorio entre 0 y numColumnas
        int aleatC = r.nextInt(col);

        //Devolver el boton del array correspondiente
        return hoyos[aleatF][aleatC];
    }















    // !!!!!!!!!!!!!!!!! BORRAR !!!!!!!!!!!!!!!!!!!!!!!!!!
    /**
     * METODO PARA CALCULAR ALTURA DDE CADA BOTON/HOYO EN FUNCION DE LA PANTALLA
     * @return
     */
    public int calcAlturaHoyo(){
        //Variable con los parametros de la pantalla
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //Obtener altura del layout superior en pixeles convirtiendo los 50 dp en px
        int pxSuperior = Math.round(50 * (dm.densityDpi / 160f));
        //Obtener altura de la pantalla
        int pxPantalla = dm.heightPixels;

        //Calcular altura de cada boton
        return (pxPantalla-pxSuperior)/fil;
    }
}
