package Lab06;

import java.io.*;
import java.util.*;

 import java.util.concurrent.*;
 import java.util.concurrent.atomic.*;
 import java.util.Map;
 import java.util.stream.*;
 import java.util.function.*;
 import static java.util.stream.Collectors.*;
 import java.util.Comparator.*;

// ============================================================================
class EjemploPalabraMasUsada {
// ============================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                     t1, t2;
    double                   ts, tp;
    int                      numHebras;
    String                   nombreFichero, palabraActual;
    Vector<String>           vectorLineas;
    HashMap<String,Integer>  hmCuentaPalabras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <fichero>" );
      System.exit( -1 );
    }
    try {
      numHebras     = Integer.parseInt( args[ 0 ] );
      nombreFichero = args[ 1 ];
      if( numHebras <= 0 )  {
          System.err.print( "Uso: [ java programa <numHebras> <fichero> ] " );
          System.err.println( "donde ( numHebras > 0 )" );
          System.exit( -1 );
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      nombreFichero = "";
      System.out.println( "ERROR: Argumento numerico incorrectos." );
      System.exit( -1 );
    }

    // Lectura y carga de lineas en "vectorLineas".
    vectorLineas = leeFichero( nombreFichero );
    System.out.println( "Numero de lineas leidas: " + vectorLineas.size() );
    System.out.println();

    //
    // Implementacion secuencial sin temporizar.
    //
    hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
    for( int i = 0; i < vectorLineas.size(); i++ ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          contabilizaPalabra( hmCuentaPalabras, palabraActual );
        }
      }
    }

    //
    // Implementacion secuencial.
    //
    t1 = System.nanoTime();
    hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
    for( int i = 0; i < vectorLineas.size(); i++ ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          contabilizaPalabra( hmCuentaPalabras, palabraActual );
        }
      }
    }
    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion secuencial: " );
    imprimePalabraMasUsadaYVeces( hmCuentaPalabras );
    System.out.println( " Tiempo(s): " + ts );
    System.out.println( "Num. elems. tabla hash: " + hmCuentaPalabras.size() );
    System.out.println();


    //
    // Implementacion paralela 1: Uso de synchronizedMap y cerrojo
    //
    t1 = System.nanoTime();

    HashMap<String, Integer> hm = new HashMap<>(1000, 0.75F);
    Map<String, Integer> maCuentaPalabras = Collections.synchronizedMap(hm);

    MiHebra_1[] vh = new MiHebra_1[numHebras];
    int tam = vectorLineas.size() / numHebras;
    for(int i = 0; i < numHebras; i++){
      int inicio = i * tam;
      int fin = (i == numHebras - 1) ? vectorLineas.size() : (i + 1) * tam;

      vh[i] = new MiHebra_1(vectorLineas, maCuentaPalabras, inicio, fin);
      vh[i].start();
    }

    for( int i = 0; i < numHebras; i++){
      try{
        vh[i].join();
      }catch (InterruptedException ex){
        ex.printStackTrace();
      }
    }


    t2 = System.nanoTime();
    tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela 1: " );
    imprimePalabraMasUsadaYVeces( maCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts/tp);
    System.out.println( "Num. elems. tabla hash: " + maCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 2: Uso de Hashtable y cerrojo
    //
    t1 = System.nanoTime();

    // 1. Inicialización: Se usa la clase Hashtable directamente
    Hashtable<String, Integer> htCuentaPalabras = new Hashtable<>(1000, 0.75F);

    // 2. Creación y ejecución de las hebras
    MiHebra_2[] vh2 = new MiHebra_2[numHebras];
    tam = vectorLineas.size() / numHebras;

    for(int i = 0; i < numHebras; i++){
        int inicio = i * tam;
        int fin = (i == numHebras - 1) ? vectorLineas.size() : (i + 1) * tam;

        vh2[i] = new MiHebra_2(vectorLineas, htCuentaPalabras, inicio, fin);
        vh2[i].start();
    }

    // 3. Esperar a que todas las hebras terminen
    for( int i = 0; i < numHebras; i++){
        try{
            vh2[i].join();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    t2 = System.nanoTime();
    tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;

    System.out.print( "Implementacion paralela 2: " );
    imprimePalabraMasUsadaYVeces( htCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts/tp);
    System.out.println( "Num. elems. tabla hash: " + htCuentaPalabras.size() );
    System.out.println();


    //
    // Implementacion paralela 3: Uso de ConcurrentHashMap y cerrojo
    // ...
    t1 = System.nanoTime();

    // 1. Inicialización: Se usa la clase Hashtable directamente
    ConcurrentHashMap<String, Integer> chmCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);

    // 2. Creación y ejecución de las hebras
    MiHebra_3[] vh3 = new MiHebra_3[numHebras];
    tam = vectorLineas.size() / numHebras;

    for(int i = 0; i < numHebras; i++){
        int inicio = i * tam;
        int fin = (i == numHebras - 1) ? vectorLineas.size() : (i + 1) * tam;

        vh3[i] = new MiHebra_3(vectorLineas, chmCuentaPalabras, inicio, fin);
        vh3[i].start();
    }

    // 3. Esperar a que todas las hebras terminen
    for( int i = 0; i < numHebras; i++){
        try{
            vh3[i].join();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    t2 = System.nanoTime();
    tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;

    System.out.print( "Implementacion paralela 3: " );
    imprimePalabraMasUsadaYVeces( chmCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts/tp);
    System.out.println( "Num. elems. tabla hash: " + chmCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 4: Uso de ConcurrentHashMap con merge
    // ...
      t1 = System.nanoTime();

      // 1. Inicialización: Se usa la clase Hashtable directamente
      ConcurrentHashMap<String, Integer> chmMergeCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);

      // 2. Creación y ejecución de las hebras
      MiHebra_4[] vh4 = new MiHebra_4[numHebras];
      tam = vectorLineas.size() / numHebras;

      for(int i = 0; i < numHebras; i++){
          int inicio = i * tam;
          int fin = (i == numHebras - 1) ? vectorLineas.size() : (i + 1) * tam;

          vh4[i] = new MiHebra_4(vectorLineas, chmMergeCuentaPalabras, inicio, fin);
          vh4[i].start();
      }

      // 3. Esperar a que todas las hebras terminen
      for( int i = 0; i < numHebras; i++){
          try{
              vh4[i].join();
          } catch (InterruptedException ex){
              ex.printStackTrace();
          }
      }

      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;

      System.out.print( "Implementacion paralela 4: " );
      imprimePalabraMasUsadaYVeces( chmMergeCuentaPalabras );
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts/tp);
      System.out.println( "Num. elems. tabla hash: " + chmMergeCuentaPalabras.size() );
      System.out.println();
    //
    // Implementacion paralela 5: Uso de ConcurrentHashMap escalable
    // ...
      t1 = System.nanoTime();

      // 1. Inicialización: Se usa la clase Hashtable directamente
      ConcurrentHashMap<String, Integer> chmputIfAbsentCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);

      // 2. Creación y ejecución de las hebras
      MiHebra_5[] vh5 = new MiHebra_5[numHebras];
      tam = vectorLineas.size() / numHebras;

      for(int i = 0; i < numHebras; i++){
          int inicio = i * tam;
          int fin = (i == numHebras - 1) ? vectorLineas.size() : (i + 1) * tam;

          vh5[i] = new MiHebra_5(vectorLineas, chmputIfAbsentCuentaPalabras, inicio, fin);
          vh5[i].start();
      }

      // 3. Esperar a que todas las hebras terminen
      for( int i = 0; i < numHebras; i++){
          try{
              vh5[i].join();
          } catch (InterruptedException ex){
              ex.printStackTrace();
          }
      }

      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;

      System.out.print( "Implementacion paralela 5: " );
      imprimePalabraMasUsadaYVeces( chmputIfAbsentCuentaPalabras );
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts/tp);
      System.out.println( "Num. elems. tabla hash: " + chmputIfAbsentCuentaPalabras.size() );
      System.out.println();
    //
    // Implementacion paralela 6: Uso de CHM escalable con AtomicInteger
    // ...
      t1 = System.nanoTime();

      // 1. Inicialización: Se usa la clase Hashtable directamente
      ConcurrentHashMap<String, AtomicInteger> chmAtomicCuentaPalabras = new ConcurrentHashMap<>(1000, 0.75F);

      // 2. Creación y ejecución de las hebras
      MiHebra_6[] vh6 = new MiHebra_6[numHebras];
      tam = vectorLineas.size() / numHebras;

      for(int i = 0; i < numHebras; i++){
          int inicio = i * tam;
          int fin = (i == numHebras - 1) ? vectorLineas.size() : (i + 1) * tam;

          vh6[i] = new MiHebra_6(vectorLineas, chmAtomicCuentaPalabras, inicio, fin);
          vh6[i].start();
      }

      // 3. Esperar a que todas las hebras terminen
      for( int i = 0; i < numHebras; i++){
          try{
              vh6[i].join();
          } catch (InterruptedException ex){
              ex.printStackTrace();
          }
      }

      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;



      System.out.print( "Implementacion paralela 6: " );
      imprimePalabraMasUsadaYVecesAtomic( chmAtomicCuentaPalabras );
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts/tp);
      System.out.println( "Num. elems. tabla hash: " + chmAtomicCuentaPalabras.size() );
      System.out.println();
    //
    // Implementacion paralela 7: Uso de CHM escalable con AtomicInteger y 256 niv.
    // ...
      t1 = System.nanoTime();

      // 1. Inicialización: Se usa la clase Hashtable directamente
      ConcurrentHashMap<String, AtomicInteger> chmAtomicCuentaPalabras256 = new ConcurrentHashMap<>(1000, 0.75F, 256);

      // 2. Creación y ejecución de las hebras
      MiHebra_6[] vh7 = new MiHebra_6[numHebras];
      tam = vectorLineas.size() / numHebras;

      for(int i = 0; i < numHebras; i++){
          int inicio = i * tam;
          int fin = (i == numHebras - 1) ? vectorLineas.size() : (i + 1) * tam;

          vh7[i] = new MiHebra_6(vectorLineas, chmAtomicCuentaPalabras256, inicio, fin);
          vh7[i].start();
      }

      // 3. Esperar a que todas las hebras terminen
      for( int i = 0; i < numHebras; i++){
          try{
              vh7[i].join();
          } catch (InterruptedException ex){
              ex.printStackTrace();
          }
      }

      t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;



      System.out.print( "Implementacion paralela 7: " );
      imprimePalabraMasUsadaYVecesAtomic( chmAtomicCuentaPalabras256 );
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts/tp);
      System.out.println( "Num. elems. tabla hash: " + chmAtomicCuentaPalabras256.size() );
      System.out.println();

     //Implementacion paralela 8: Uso de Streams
     t1 = System.nanoTime();
     Map<String,Long> stCuentaPalabras = vectorLineas.parallelStream()
                                           .filter( s -> s != null )
                                           .map( s -> s.split( "\\W+" ) )
                                           .flatMap( Arrays::stream )
                                           .map( String::trim )
                                           .filter( s -> (s.length() > 0) )
                                           .collect( groupingBy (s -> s, counting()));
     t2 = System.nanoTime();
      tp = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
      System.out.print( "Implementacion Stream 8: " );
      imprimePalabraMasUsadaYVecesLong( stCuentaPalabras );
      System.out.println( " Tiempo(s): " + tp  + " , Incremento " + ts/tp);
      System.out.println( "Num. elems. tabla hash: " + stCuentaPalabras.size() );
      System.out.println();

    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  public static Vector<String> leeFichero( String fileName ) {
    BufferedReader br; 
    String         linea;
    Vector<String> data = new Vector<String>();

    try {
      br = new BufferedReader( new FileReader( fileName ) );
      while( ( linea = br.readLine() ) != null ) {
        //// System.out.println( "Leida linea: " + linea );
        data.add( linea );
      }
      br.close(); 
    } catch( FileNotFoundException ex ) {
      ex.printStackTrace();
    } catch( IOException ex ) {
      ex.printStackTrace();
    }
    return data;
  }

  // -------------------------------------------------------------------------
  public static void contabilizaPalabra( 
                         HashMap<String,Integer> cuentaPalabras,
                         String palabra ) {
    Integer numVeces = cuentaPalabras.get( palabra );
    if( numVeces != null ) {
      cuentaPalabras.put( palabra, numVeces+1 );
    } else {
      cuentaPalabras.put( palabra, 1 );
    }
  }   

  // --------------------------------------------------------------------------
  static void imprimePalabraMasUsadaYVeces(
                  Map<String,Integer> cuentaPalabras ) {
    Vector<Map.Entry> lista = 
        new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    String palabraMasUsada = "";
    int    numVecesPalabraMasUsada = 0;
    // Calcula la palabra mas usada.
    for( int i = 0; i < lista.size(); i++ ) {
      String palabra = ( String ) lista.get( i ).getKey();
      int numVeces = ( Integer ) lista.get( i ).getValue();
      if( i == 0 ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      } else if( numVecesPalabraMasUsada < numVeces ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      }
    }
    // Imprime resultado.
    System.out.print( "( Palabra: '" + palabraMasUsada + "' " + 
                         "veces: " + numVecesPalabraMasUsada + " )" );
  }
    static void imprimePalabraMasUsadaYVecesAtomic(
            Map<String,AtomicInteger> cuentaPalabras ) {
        Vector<Map.Entry> lista =
                new Vector<Map.Entry>( cuentaPalabras.entrySet() );

        String palabraMasUsada = "";
        int    numVecesPalabraMasUsada = 0;
        // Calcula la palabra mas usada.
        for( int i = 0; i < lista.size(); i++ ) {
            String palabra = ( String ) lista.get( i ).getKey();
            AtomicInteger numVecesAtomic = ( AtomicInteger ) lista.get( i ).getValue();
            int numVeces = numVecesAtomic.get();
            if( i == 0 ) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            } else if( numVecesPalabraMasUsada < numVeces ) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            }
        }
        // Imprime resultado.
        System.out.print( "( Palabra: '" + palabraMasUsada + "' " +
                "veces: " + numVecesPalabraMasUsada + " )" );
    }
    static void imprimePalabraMasUsadaYVecesLong(
            Map<String,Long> cuentaPalabras ) {
        Vector<Map.Entry> lista =
                new Vector<Map.Entry>( cuentaPalabras.entrySet() );

        String palabraMasUsada = "";
        long    numVecesPalabraMasUsada = 0;
        // Calcula la palabra mas usada.
        for( int i = 0; i < lista.size(); i++ ) {
            String palabra = ( String ) lista.get( i ).getKey();
            long numVeces = ( Long ) lista.get( i ).getValue();
            if( i == 0 ) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            } else if( numVecesPalabraMasUsada < numVeces ) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            }
        }
        // Imprime resultado.
        System.out.print( "( Palabra: '" + palabraMasUsada + "' " +
                "veces: " + numVecesPalabraMasUsada + " )" );
    }
  // --------------------------------------------------------------------------
  static void printCuentaPalabrasOrdenadas(
                  HashMap<String,Integer> cuentaPalabras ) {
    int             i, numVeces;
    List<Map.Entry> list = new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    // Ordena por valor.
    Collections.sort( 
        list,
        new Comparator<Map.Entry>() {
            public int compare( Map.Entry e1, Map.Entry e2 ) {
              Integer i1 = ( Integer ) e1.getValue();
              Integer i2 = ( Integer ) e2.getValue();
              return i2.compareTo( i1 );
            }
        }
    );
    // Muestra contenido.
    i = 1;
    System.out.println( "Veces Palabra" );
    System.out.println( "-----------------" );
    for( Map.Entry e : list ) {
      numVeces = ( ( Integer ) e.getValue () ).intValue();
      System.out.println( i + " " + e.getKey() + " " + numVeces );
      i++;
    }
    System.out.println( "-----------------" );
  }
}

