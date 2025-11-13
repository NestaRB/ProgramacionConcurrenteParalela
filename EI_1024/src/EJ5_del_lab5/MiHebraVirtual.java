/*package EJ5_del_lab5;

public class MiHebraVirtual implements Runnable{

    private NuevoDisparoUnaHebra d;
    private GUITiroAlBlancoUnaHebra gui;

    public MiHebraVirtual(NuevoDisparoUnaHebra d,
                          GUITiroAlBlancoUnaHebra gui) {
        this.d = d;
        this.gui = gui;
    }
    @Override
    public void run() {
        ProyectilUnaHebra p = new ProyectilUnaHebra(
                d.velocidadInicial, d.anguloInicial, gui.cnvCampoTiro);

        for (int i = 0; i < gui.numIters; i++) {
            p.imprimeEstadoProyectilEnConsola();
            p.mueveUnIncremental();
            p.actualizaDibujoDeProyectil();

            if (gui.determinaEstadoProyectil(p))
                break;
        }
    }

}
*/