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
  MPI_Status st ; //Aquí faltaba el _
  // Cada proceso dispone de un dato cualquiera.
  dato = numProcs - miId + 1; //Aquí el guión estaba mal
  printf("Proc %d Dato vale: %d \n", miId, dato ); // Aquí las comillas mal tmb
  //Los comentarios de antes los he puesto yo y he cambiado lo que pone en ellos :D
 
  // ... Incluir codigo asociado a los ejercicios 1 y 2

  int nuevoDato;
  int nextPar = miId + 2;
  int prevPar  = miId - 2;

  int ultimoPar = (numProcs % 2 == 0 ? numProcs - 2 : numProcs - 1);


  if (miId == ultimoPar) {
    MPI_Send(&dato, 1, MPI_INT, prevPar, 67, MPI_COMM_WORLD );
  }
  else if (miId % 2 == 0 && miId != 0){
    MPI_Recv(&nuevoDato, 1, MPI_INT, nextPar, 67, MPI_COMM_WORLD, &st);
        suma = dato + nuevoDato;
    MPI_Send( &suma, 1, MPI_INT, prevPar, 67, MPI_COMM_WORLD);
  }
  else if ( miId == 0){

    MPI_Recv(&nuevoDato, 1, MPI_INT, nextPar, 67, MPI_COMM_WORLD, &st);
    suma = dato + nuevoDato;

    printf("Suma total de los pares: %d \n", suma);
  }

  printf("Dato inicial %d del proceso %d \n", dato, miId);
  // --------------------------------------------------------------------------

  // Finalizacion de MPI.
  MPI_Finalize();

  // Fin de programa.
  printf( "Final de programa (%d) \n", miId );
  return 0;
}

