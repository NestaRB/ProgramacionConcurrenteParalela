// ============================================================================
class CuentaIncrementos {
// ============================================================================
    long contador = 0;

// --------------------------------------------------------------------------
    void incrementaContador() {
    contador++;
    }

// --------------------------------------------------------------------------
    long dameContador() {
    return( contador );
    }
}


// ============================================================================
class MiHebraIncremento extends Thread {
// ============================================================================
  // Declaracion de variables
    CuentaIncrementos cuenta;
    int miId;

// --------------------------------------------------------------------------
// Definicion del constructor, si es necesario
    MiHebraIncremento( int miId, CuentaIncrementos cuenta )
    {
        this.miId = miId;
        this.cuenta = cuenta;
    }

  // --------------------------------------------------------------------------
    public void run()
    {
        System.out.println( "Hebra: " + miId + " Comenzando incrementos" );
        // Bucle de 1000000 incrementos del objeto compartido
        for(int i = 0; i < 1000000; i++ )
        {
            cuenta.incrementaContador();
        }
        System.out.println( "Hebra: " + miId + " Terminando incrementos" );
    }
}

// ============================================================================
class EjemploIncrementos {
// ============================================================================

    // --------------------------------------------------------------------------
    public static void main( String args[] )
    {
        int  numHebras;

        // Comprobacion y extraccion de los argumentos de entrada.
        if( args.length != 1 )
        {
          System.err.println( "Uso: java programa <numHebras>" );
          System.exit( -1 );
        }
        try
        {
            numHebras = Integer.parseInt( args[ 0 ] );
            if( numHebras <= 0 )
            {
            System.err.println( "Uso: [ java programa <numHebras> ] donde numHebras > 0" );
            System.exit( -1 );
            }
        }
        catch( NumberFormatException ex )
        {
            numHebras = -1;
            System.out.println( "ERROR: Argumentos numericos incorrectos." );
            System.exit( -1 );
        }
        System.out.println( "numHebras: " + numHebras );

        // --------  INCLUIR NUEVO CODIGO A CONTINUACION --------------------------
        CuentaIncrementos cuenta = new CuentaIncrementos();

        System.out.println( "Valor inicial del contador: " + cuenta.dameContador());

        MiHebraIncremento[] vh = new MiHebraIncremento[numHebras];
        for(int i = 0; i < numHebras; i++)
        {
            vh[i] = new MiHebraIncremento( i, cuenta );
            vh[i].start();
        }

        for( int i = 0; i < numHebras; i++ )
        {
            try
            {
                vh[i].join();
            }
            catch(InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
        System.out.println( "Valor final del contador: " + cuenta.dameContador());
    }
}