class MiHebra_1 extends Thread{
  Vector<String> palabras;
  final Map<String, Integer> cuentaPalabras;
  int inicio;
  int fin;

  public MiHebra_1(Vector<String> palabras, Map<String, Integer> mapa, int inicio, int fin) {
    this.palabras = palabras;
    this.cuentaPalabras = mapa;
    this.inicio = inicio;
    this.fin = fin;
  }

  @Override
  public void run(){
    String palabraActual;

    for (int i = inicio; i < fin ; i++ ) {
      String[] palabras = this.palabras.get(i).split( "\\W+" );

      for( int j = 0; j < palabras.length; j++ ) {
        palabraActual = palabras[ j ].trim();

        if( palabraActual.length() > 0 ) {
          synchronized (cuentaPalabras) {
            Integer veces = cuentaPalabras.getOrDefault(palabraActual, 0);
            cuentaPalabras.put(palabraActual, veces + 1);
          }
        }
      }
    }
  }
}

class MiHebra_2 extends Thread {
  final Vector<String> vectorLineas;
  final Map<String, Integer> cuentaPalabras;
  final int inicio;
  final int fin;

  public MiHebra_2(Vector<String> lineas, Map<String, Integer> mapa, int inicio, int fin) {
    this.vectorLineas = lineas;
    this.cuentaPalabras = mapa;
    this.inicio = inicio;
    this.fin = fin;
  }

