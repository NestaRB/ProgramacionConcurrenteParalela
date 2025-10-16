package Lab02;

// ============================================================================
class EjemploFuncionCostosa {
// ============================================================================

    // --------------------------------------------------------------------------
    public static void main(String args[]) {
        int n, numHebras;
        long t1, t2, t3, t4;
        double sumaX, sumaY, ts, tc, tb;

        // Comprobacion y extraccion de los argumentos de entrada.
        if (args.length != 2) {
            System.err.println("Uso: java programa <numHebras> <tamanyo>");
            System.exit(-1);
        }
        try {
            numHebras = Integer.parseInt(args[0]);
            n = Integer.parseInt(args[1]);
            if ((numHebras <= 0) || (n <= 0)) {
                System.err.print("Uso: [ java programa <numHebras> <n> ] ");
                System.err.println("donde ( numHebras > 0 )  y ( n > 0 )");
                System.exit(-1);
            }
        } catch (NumberFormatException ex) {
            numHebras = -1;
            n = -1;
            System.out.println("ERROR: Argumentos numericos incorrectos.");
            System.exit(-1);
        }

        // Crea los vectores.
        double vectorX[] = new double[n];
        double vectorY[] = new double[n];

        //
        // Implementacion secuencial (sin temporizar).
        //
        inicializaVectorX(vectorX);
        inicializaVectorY(vectorY);
        for (int i = 0; i < n; i++) {
            vectorY[i] = evaluaFuncion(vectorX[i]);
        }

        //
        // Implementacion secuencial.
        //
        inicializaVectorX(vectorX);
        inicializaVectorY(vectorY);
        t1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            vectorY[i] = evaluaFuncion(vectorX[i]);
        }
        t2 = System.nanoTime();
        ts = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Tiempo secuencial (seg.):                    " + ts);
        imprimeResultado( vectorX, vectorY );
        //imprimeVector(vectorX);
        //imprimeVector(vectorY);
        // Comprueba el resultado.
        sumaX = sumaVector(vectorX);
        sumaY = sumaVector(vectorY);
        System.out.println("Suma del vector X:          " + sumaX);
        System.out.println("Suma del vector Y:          " + sumaY);

        //
        // Implementacion paralela ciclica.
        //
        inicializaVectorX(vectorX);
        inicializaVectorY(vectorY);
        t1 = System.nanoTime();
        // Gestion de hebras para la implementacion paralela ciclica
        // (A)

        MiHebra2_1[] vh = new MiHebra2_1[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vh[i] = new MiHebra2_1(i, numHebras, vectorX, vectorY);
            vh[i].start();
        }

        for (int i = 0; i < numHebras; i++){
            try {
                vh[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        t2 = System.nanoTime();
        tc = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Tiempo paralela ciclica (seg.):              " + tc);
        System.out.println("Incremento paralela ciclica:                 " + ts/tc ); // (B)
        imprimeResultado( vectorX, vectorY );
        //imprimeVector(vectorX);
        //imprimeVector(vectorY);
        // Comprueba el resultado.
        sumaX = sumaVector(vectorX);
        sumaY = sumaVector(vectorY);
        System.out.println("Suma del vector X:          " + sumaX);
        System.out.println("Suma del vector Y:          " + sumaY);
        //
        // Implementacion paralela por bloques.
        //
        // (C) ....
        //
        t3 = System.nanoTime();

        MiHebra2_2[] vh2 = new MiHebra2_2[numHebras];
        int tam = (n + numHebras - 1) / numHebras;
        for (int i = 0; i < numHebras; i++) {
            int inicio = tam * i;
            int fin = Math.min( inicio +tam, n);
            vh2[i] = new MiHebra2_2(i, inicio, fin, vectorX, vectorY);
            vh2[i].start();
        }

        for (int i = 0; i < numHebras; i++){
            try {
                vh[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        t4 =  System.nanoTime();
        tb = ((double) (t4 - t3)) / 1.0e9;
        System.out.println("Tiempo paralela bloques (seg.):              " + tb);
        System.out.println("Incremento paralela bloques:                 " + ts/tb ); // (B)
        imprimeResultado( vectorX, vectorY );


        System.out.println("Fin del programa.");
    }

    // --------------------------------------------------------------------------
    static void inicializaVectorX(double vectorX[]) {
        if (vectorX.length == 1) {
            vectorX[0] = 0.0;
        } else {
            for (int i = 0; i < vectorX.length; i++) {
                vectorX[i] = 10.0 * (double) i / ((double) vectorX.length - 1);
            }
        }
    }

    // --------------------------------------------------------------------------
    static void inicializaVectorY(double vectorY[]) {
        for (int i = 0; i < vectorY.length; i++) {
            vectorY[i] = 0.0;
        }
    }

    // --------------------------------------------------------------------------
    static double sumaVector(double vector[]) {
        double suma = 0.0;
        for (int i = 0; i < vector.length; i++) {
            suma += vector[i];
        }
        return suma;
    }

    // --------------------------------------------------------------------------
    static double evaluaFuncion(double x) {
        return -Math.cos(Math.exp(-x) + Math.log1p(x));
    }

    // --------------------------------------------------------------------------
    static void imprimeVector(double vector[]) {
        for (int i = 0; i < vector.length; i++) {
            System.out.println(" vector[ " + i + " ] = " + vector[i]);
        }
    }

    // --------------------------------------------------------------------------
    static void imprimeResultado(double vectorX[], double vectorY[]) {
        for (int i = 0; i < Math.min(vectorX.length, vectorY.length); i++) {
            System.out.println("  i: " + i +
                    "  x: " + vectorX[i] +
                    "  y: " + vectorY[i]);
        }
    }

}

// Crea las clases adicionales que sean necesarias
class MiHebra2_1 extends Thread {
    int miId, n_hebras;
    double[] vectorX, vectorY;

    MiHebra2_1(int miId, int n_hebras, double[] vectorX, double[] vectorY) {
        this.miId = miId;
        this.n_hebras = n_hebras;
        this.vectorX = vectorX;
        this.vectorY = vectorY;
    }

    public void run() {
        for (int i = miId; i < vectorX.length; i += n_hebras) {
            vectorY[i] = EjemploFuncionCostosa.evaluaFuncion(vectorX[i]);
        }
    }

}

class MiHebra2_2 extends Thread
{
    int inicio, fin, miId;
    double[] vectorX, vectorY;
    public MiHebra2_2(int miId, int inicio, int fin, double[] vectorX, double[] vectorY)
    {
        this.inicio = inicio;
        this.fin = fin;
        this.miId = miId;
        this.vectorX = vectorX;
        this.vectorY = vectorY;

    }
    public void run()
    {
        for (int i = inicio; i < fin ; i++ ) {
            vectorY[i] = EjemploFuncionCostosa.evaluaFuncion(vectorX[i]);
        }
    }
}

