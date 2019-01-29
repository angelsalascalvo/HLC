package com.example.rutil.juego1;

import java.util.Random;

public class MatrizJuego {
    private int filas, columnas, elementos;
    private int[][] matriz;

    /**
     * CONSTRUCTOR PARAMETRIZADO
     * @param f
     * @param c
     * @param e
     */
    public MatrizJuego(int f, int c, int e){
        filas = f;
        columnas = c;
        elementos = e;
        matriz=new int [f][c];
        rellenarMatriz();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA RELLENAR LA MATRIZ O ARRAY BIDIMENSIONAL CON NUMEROS ALEATORIOS
     */
    public void rellenarMatriz(){
        Random r = new Random(System.currentTimeMillis());

        for (int i =0; i<filas; i++){
            for (int j=0; j<columnas; j++){
                //Generar numero de forma aleatoria
                matriz[i][j]=r.nextInt(elementos);
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA DEVOLVER LA MATRIZ CREADA CON NUMEROS ALEATORIOS
     * @return
     */
    public int [][] getMatriz(){
        return matriz;
    }
}