  public void run(){
    String palabraActual;
    for (int i = inicio; i < fin ; i++ ) {

      String[] palabras = vectorLineas.get(i).split( "\\W+" );

      for( int j = 0; j < palabras.length; j++ ) {
        palabraActual = palabras[ j ].trim();

        if( palabraActual.length() > 0 ) {
          synchronized (cuentaPalabras) {
            Integer veces = cuentaPalabras.getOrDefault(palabraActual, 0);
            cuentaPalabras.put(palabraActual, veces + 1);
          }
        }
      }
    }
  }
}

class MiHebra_3 extends Thread {
    final Vector<String> vectorLineas;
    final Map<String, Integer> cuentaPalabras;
    final int inicio;
    final int fin;

    public MiHebra_3(Vector<String> lineas, Map<String, Integer> mapa, int inicio, int fin) {
        this.vectorLineas = lineas;
        this.cuentaPalabras = mapa;
        this.inicio = inicio;
        this.fin = fin;
    }

    public void run(){
        String palabraActual;
        for (int i = inicio; i < fin ; i++ ) {

            String[] palabras = vectorLineas.get(i).split( "\\W+" );

            for( int j = 0; j < palabras.length; j++ ) {
                palabraActual = palabras[ j ].trim();

                if( palabraActual.length() > 0 ) {
                    synchronized (cuentaPalabras) {
                        Integer veces = cuentaPalabras.getOrDefault(palabraActual, 0);
                        cuentaPalabras.put(palabraActual, veces + 1);
                    }
                }
            }
        }
    }
}

