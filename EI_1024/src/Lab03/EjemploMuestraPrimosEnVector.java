package Lab03;

import java.util.concurrent.atomic.AtomicInteger;

// ===========================================================================
public class EjemploMuestraPrimosEnVector {
// ===========================================================================
  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    int     numHebras, vectOpt;
    boolean option = true;
    long    t1, t2, t3, t4, t5, t6;
    double  ts, tc, tb, td;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <vectOpt>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      vectOpt   = Integer.parseInt( args[ 1 ] );
      if( ( numHebras <= 0 ) || ( ( vectOpt != 0 ) && ( vectOpt != 1 ) ) ){
          System.err.print( "Uso: [ java programa <numHebras> <vecOpt> ] " );
          System.err.println( "donde ( numHebras > 0 )  y ( vectOpt es 0 o 1 )" );
          System.exit( -1 );
      } else {
        option = (vectOpt == 0);
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    //
    // Eleccion del vector de trabajo
    //
    VectorNumeros vn = new VectorNumeros (option);
    long vectorTrabajo[] = vn.vector;

    //
    // Implementacion secuencial.
    //
    System.out.println( "" );
    System.out.println( "Implementacion secuencial." );
    t1 = System.nanoTime();
    for( int i = 0; i < vectorTrabajo.length; i++ ) {
      if( esPrimo( vectorTrabajo[ i ] ) ) {
        System.out.println( "  Encontrado primo: " + vectorTrabajo[ i ] );
      }
    }

    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo secuencial (seg.):                    " + ts );

    //
    // Implementacion paralela ciclica.
    //
    System.out.println( "" );
    System.out.println( "Implementacion paralela ciclica." );
    t1 = System.nanoTime();

    // Gestion de hebras para la implementacion paralela ciclica
      MiHebraPrimoDistCiclica[] vh = new MiHebraPrimoDistCiclica[numHebras];
      for(int i = 0; i < numHebras; i++)
      {
          vh[i] = new MiHebraPrimoDistCiclica(i, numHebras, vectorTrabajo );
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
    t2 = System.nanoTime();
    tc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela ciclica (seg.):              " + tc );
    System.out.println( "Incremento paralela ciclica:                 " + tc/ts ); // (B)

    //
    // Implementacion paralela por bloques.
    //
      System.out.println( "" );
      System.out.println( "Implementacion paralela bloques." );
      t3 = System.nanoTime();
      // Gestion de hebras para la implementacion paralela bloques
      MiHebraPrimoDistPorBloques[] vh2 = new MiHebraPrimoDistPorBloques[numHebras];
      int tam = (vectorTrabajo.length + numHebras - 1) / numHebras;
      for(int i = 0; i < numHebras; i++)
      {
          int inicio = tam * i;
          int fin = Math.min( inicio +tam, vectorTrabajo.length );
          vh2[i] = new MiHebraPrimoDistPorBloques( inicio, fin, vectorTrabajo );
          vh2[i].start();
      }
      for( int i = 0; i < numHebras; i++ )
      {
          try
          {
              vh2[i].join();
          }
          catch(InterruptedException ex)
          {
              ex.printStackTrace();
          }
      }
      t4 = System.nanoTime();
      tb = ( ( double ) ( t4 - t3 ) ) / 1.0e9;
      System.out.println( "Tiempo paralela bloque (seg.):              " + tb );
      System.out.println( "Incremento paralela bloque:                 " + tb/ts ); // (B)
    //
    // Implementacion paralela dinamica.
    //
      System.out.println( "" );
      System.out.println( "Implementacion paralela dinamica." );
      t5 = System.nanoTime();
      // Gestion de hebras para la implementacion paralela dinamica
      AtomicInteger index = new AtomicInteger(0);
      MiHebraPrimoDistDinamica[] vh3 = new MiHebraPrimoDistDinamica[numHebras];
      for(int i = 0; i < numHebras; i++)
      {
          vh3[i] = new MiHebraPrimoDistDinamica( index, vectorTrabajo );
          vh3[i].start();
      }
      for( int i = 0; i < numHebras; i++ )
      {
          try
          {
              vh3[i].join();
          }
          catch(InterruptedException ex)
          {
              ex.printStackTrace();
          }
      }
      t6 = System.nanoTime();
      td = ( ( double ) ( t6 - t5 ) ) / 1.0e9;
      System.out.println( "Tiempo paralela dinamica (seg.):              " + td );
      System.out.println( "Incremento paralela dinamica:                 " + td/ts ); // (B)

  }

  // -------------------------------------------------------------------------
  static boolean esPrimo( long num ) {
    boolean cond;
    if( num < 2 ) {
      cond = false;
    } else {
      cond = true;
      long i = 2;
      while( ( i < num )&&( cond ) ) { 
        cond = ( num % i != 0 );
        i++;
      }
    }
    return( cond );
  }
}

// Definicion de las Clases Hebras
//
// (E)
class MiHebraPrimoDistCiclica extends Thread
{
    int miId, numHebras;
    long[] vector;
    MiHebraPrimoDistCiclica(int miId, int numHebras,  long[] vector)
    {
        this.miId = miId;
        this.numHebras = numHebras;
        this.vector = vector;
    }
    @Override
    public void run() {
        for(int i = miId; i < vector.length; i+= numHebras )
        {
            if( EjemploMuestraPrimosEnVector.esPrimo( vector[ i ] ) )
            {
                System.out.println( "  Encontrado primo: " + vector[ i ] );
            }
        }
    }
}

class MiHebraPrimoDistPorBloques extends Thread
{
    int inicio, fin;
    long[] vector;
    MiHebraPrimoDistPorBloques(int inicio, int fin, long[] vector)
    {
        this.inicio = inicio;
        this.fin = fin;
        this.vector = vector;
    }
    @Override
    public void run()
    {
        for( int i = inicio; i < fin ; i++ )
        {
            if( EjemploMuestraPrimosEnVector.esPrimo( vector[ i ] ) )
            {
                System.out.println( "  Encontrado primo: " + vector[ i ] );
            }
        }
    }
}

class MiHebraPrimoDistDinamica extends Thread
{
    AtomicInteger index;
    long[] vector;
    MiHebraPrimoDistDinamica(AtomicInteger index, long[] vector)
    {
        this.index = index;
        this.vector = vector;
    }

    @Override
    public void run() {
        while( index.get() < vector.length )
        {
            int i = index.getAndIncrement();
            if( EjemploMuestraPrimosEnVector.esPrimo( vector[ i ] ) )
            {
                System.out.println( "  Encontrado primo: " + vector[ i ] );
            }
        }
    }
}
// ===========================================================================
class VectorNumeros {
// ===========================================================================
  long    vector[];
  // -------------------------------------------------------------------------
  public VectorNumeros (boolean caso) {
    if (caso) {
      vector = new long [] {
      200000081L, 200000083L, 200000089L, 200000093L,
      200000107L, 200000117L, 200000123L, 200000131L,
      200000161L, 200000183L, 200000201L, 200000209L,
      200000221L, 200000237L, 200000239L, 200000243L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
      };
    } else {
      vector = new long [] {
      200000081L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000083L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000089L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000093L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000107L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000117L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000123L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000131L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000161L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000183L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000201L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000209L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 
      200000221L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000237L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000239L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000243L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
      };
    }
  }
}

