/*package EJ5_del_lab5;

import Lab01.MiHebraDaemonVirtual;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MiHebraCalculadora extends Thread {

    private LinkedBlockingQueue<NuevoDisparoUnaHebra> listaD;
    private GUITiroAlBlancoUnaHebra gui;
    private ArrayList<ProyectilUnaHebra> listaP;

    public MiHebraCalculadora(LinkedBlockingQueue<NuevoDisparoUnaHebra> listaD, GUITiroAlBlancoUnaHebra gui) throws InterruptedException {
        this.listaD = listaD;
        this.gui = gui;
        this.listaP = new ArrayList<ProyectilUnaHebra>();
    }
    // Bucle infinito en el cuerpo de la hebra.

    public void run() {
        try {
            while (true) {
                MiHebraVirtual[] vh = new MiHebraVirtual[listaP.size()];
                // Bucle para coger todos los nuevos disparos dejados por la hebra grafica .
                while ((!listaD.isEmpty()) || (listaP.isEmpty())) {
                    //Tomar un nuevo disparo de listaD (d), bloqueandose si no hubiera.
                    NuevoDisparoUnaHebra d = listaD.take();
                    //Crear un nuevo proyectil (p) a partir del nuevo disparo (d).
                    ProyectilUnaHebra p = new ProyectilUnaHebra(d.velocidadInicial, d.anguloInicial, gui.cnvCampoTiro);
                    //Anyadir el nuevo proyectil (p) a listaP .
                    listaP.add(p);
                }

                for(int i = listaP.size() - 1; i >= 0; i--)
                {
                    vh[i] = Thread.startVirtualThread(new MiHebraVirtual(listaD, gui));

                    if(gui.determinaEstadoProyectil(p))
                    {
                        listaP.remove(i);
                    }
                    gui.duermeUnPoco(2l);
                }

            }
        } catch (InterruptedException e) {
            System.out.println("Error se ha interrumpido a la hebra auxiliar");
        }
    }
}
*/