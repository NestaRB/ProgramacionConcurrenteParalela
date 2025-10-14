package Lab02;

// ============================================================================
class EjemploMuestraNumeros {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main(String[] args) {
    int  n, numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <n>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      n         = Integer.parseInt( args[ 1 ] );
      if( ( numHebras <= 0 ) || ( n <= 0 ) ) {
        System.err.print( "Uso: [ java programa <numHebras> <n> ] " );
        System.err.println( "donde ( numHebras > 0 )  y ( n > 0 )" );
        System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      n         = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

        //Ciclica
      MiHebra1_1[] vh = new MiHebra1_1[numHebras];
      for(int i = 0; i < numHebras; i++)
      {
          vh[i] = new MiHebra1_1( numHebras, n, i );
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

      /*
        //Por bloques
      Lab02.MiHebra1_2[] vh = new MiHebra1_2[numHebras];
      int tam = (n + numHebras - 1) / numHebras;
      for(int i = 0; i < numHebras; i++)
      {

          int inicio = tam * i;
          int fin = Math.min( inicio +tam, n);
          vh[i] = new Lab02.MiHebra( inicio, fin, i );
          vh[i].start();
      }
       */
  }
}


  //Ciclica
class MiHebra1_1 extends Thread
 {
    int numHebras, miId, n;

    public MiHebra1_1(int numHebras, int n , int miId )
    {
        this.numHebras = numHebras;
        this.miId = miId;
        this.n = n;
    }

    public void run()
    {
        for( int i = miId; i < n; i+=numHebras )
        {
            System.out.println( "Hebra " + miId + ": " + i);
        }
    }



}

  //Bloque
class MiHebra1_2 extends Thread
{
    int inicio, fin, miId;
    public MiHebra1_2(int inicio, int fin, int miId)
    {
        this.inicio = inicio;
        this.fin = fin;
        this.miId = miId;
    }
    public void run()
    {
        for( int i = inicio; i < fin ; i++ )
        {
            System.out.println( "Lab02.MiHebra " + miId + ": " + i );
        }
    }

}
