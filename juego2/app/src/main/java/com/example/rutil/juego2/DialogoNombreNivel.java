package com.example.rutil.juego2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.zip.Inflater;

/**
 * CLASE PARA CREAR EL DIALOGO CON EL ACTIVITY PERSONALIZADO DONDE SE INTRODUCIRÁ EL NIVEL Y NOMBRE
 */
public class DialogoNombreNivel extends DialogFragment implements DialogInterface.OnClickListener {
    //Variables
    protected EditText etNombre;
    protected RadioGroup rgBotones;
    protected String nombre;
    onDialogoNombreNivel miListener;

    //Sustitucion del constructor por el newInstance que hace lo mismo que el constructor por defecto
    //pero con una serie de ventajas
    public static  DialogoNombreNivel newInstance(){
        return new DialogoNombreNivel();
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

        //Crear el cuadro de dialogo
        builder.setView(inflater.inflate(R.layout.dialogo_nombre_nivel, null)) //establecer el layout personalizado
                .setTitle(R.string.nueva_partida)
                .setPositiveButton(R.string.jugar, this)
                .setNegativeButton(R.string.volver, this)
                .setCancelable(false);

        return builder.create();
    }

    //----------------------------------------------------------------------------------------------

    //Añadir interfaz que obligará a implementar el método indicado**
    public interface onDialogoNombreNivel{
        public void onAceptarDialogo(String nombre, int velocidad);
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
            miListener = (onDialogoNombreNivel) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement onDialogoNombreNivel");
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //Referencias
        etNombre= (EditText)((Dialog)dialog).findViewById(R.id.tieNombre); //Se debe castear al dialogo para obtener el elemento que hay en el desde esta clase
        rgBotones = (RadioGroup)((Dialog)dialog).findViewById(R.id.rGroupNivel);
        //Guardar valor del nombre
        nombre=etNombre.getText().toString();

        //Establecer velocidad en funcion del RadioButton de nivel seleccionado
        int velocidad = 0; //Cuanto mas pequeño, menos pausa y por lo tanto mas rápido
        int nivel = rgBotones.getCheckedRadioButtonId();
        switch (nivel){
            case R.id.rbFacil:
                velocidad=80;
                break;
            case R.id.rbMedio:
                velocidad=60;
                break;
            case R.id.rbDificil:
                velocidad=40;
                break;
        }

        //En función de la variable que identifica el boton pulsado actuaremos de un modo u otro
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                //Asignamos al metodo de la interfaz que mas tarde se implementara, los parametros adecuados
                //Cuando damos en aceptar enviamos el nombre introducido y la velocidad en funcion del nivel
                //Esto hace que se ejecute el método que implementaremos obligatoriamente en la clase donde creamos el dialogo
                miListener.onAceptarDialogo(nombre, velocidad);

                break;
            case DialogInterface.BUTTON_NEGATIVE:
                //Si no, se finaliza el activity
                getActivity().finish();
                break;
        }
    }
}
