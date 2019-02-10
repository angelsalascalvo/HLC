package com.example.rutil.practicajuego2angelsalascalvo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DialogoRecord extends DialogFragment implements DialogInterface.OnClickListener {
    //Variables
    protected EditText etNombre;
    protected TextView tvPuntos;
    protected String nombre;
    private static int puntuacion;
    onDialogoRecord miListener;


    public static DialogoRecord newInstance(int p){
        puntuacion=p;
        return new DialogoRecord();
    }

    /**
     * BRESCRITURA DEL METODO OnCreateDialog
     * @param saveInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        //Declarar el cuadro de dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Obtener inflador del layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogo_record, null);

        //Crear el cuadro de dialogo
        builder.setView(view) //establecer el layout personalizado
                .setPositiveButton(android.R.string.yes, this)
                .setCancelable(false);

        tvPuntos=(TextView) view.findViewById(R.id.tvPuntosOb);
        tvPuntos.setText(puntuacion+" PUNTOS");

        return builder.create();
    }

    //----------------------------------------------------------------------------------------------

    //Añadir interfaz que obligará a implementar el método indicado**
    public interface onDialogoRecord{
        public void onAceptarDialogo(String nombre);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCITRURA DEL METODO ONATTACH
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity=null;
        if(context instanceof Activity) {
            activity =(Activity)context;
        }
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            miListener = (onDialogoRecord) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement onDialogoRecord");
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Referencias
        etNombre= (EditText)((Dialog)dialog).findViewById(R.id.etNombre);

        //Guardar valor del nombre
        nombre=etNombre.getText().toString();

        miListener.onAceptarDialogo(nombre);
    }

    //----------------------------------------------------------------------------------------------
}
