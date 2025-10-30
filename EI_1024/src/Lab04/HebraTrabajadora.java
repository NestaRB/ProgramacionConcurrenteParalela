package Lab04;

import javax.swing.*;

import static Lab04.GUISecuenciaPrimos.esPrimo;

public class HebraTrabajadora extends Thread {
    private boolean fin = false;
    private JTextField txfMensaje;

    private ZonaIntercambio z;

    public HebraTrabajadora(JTextField txfMensajes, ZonaIntercambio z) {
        this.txfMensaje = txfMensajes;
        this.z = z;
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
                try {
                    sleep(z.getTiempo());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            i++;
        }
    }
}
