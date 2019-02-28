package com.example.rutil.practicajuegofinalangelsalascalvo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

public class ActivityJuego extends BaseActivity {

    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        //Modo pantalla completa
        setModoInmersivo();
        //Referencias
    }
}
