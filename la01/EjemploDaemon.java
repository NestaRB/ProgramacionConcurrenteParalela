class MiHebraDaemon extends Thread
{
    private int miId;
    private int num1;
    private int num2;

    public MiHebraDaemon( int miId, int num1, int num2 )
    {
        this.miId = miId;
        this.num1 = num1;
        this.num2 = num2;
    }

    public void run()
    {
        long suma = 0;

        System.out.println( "Hebra Auxiliar " + miId + " , inicia calculo" );
        for( int i = num1; i <= num2 ; i++ )
        {
            suma += (long) i;
        }
        System.out.println( "Hebra Auxiliar " + miId + " , suma: " + suma);
    }

}

class EjemploDaemon
{
    public static void main( String args[] )
    {
        System.out.println( "Hebra Principal inicia" );
        // Crea y arranca hebras sumando desde 1 hasta 1000000
        MiHebraDaemon h0 = new MiHebraDaemon(0, 1, 1000000);
        MiHebraDaemon h1 = new MiHebraDaemon(1, 1, 1000000);

        // D
        //h0.setDaemon( true );
        //h1.setDaemon( true );
        h0.start();
        h1.start();

        // Espera la finalizacion de las hebras t0 y t1
        // E

        try {
            h0.join();
            h1.join();
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }

        System.out.println( "Hebra Principal finaliza" );
    }
}

