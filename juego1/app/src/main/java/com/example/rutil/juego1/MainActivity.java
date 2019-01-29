package com.example.rutil.juego1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //Variables que pasaremos con el intent
    public final static String FILAS="nFilas";
    public final static String COLUMNAS="nColumnas";
    public final static String ELEMENTOS="nElementos";
    public final static String VIBRAR="vibrar";
    public final static String SONAR="sonar";
    public final static String NUMEROS="numeros";
    public final static String TOTAL_CLICK="totalClick";
    public final static int REQUEST_CODE=1234;

    //------------------------

    private SeekBar sbFilas, sbColumnas, sbElementos;
    private CheckBox ckSonar, ckVibrar;
    private RadioButton rbNumeros, rbColores;
    private TextView tvFilas, tvColumnas, tvElementos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sbFilas = (SeekBar) findViewById(R.id.sbFilas);
        sbColumnas = (SeekBar) findViewById(R.id.sbColumnas);
        sbElementos = (SeekBar) findViewById(R.id.sbNumEle);

        ckSonar = (CheckBox) findViewById(R.id.ckSonar);
        ckVibrar = (CheckBox) findViewById(R.id.ckVibrar);

        rbColores = (RadioButton) findViewById(R.id.rbColores);
        rbNumeros = (RadioButton) findViewById(R.id.rbNumeros);

        tvColumnas = (TextView) findViewById(R.id.txtColumnas);
        tvFilas = (TextView) findViewById(R.id.txtFilas);
        tvElementos = (TextView) findViewById(R.id.txtElementos);

        //Añadimos listener

        sbFilas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Llamada al metodo que hemos creado para actualizar
                actualizaFila(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbColumnas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Llamada al metodo que hemos creado para actualizar
                actualizaColumna(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbElementos.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Llamada al metodo que hemos creado para actualizar
                actualizaElemento(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //----------------------------------------------------------------------------------------------

    // METODOS PARA ACTUALIZAR EL TEXTO CON EL VALOR ADECUADO EN FUNCION DEL SEEKBAR
    public void actualizaFila(int p){
        //Actualizamos el valor en el texto sumando 3 ya que por defecto el valor minimo es 3
        tvFilas.setText(""+(p+3));
    }

    public void actualizaColumna(int p){
        //Actualizamos el valor en el texto sumando 3 ya que por defecto el valor minimo es 3
        tvColumnas.setText(""+(p+3));
    }

    public void actualizaElemento(int p){
        //Actualizamos el valor en el texto sumando 2 ya que por defecto el valor minimo es 2
        tvFilas.setText(""+(p+2));
    }

    //----------------------------------------------------------------------------------------------

    //---- MENU ----

    /**
     * METODO PARA PINTAR EL MENU EN LA APLICACIÓN (PARA QUE SE MUESTRE)
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    /**
     * METODO PARA ACTUAR A LA HORA DE SELECCIONAR UNA OPCIÓN DEL MENU CONTEXTUAL CREADO
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.itemAyuda:
                mostrarAyuda();
                break;

            case R.id.itemAbout:
                mostrarAbout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------

    //METODOS PARA CARGAR LOS ACTIVITY DE LAS OPCIONES DEL MENU
    public void mostrarAyuda(){
        Intent i = new Intent(this, AyudaActivity.class);
    }

    public void mostrarAbout(){
        Intent i = new Intent(this, AboutActivity.class);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SERÁ EJECUTADO AL PULSAR EL BOTÓN JUGAR
     * @param view
     */
    public void jugar(View view){
        Intent i = new Intent(this, JuegoActivity.class);
        //Es un conjunto de elementos (saco) que podemos pasar a traves del intent en lugar
        //de pasar dato a dato.
        Bundle datos = new Bundle();

        //Guardar los valores que vamos a pasar por el intent en variables
        int filas, columnas, elementos, vibrar, sonar, numeros;
        filas=sbFilas.getProgress()+3;
        columnas=sbColumnas.getProgress()+3;
        elementos=sbElementos.getProgress()+2;
        //Estructura condicional simplificada
        sonar = ckSonar.isChecked() ? 1 : 0;
        vibrar = ckVibrar.isChecked() ? 1 : 0;
        numeros = rbNumeros.isChecked() ? 1 : 0;

        //Guardar datos de las variables en el Bundle
        datos.putInt(FILAS, filas);
        datos.putInt(COLUMNAS, columnas);
        datos.putInt(ELEMENTOS, elementos);
        datos.putInt(SONAR, sonar);
        datos.putInt(VIBRAR, vibrar);
        datos.putInt(NUMEROS, numeros);

        //Metodo para para pasar el bundle de datos a traves del intent
        i.putExtras(datos);
        //Lanzar la activity con este metodo ya que la activity que abriremos devolvera tambien resultados
        startActivityForResult(i, REQUEST_CODE);
    }

    //----------------------------------------------------------------------------------------------

    /**
     *  METODO QUE ACTUA CUANDO SE FINALIZA EL ACTIVITY INCIADO CON EL METODO startActivityForResult
     *  PODEMOS OBTENER LOS DATOS ENVIADOS DESDE EL ACTIVITY QUE SE HA INICIADO.
     * @param reqCode
     * @param resCode
     * @param datos
     */
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent datos){
        if(reqCode==REQUEST_CODE && resCode==RESULT_OK){
            //Obtener el numero de click totales
            int totalClicks = datos.getIntExtra(TOTAL_CLICK, 0);
            //Mostrar mensaje
            Toast.makeText(this, "Finalizaste el juego con un total de "+totalClicks+"click", Toast.LENGTH_SHORT).show();
        }

        if(reqCode==REQUEST_CODE && resCode==RESULT_CANCELED){
            Toast.makeText(this, "El juego se canceló", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(reqCode, resCode, datos);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------



}
