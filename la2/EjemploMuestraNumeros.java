// ============================================================================
class EjemploMuestraNumeros {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
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
    //
    // Implementacion paralela con distribucion ciclica o por bloques.
    //
    // Crea un vector de hebras. Crea y arranca las hebras
    // (A) ... 
    // Espera a que terminen todas las hebras.
    // (B) ... 
    //
  }
}

// Crea las clases adicionales que sean necesarias
// (C) ... 
// 
