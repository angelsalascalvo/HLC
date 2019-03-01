package com.example.rutil.practicajuegofinalangelsalascalvo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RadioGroup;

/**
 * CLASE PARA CREAR EL DIALOGO CON EL ACTIVITY PERSONALIZADO DONDE SE INTRODUCIRÁ EL NIVEL Y NOMBRE
 */
public class DialogoAjustes extends DialogFragment implements DialogInterface.OnClickListener {
    //Variables
    protected SwitchCompat swSonar, swVibrar;
    protected RadioGroup rgBotones;
    onDialogoAjustes miListener;

    /**
     * CONSTRUCTOR NewInstance
     * @return
     */
    public static  DialogoAjustes newInstance(){
        return new DialogoAjustes();
    }

    //----------------------------------------------------------------------------------------------

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

        //Crear el cuadro de dialogo
        builder.setView(inflater.inflate(R.layout.dialogo_ajustes, null)) //establecer el layout personalizado
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.cancel, this)
                .setCancelable(false);

        return builder.create();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * INTERFAZ DEL DIALOGO
     */
    public interface onDialogoAjustes{
        public void onAceptarDialogo(int numRondas, boolean sonar, boolean vibrar);
    }

    //----------------------------------------------------------------------------------------------

    //metodo que garantiza que se podrán obtener los datos pasados a traves del metodo que hay que
    //implementar segun la interfaz
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
            miListener = (onDialogoAjustes) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement onDialogoNombreNivel");
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCRITURA DEL METODO ON CLICK QUE SE EJECUTARÁ AL PULSAR UN BOTON DEL CUADRO
     * @param dialog
     * @param which
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Referencias
        swSonar= (SwitchCompat) ((Dialog)dialog).findViewById(R.id.swSonido);
        swVibrar= (SwitchCompat) ((Dialog)dialog).findViewById(R.id.swVibrar);
        rgBotones = (RadioGroup)((Dialog)dialog).findViewById(R.id.grupoRondas);

        //Establecer el numero de rondas segun lo indicado
        int numRondas = 3;
        int nivel = rgBotones.getCheckedRadioButtonId();
        switch (nivel){
            case R.id.rb3R:
                numRondas=3;
                break;
            case R.id.rb5R:
                numRondas=5;
                break;
            case R.id.rb7R:
                numRondas=7;
                break;
        }

        //En función de la variable que identifica el boton pulsado actuaremos de un modo u otro
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                //Enviar datos a traves de la interfaz
                miListener.onAceptarDialogo(numRondas, swSonar.isChecked(), swVibrar.isChecked());
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }
}
