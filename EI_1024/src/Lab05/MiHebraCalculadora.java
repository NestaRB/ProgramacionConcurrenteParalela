package Lab05;

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
                // Bucle para coger todos los nuevos disparos dejados por la hebra grafica .
                while ((!listaD.isEmpty()) || (listaP.isEmpty())) {
                    //Tomar un nuevo disparo de listaD (d), bloqueandose si no hubiera.
                    NuevoDisparoUnaHebra d = listaD.take();
                    //Crear un nuevo proyectil (p) a partir del nuevo disparo (d).
                    ProyectilUnaHebra p = new ProyectilUnaHebra(d.velocidadInicial, d.anguloInicial, gui.cnvCampoTiro);
                    //Anyadir el nuevo proyectil (p) a listaP .
                    listaP.add(p);
                }
                // Procesado de la lista local de proyectiles .
                for (ProyectilUnaHebra p : listaP) {
                    // Sea p el proyectil actual de listaP .
                    //Muestra en pantalla los datos del proyectil p.
                    p.imprimeEstadoProyectilEnConsola();
                    //Mueve un incremental de tiempo el proyectil p.
                    p.mueveUnIncremental();
                    //Actualiza en pantalla la posicion del proyectil p.
                    p.actualizaDibujoDeProyectil();
                    //Comprueba si el proyectil p ha impactado en el suelo .
                    if (gui.determinaEstadoProyectil(p)) {
                        listaP.remove(p);
                    }
                }

            }
        } catch (InterruptedException e) {
            System.out.println("Error se ha interrumpido a la hebra auxiliar");
        }
    }
}
