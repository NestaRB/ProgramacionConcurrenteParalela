package Lab04;

import javax.swing.*;

import static Lab04.GUISecuenciaPrimos.esPrimo;

public class HebraTrabajadora extends Thread {
    private boolean fin = false;
    private JTextField txfMensaje;

    public HebraTrabajadora(JTextField txfMensajes) {
        this.txfMensaje = txfMensajes;
    }
    public void parar() {
        fin = true;
    }

    public void run(){
        long i = 1L;
        while (! fin){
            if ( esPrimo(i) ){

                final long finalI = i;
                SwingUtilities.invokeLater( () ->
                        txfMensaje.setText(Long.valueOf(finalI).toString()));
                //espera (ejer 2.3)
            }

            i++;
        }
    }
}