class MiHebra_4 extends Thread {
    final Vector<String> vectorLineas;
    final ConcurrentHashMap<String, Integer> cuentaPalabras;
    final int inicio;
    final int fin;

    public MiHebra_4(Vector<String> lineas, ConcurrentHashMap<String, Integer> mapa, int inicio, int fin) {
        this.vectorLineas = lineas;
        this.cuentaPalabras = mapa;
        this.inicio = inicio;
        this.fin = fin;
    }

    public void run(){
        String palabraActual;
        for (int i = inicio; i < fin ; i++ ) {

            String[] palabras = vectorLineas.get(i).split( "\\W+" );

            for( int j = 0; j < palabras.length; j++ ) {
                palabraActual = palabras[ j ].trim();

                if( palabraActual.length() > 0 ) {
                    cuentaPalabras.merge(palabraActual, 1, (old, val) -> (Integer) (old+val));
                }
            }
        }
    }
}

class MiHebra_5 extends Thread {
    final Vector<String> vectorLineas;
    final ConcurrentHashMap<String, Integer> cuentaPalabras;
    final int inicio;
    final int fin;

    public MiHebra_5(Vector<String> lineas, ConcurrentHashMap<String, Integer> mapa, int inicio, int fin) {
        this.vectorLineas = lineas;
        this.cuentaPalabras = mapa;
        this.inicio = inicio;
        this.fin = fin;
    }

