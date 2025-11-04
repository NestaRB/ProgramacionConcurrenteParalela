package Lab04;

import java.util.concurrent.atomic.DoubleAdder;

// ===========================================================================
class Acumula {
// ===========================================================================
  double  suma;

  // -------------------------------------------------------------------------
  Acumula()
  {
    this.suma = 0;
  }

  // -------------------------------------------------------------------------
  synchronized void acumulaDato( double dato ) {
    suma += dato;
  }

  // -------------------------------------------------------------------------
  synchronized double dameDato() {
    return suma;
  }
}

// ===========================================================================
class MiHebraMultAcumulaciones extends Thread
{
// ===========================================================================
  int      miId, numHebras;
  long     numRectangulos;
  Acumula  a;
  double baseRectangulo;

  // -------------------------------------------------------------------------
  MiHebraMultAcumulaciones( int miId, int numHebras, long numRectangulos, Acumula a, double baseRectangulo )
  {
      this.miId = miId;
      this.numHebras = numHebras;
      this.numRectangulos = numRectangulos;
      this.a = a;
      this.baseRectangulo =  baseRectangulo;
  }

  // -------------------------------------------------------------------------
  public void run()
  {
      for( long i = miId; i < numRectangulos; i+=numHebras )
      {
          a.acumulaDato( EjemploNumeroPI.f( baseRectangulo * ( ( ( double ) i ) + 0.5 ) ) * baseRectangulo );
      }
  }
}

// ===========================================================================
class MiHebraUnaAcumulacion extends Thread
{
    int      miId, numHebras;
    long     numRectangulos;
    Acumula  a;
    final double baseRectangulo;

    MiHebraUnaAcumulacion( int miId, int numHebras, long numRectangulos, Acumula a, double baseRectangulo )
    {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.a = a;
        this.baseRectangulo =  baseRectangulo;
    }

    // -------------------------------------------------------------------------
    public void run()
    {
        double sumaLocal = 0;
        for( long i = miId; i < numRectangulos; i+=numHebras )
        {
            sumaLocal += EjemploNumeroPI.f( baseRectangulo * ( ( ( double ) i ) + 0.5 ) ) * baseRectangulo;
        }
        a.acumulaDato( sumaLocal );
    }
}

// ===========================================================================
class MiHebraMultAcumulacionAtomica extends Thread
{
    int      miId, numHebras;
    long     numRectangulos;
    double baseRectangulo;
    DoubleAdder adder;

    MiHebraMultAcumulacionAtomica( int miId, int numHebras, long numRectangulos, DoubleAdder adder, double baseRectangulo )
    {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.baseRectangulo =  baseRectangulo;
        this.adder = adder;
    }

    // -------------------------------------------------------------------------
    public void run()
    {
        for( long i = miId; i < numRectangulos; i+=numHebras )
        {
            adder.add( EjemploNumeroPI.f( baseRectangulo * ( ( ( double ) i ) + 0.5 ) ) * baseRectangulo );
        }
    }

}

// ===========================================================================
class MiHebraUnaAcumulacionAtomica extends Thread
{
    int      miId, numHebras;
    long     numRectangulos;
    double baseRectangulo;
    DoubleAdder adder;

    MiHebraUnaAcumulacionAtomica( int miId, int numHebras, long numRectangulos, DoubleAdder adder, double baseRectangulo )
    {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.baseRectangulo =  baseRectangulo;
        this.adder = adder;
    }

    // -------------------------------------------------------------------------
    public void run()
    {
        double sumaLocal = 0;
        for( long i = miId; i < numRectangulos; i+=numHebras )
        {
            sumaLocal += EjemploNumeroPI.f( baseRectangulo * ( ( ( double ) i ) + 0.5 ) ) * baseRectangulo;
        }
        adder.add(sumaLocal);
    }
}



// ===========================================================================
class EjemploNumeroPI {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                        numRectangulos;
    double                      baseRectangulo, x, suma, pi;
    int                         numHebras;
    long                        t1, t2;
    double                      tSec, tPar;
    // Acumula                     a;
    // MiHebraMultAcumulaciones  vt[];

