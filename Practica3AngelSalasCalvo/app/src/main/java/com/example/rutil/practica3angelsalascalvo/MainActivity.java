package com.example.rutil.practica3angelsalascalvo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = (Button) findViewById(R.id.button);
        b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mostrar();
                return false;
            }
        });
    }

    public void mostrar(){
        Toast.makeText(this, "Hola", Toast.LENGTH_SHORT).show();
    }

    public void pas(View view){
        Intent i = new Intent(this, ActivityJuego.class);
        startActivity(i);
    }
}
