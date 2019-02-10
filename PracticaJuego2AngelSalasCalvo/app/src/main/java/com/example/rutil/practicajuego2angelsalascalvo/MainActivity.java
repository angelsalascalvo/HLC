package com.example.rutil.practicajuego2angelsalascalvo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements DialogoRecord.onDialogoRecord{

    public static int REQUEST_CODE=1234;
    public static String PUNTOS="puntuacionObt";
    public static String VIBRAR="vibrar";
    public static String SONAR="sonar";

    int puntuacion;
    TextView tvRecordNom, tvRecordNum, tvPuntosOb;
    SwitchCompat sVibrar, sSonar;
    MediaPlayer mpTiempo, mpRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciar();
        cargarRecord();
    }

    public void iniciar(){
        //Referencias
        tvRecordNom = (TextView) findViewById(R.id.tvRecordNom);
        tvRecordNum = (TextView) findViewById(R.id.tvRecordNum);
        sSonar = (SwitchCompat) findViewById(R.id.swSonar);
        sVibrar = (SwitchCompat) findViewById(R.id.swVibrar);

        if(sSonar.isChecked()){
            mpTiempo = MediaPlayer.create(this, R.raw.fin);
            mpRecord = MediaPlayer.create(this, R.raw.record);
        }


        //Establecer puntuacion por defecto
        puntuacion=0;
    }

    public void cargarRecord(){

        try {
            //Si no se encuentra el archivo salta la excepción
            openFileInput("record.txt");
        } catch (FileNotFoundException e) {
            //Proceso de creación del fichero
            try {
                OutputStreamWriter fs = new OutputStreamWriter(openFileOutput("record.txt", Context.MODE_PRIVATE));
                fs.write("0\n");
                fs.write(" ");
                fs.close();
            }catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        //Leer datos del fichero
        try
        {
            BufferedReader be = new BufferedReader(new InputStreamReader(openFileInput("record.txt")));
            tvRecordNum.setText(be.readLine()+"p");
            tvRecordNom.setText(be.readLine());
            be.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void jugar(View view){
        Intent i = new Intent(this, ActivityJuego.class);
        i.putExtra(VIBRAR, sVibrar.isChecked());
        i.putExtra(SONAR, sSonar.isChecked());
        startActivityForResult(i, REQUEST_CODE); //Iniciar Activity pasando codigo
    }

    public void info(View view){
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

    public void onActivityResult(int reqCode, int resCode, Intent i){
        super.onActivityResult(reqCode, resCode, i);
        if(reqCode==REQUEST_CODE && resCode==RESULT_OK) {
            puntuacion = i.getIntExtra(PUNTOS,0);
            int record=0;

            //Obtener el record almacenado en fichero
            try
            {
                BufferedReader be = new BufferedReader(new InputStreamReader(openFileInput("record.txt")));
                record= Integer.parseInt(be.readLine());
                be.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            //Comprobar si se ha obtenido una puntuación mayor que el record
            if(puntuacion>record){
                dialogoRecord();
                if(sSonar.isChecked()) mpRecord.start();
            }else{
                dialogoPuntos();
                //Sonar
                if(sSonar.isChecked()) mpTiempo.start();
            }
        }

    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA MOSTRAR EL DIALOGO QUE PIDE LA CONFIGURACIÓN DEL JUEGO (DIFICULTAD Y NOMBRE)
     */
    public void dialogoRecord(){
        //Creamos el dialogo
        DialogoRecord ventEmergente = DialogoRecord.newInstance(puntuacion);
        //Desactiar la opcion de cancelar
        ventEmergente.setCancelable(false);
        //Mostrar el dialogo que pide configuracion de la partida
        ventEmergente.show(getSupportFragmentManager(), "DialogoRecord"); //Tag??
    }

    /**
     * SOBRESCRITURA DEL METODO CON EL QUE PODREMOS OBTENER EL NOMBRE INTRODUCIDO
     * @param nombre
     */
    @Override
    public void onAceptarDialogo(String nombre) {
        //Con el nombre introducido almacenamos el nuevo record
        try {
            OutputStreamWriter fs = new OutputStreamWriter(openFileOutput("record.txt", Context.MODE_PRIVATE));
            fs.write(puntuacion+"\n");
            fs.write(nombre);
            fs.close();
        }catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //Establecemos nuevos valores en las etiquetas
        tvRecordNum.setText(puntuacion+"p");
        tvRecordNom.setText(nombre);
    }

    /**
     * METODO PARA MOSTRAR EL DIALOGO CUANDO SE HA PERDIDO LA PARTIDA
     */
    public void dialogoPuntos(){
        AlertDialog dialogoGanado = new AlertDialog.Builder(this)
                .setView(R.layout.dialogo_tiempo) //Layout personalizado
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .show();

        //Escribir datos de la partida
        tvPuntosOb = (TextView) ((AlertDialog) dialogoGanado).findViewById(R.id.tvPuntosOb);
        tvPuntosOb.setText(puntuacion+" PUNTOS");
    }
}
