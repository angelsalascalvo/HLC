package com.example.rutil.practicajuego1angelsalascalvo;

import android.content.Context;
import android.graphics.Color;

public class BotonHoyo extends android.support.v7.widget.AppCompatImageButton {

    /**
     * CONSTRUCTOR POR DEFECTO
     * @param context
     */
    public BotonHoyo(Context context){
        super(context);
        //Establecer el fondo por defecto
        setImageResource(R.drawable.hoyo);
        //Establecer color del fondo
        setBackgroundColor(Color.WHITE);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * METODO PARA REINICIAR LA IMAGEN DEL BOTON
     */
    public void reiniciar(){
        setImageResource(R.drawable.hoyo);
    }
}
