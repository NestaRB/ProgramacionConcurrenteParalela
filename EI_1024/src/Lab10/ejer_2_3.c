#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

// ============================================================================
int main( int argc, char * argv[] ) {
  int  numProcs, miId;

  // Inicializa MPI.
  MPI_Init( &argc, &argv );
  MPI_Comm_size( MPI_COMM_WORLD, &numProcs );
  MPI_Comm_rank( MPI_COMM_WORLD, &miId );
  
  // --------------------------------------------------------------------------
  int prc, suma, aux, dato;
  MPI_Status st ;
  // Cada proceso dispone de un dato cualquiera.
  dato = numProcs - miId + 1;
  printf( "Proc %d Dato vale: %d \n", miId, dato );
  //He cambiado cosas de aquí por lo de antes (explicado en 2.1 y 2.2 )
 
  // ... Incluir codigo asociado a los ejercicios 1 y 2
  int nuevoDato;
  if (miId % 2 != 0){
    nuevoDato = 0;
  } else {
    nuevoDato = dato;
  }

  MPI_Reduce( &nuevoDato, &suma, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD  );

  printf("Valor inicial: %d del poceso %d \n", dato, miId);

  MPI_Barrier ( MPI_COMM_WORLD );

  if ( miId == 0){
    printf("Suma: %d \n", suma);
  }
  // --------------------------------------------------------------------------

  // Finalizacion de MPI.
  MPI_Finalize();

  // Fin de programa.
  printf( "Final de programa (%d) \n", miId );
  return 0;
}

