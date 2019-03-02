package com.example.rutil.practicajuegofinalangelsalascalvo;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BarraCohete extends android.support.v7.widget.AppCompatSeekBar {

    /**
     * CONSTRUCTOR PARAMETRIZADO
     * @param context
     */
    public BarraCohete(Context context) {
        super(context);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * CONSTRUCTOR PARAMETRIZADO
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BarraCohete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * CONSTRUCTOR PARAMETRIZADO
     * @param context
     * @param attrs
     */
    public BarraCohete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCRITURA DEL METODO onSizeChanged QUE PERMITIRÁ CAMBIAR EL TAMAÑO DE LA BARRA
     * UTILIZANDO COMO REFERENCIA LA ALTURA EN LUGAR DE LA ANCHURA
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCRITURA DEL METODO onDraw QUE PERMITIRÁ DIBUJAR LA BARRA SEEKBAR DE FORMA VERTICAL
     * @param c
     */
    @Override
    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCRITURA DEL METODO QUE PERMITIRÁ ESTABLECER EL VALOR DE LA SEEKBAR
     * @param progress
     */
    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * SOBRESCRITURA DEL METODO onTouchEvent PARA MOVER LA BARRA AL PULSAR SOBRE ELLA
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);

                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}