package com.example.rutil.practicajuego1angelsalascalvo;

import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Variables para referenciar datos pasados por intent
    public final static String NUM_TOPOS="num_Topos";
    public final static String NIVEL="nivel";
    public final static String VIBRAR="vibrar";
    public final static String SONAR="sonar";
    public final static String GANADO="ganado";
    public final static String TIEMPO="tiempo";
    public final static String TOPOSATRAPADOS="topos_Atrapados";
    public final static int REQUEST_CODE=1234;

    private RadioButton rbFacil, rbMedio, rbDificil;
    private SwitchCompat swSonar, swVibrar;
    private SeekBar sbNumTopos;
    private TextView tvNumTopos, tvToposFin, tvCronoFin, tvGanado;
    private boolean ganado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtener referencias de los elementos
        iniciar();

        //Actualizar el valor de la barra de numero de topos
        sbNumTopos.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Incremento de la barra de 10 en 10
                progress = ((int)Math.round(progress/10 ))*10;
                seekBar.setProgress(progress);
                tvNumTopos.setText(""+(sbNumTopos.getProgress()+10));
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

    /**
     * METODO PARA INICIALIZAR VISTA OBTENIENDO REFERENCIAS
     */
    public void iniciar(){
        rbFacil=(RadioButton) findViewById(R.id.rbFacil);
        rbMedio=(RadioButton) findViewById(R.id.rbMedio);
        rbDificil=(RadioButton) findViewById(R.id.rbDificil);

        swSonar=(SwitchCompat) findViewById(R.id.swSonar);
        swVibrar=(SwitchCompat) findViewById(R.id.swVibrar);

        sbNumTopos=(SeekBar) findViewById(R.id.sbNumTopos);
        tvNumTopos=(TextView) findViewById(R.id.tvContNumTopos);

        ganado=false;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SE EJECUTARÁ AL PULSAR EL BOTON DE JUGAR
     * Cargará el activity del juego
     * @param v
     */
    public void jugar(View v){
        Bundle datos = new Bundle();
        int nivel = 0;

        //Comprobar la dificultad seleccionada
        if(rbFacil.isChecked())
            nivel=1;
        if(rbMedio.isChecked())
            nivel=2;
        if(rbDificil.isChecked())
            nivel=3;

        //Incluir los datos a enviar al intent en el bundler
        datos.putInt(NIVEL,nivel);
        datos.putInt(NUM_TOPOS,sbNumTopos.getProgress()+10);
        datos.putBoolean(SONAR, swSonar.isChecked());
        datos.putBoolean(VIBRAR, swVibrar.isChecked());

        Intent i = new Intent(this, JuegoActivity.class);
        i.putExtras(datos); //Enviar el bundler por con el intent
        startActivityForResult(i, REQUEST_CODE); //Iniciar Activity pasando codigo
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO QUE SE EJECUTARÁ AL PULSAR EL BOTÓN DE INFORMACIÓN
     * @param v
     */
    public void info(View v){
        new AlertDialog.Builder(this)
                .setView(R.layout.dialogo_info) //Layout personalizado
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
     * METODO PARA OBTENER DATOS RESULTANTES DEL ACTIVITY DE JUEGO
     * @param reqCode
     * @param resCode
     * @param i
     */
    public void onActivityResult(int reqCode, int resCode, Intent i){
        if(reqCode==REQUEST_CODE && resCode==RESULT_OK) {
            ganado = i.getBooleanExtra(GANADO, true);

            //En funcion de si a ganado o perdido se mostrará un dialogo u otro
            if (ganado) {
                String modo="";
                if(rbFacil.isChecked())
                    modo=" fácil.";
                if(rbMedio.isChecked())
                    modo=" medio.";
                if(rbDificil.isChecked())
                    modo=" dificil.";

                dialogoGanar(i.getStringExtra(TIEMPO), i.getIntExtra(TOPOSATRAPADOS, 0), modo);
            } else {
                dialogoPerder();
            }
        }
        super.onActivityResult(reqCode, resCode, i);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA MOSTRAR EL DIALOGO CUANDO SE HA PERDIDO LA PARTIDA
     */
    public void dialogoPerder(){
        new AlertDialog.Builder(this)
                .setView(R.layout.dialogo_perder) //Layout personalizado
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
     * METODO PARA MOSTRAR EL DIALOGO CUANDO SE HA GANADO LA PARTIDA
     */
    public void dialogoGanar(String crono, int topos, String modo){
        AlertDialog dialogoGanado = new AlertDialog.Builder(this)
                .setView(R.layout.dialogo_ganado) //Layout personalizado
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .show();

        //Escribir datos de la partida
        tvCronoFin = (TextView) ((AlertDialog) dialogoGanado).findViewById(R.id.tvCronoFin);
        tvToposFin = (TextView) ((AlertDialog) dialogoGanado).findViewById(R.id.tvToposFin);
        tvGanado = (TextView) ((AlertDialog) dialogoGanado).findViewById(R.id.tvGanado);
        tvCronoFin.setText(crono);
        tvToposFin.setText("x "+topos);
        tvGanado.setText(tvGanado.getText()+modo);
    }
}