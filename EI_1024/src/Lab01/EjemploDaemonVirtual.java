package Lab01;

class MiHebraDaemonVirtual extends Thread
{
    private int miId;
    private int num1;
    private int num2;

    public MiHebraDaemonVirtual( int miId, int num1, int num2 )
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

class EjemploDaemonVirtual
{
    public static void main( String args[] )
    {
        System.out.println( "Hebra Principal inicia" );

        MiHebraDaemonVirtual r0 = new MiHebraDaemonVirtual( 0, 1, 1000000 );
        MiHebraDaemonVirtual r1 = new MiHebraDaemonVirtual( 1, 1, 1000000 );

        Thread h0 = Thread.startVirtualThread(r0);
        Thread h1 = Thread.startVirtualThread(r1);

        /*
        try {
            h0.join();
            h1.join();
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
         */

        System.out.println( "Hebra Principal finaliza" );
    }
}

