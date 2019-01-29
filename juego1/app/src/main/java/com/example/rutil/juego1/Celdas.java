package com.example.rutil.juego1;

import android.content.Context;

/**
 * CLASE PARA PERSONALIZAR LOS BOTONES DEL JUEGO
 */
public class Celdas extends android.support.v7.widget.AppCompatButton {
    int idButon, numeroElementos, contenidoCelda, fondo, fila, columna;

    /**
     * CONSTRUCTOR PARAMETRIZADO
     * @param context
     * @param id
     * @param nE
     * @param cC
     * @param fond
     * @param fil
     * @param col
     */
    public Celdas(Context context, int id, int nE, int cC, int fond, int fil, int col) {
        super(context);
        idButon=id;
        numeroElementos=nE;
        contenidoCelda=cC;
        fondo=fond;
        fila=fil;
        columna=col;


        this.setBackgroundResource(fondo);
    }

    /**
     *  METODO QUE DEVUELVE EL VALOR DEL FONDO, SI LLEGA AL FINAL DEL LISTADO DEVUELVE EL 1 DE NUEVO
     * @return
     */
    public int getSiguienteFondo(){
        //Al pulsar una celda incremento en 1 el numero o color de la celda
        contenidoCelda++;
        //Si hemos llegado al maximo valor de la celda la reseteamos a 1.
        if(contenidoCelda==numeroElementos) //Si se cumple esta condición me he salido del array
            contenidoCelda=0;
        return contenidoCelda;
    }
}