    // Comprobacion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <numRectangulos>" );
      System.exit( -1 );
    }
    try {
      numHebras      = Integer.parseInt( args[ 0 ] );
      numRectangulos = Long.parseLong( args[ 1 ] );
      if( ( numHebras <= 0 ) || ( numRectangulos <= 0 ) ) {
        System.err.print( "Uso: [ java programa <numHebras> <n> ] " );
        System.err.println( "donde ( numHebras > 0 ) y ( numRectangulos > 0 )" );
        System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras      = -1;
      numRectangulos = -1;
      System.out.println( "ERROR: Numeros de entrada incorrectos." );
      System.exit( -1 );
    }

    System.out.println();
    System.out.println( "Calculo del numero PI mediante integracion." );

    //
    // Calculo del numero PI de forma secuencial.
    //
    System.out.println();
    System.out.println( "Inicio del calculo secuencial." );
    t1 = System.nanoTime();
    baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    suma           = 0.0;
    for( long i = 0; i < numRectangulos; i++ ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      suma += f( x );
    }
    pi = baseRectangulo * suma;
    t2 = System.nanoTime();
    tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Version secuencial. Numero PI: " + pi );
    System.out.println( "Tiempo secuencial (s.):        " + tSec );

    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra.
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra." );
    t1 = System.nanoTime();

    Acumula a = new Acumula();
    MiHebraMultAcumulaciones[] vh1 = new MiHebraMultAcumulaciones[numHebras];
    for( int i = 0; i < numHebras; i++ )
    {
        vh1[i] = new MiHebraMultAcumulaciones(i, numHebras, numRectangulos, a, baseRectangulo );
        vh1[i].start();
    }
    for( int i = 0; i < numHebras; i++ )
    {
        try {
            vh1[i].join();
        }
        catch( InterruptedException ex ) {
            ex.printStackTrace();
        }
    }
    pi = a.dameDato();


    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    //
    // Calculo del numero PI de forma paralela: 
    // Una acumulacion por hebra.
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra." );
    t1 = System.nanoTime();

    a = new Acumula();
    MiHebraUnaAcumulacion[] vh2 = new MiHebraUnaAcumulacion[numHebras];
    for( int i = 0; i < numHebras; i++ )
    {
      vh2[i] = new MiHebraUnaAcumulacion(i, numHebras, numRectangulos, a, baseRectangulo);
      vh2[i].start();
    }
    for( int i = 0; i < numHebras; i++ )
    {
      try {
          vh2[i].join();
      }
      catch( InterruptedException ex ) {
          ex.printStackTrace();
      }
    }
    pi = a.dameDato();


    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra (Atomica)
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra (At)." );
    t1 = System.nanoTime();

    DoubleAdder adder = new DoubleAdder();
    MiHebraMultAcumulacionAtomica[] vh3 = new MiHebraMultAcumulacionAtomica[numHebras];
    for( int i = 0; i < numHebras; i++ )
    {
      vh3[i] = new MiHebraMultAcumulacionAtomica(i, numHebras, numRectangulos, adder, baseRectangulo);
      vh3[i].start();
    }
    for( int i = 0; i < numHebras; i++ )
    {
      try {
          vh3[i].join();
      }
      catch( InterruptedException ex ) {
          ex.printStackTrace();
      }
    }
    pi = adder.sum();


    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    //
    // Calculo del numero PI de forma paralela: 
    // Una acumulacion por hebra (Atomica).
    //
    System.out.println();
    System.out.print( "Inicio del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra (At)." );
    t1 = System.nanoTime();

    adder = new DoubleAdder();
      MiHebraUnaAcumulacionAtomica[] vh4 = new MiHebraUnaAcumulacionAtomica[numHebras];
    for( int i = 0; i < numHebras; i++ )
    {
      vh4[i] = new MiHebraUnaAcumulacionAtomica(i, numHebras, numRectangulos, adder, baseRectangulo);
      vh4[i].start();
    }
    for( int i = 0; i < numHebras; i++ )
    {
      try {
          vh4[i].join();
      }
      catch( InterruptedException ex ) {
          ex.printStackTrace();
      }
    }
    pi = adder.sum();


    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    System.out.println();
    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  static double f( double x ) {
    return ( 4.0/( 1.0 + x*x ) );
  }
}

