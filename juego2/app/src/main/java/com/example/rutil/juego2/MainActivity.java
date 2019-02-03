package com.example.rutil.juego2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

//Extendemos del BaseActivity que tiene toodo lo de CompatActivity porque lo hereda mas la funcionalidad o método
//de poner en pantalla completa que hemos creado anteriormente en la clase BaseActivity.
public class MainActivity extends BaseActivity implements View.OnClickListener{

    private ImageView ivIns, ivJugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Referencias
        ivIns = (ImageView) findViewById(R.id.ivInstrucciones);
        ivJugar = (ImageView) findViewById(R.id.ivJugar);

        //Metodo creado en "BaseActivity" para poner pantalla completa
        setModoInmersivo();

        //Añadir los listener a las imagenes ya que no son botones
        ivIns.setOnClickListener(this);
        ivJugar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Comprobar que boton se ha pulsado
        if(v.getId()==ivIns.getId()){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.como_jugar)
                    .setMessage(R.string.explicacion)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Al abrir el dialogo se pierde la pantalla completa por lo que
                            //tenemos que llamar de nuevo al modo inmersivo
                            setModoInmersivo();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

        if(v.getId()==ivJugar.getId()){
            //Cargar Activity de juego con un Intent
            Intent i = new Intent(this, JuegoActivity.class);
            startActivity(i);
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onResume(){
        super.onResume();
        setModoInmersivo();
    }
}