    public void run(){
        String palabraActual;
        for (int i = inicio; i < fin ; i++ ) {

            String[] palabras = vectorLineas.get(i).split( "\\W+" );

            for( int j = 0; j < palabras.length; j++ ) {
                palabraActual = palabras[ j ].trim();

                if( palabraActual.length() > 0 ) {

                    if (cuentaPalabras.putIfAbsent(palabraActual, 1) != null) {
                        Integer valorActual;
                        do {
                            valorActual = cuentaPalabras.get(palabraActual);
                        } while (!cuentaPalabras.replace(palabraActual, valorActual, valorActual + 1));
                    }
                }
            }
        }
    }
}

class MiHebra_6 extends Thread {
    final Vector<String> vectorLineas;
    final ConcurrentHashMap<String, AtomicInteger> cuentaPalabras;
    final int inicio;
    final int fin;

    public MiHebra_6(Vector<String> lineas, ConcurrentHashMap<String, AtomicInteger> mapa, int inicio, int fin) {
        this.vectorLineas = lineas;
        this.cuentaPalabras = mapa;
        this.inicio = inicio;
        this.fin = fin;
    }

    public void run(){
        String palabraActual;
        for (int i = inicio; i < fin ; i++ ) {

            String[] palabras = vectorLineas.get(i).split( "\\W+" );

            for( int j = 0; j < palabras.length; j++ ) {
                palabraActual = palabras[ j ].trim();

                if( palabraActual.length() > 0 ) {
                    cuentaPalabras.putIfAbsent(palabraActual, new AtomicInteger(0));
                    cuentaPalabras.get(palabraActual).incrementAndGet();
                }
            }
        }
    }
}