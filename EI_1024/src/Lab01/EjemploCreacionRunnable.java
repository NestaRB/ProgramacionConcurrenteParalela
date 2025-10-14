package Lab01;

class MiRun implements Runnable
{
    private int miId;

    public MiRun( int miId )
    {
        this.miId = miId;
    }
    public void run()
    {
    for( int i = 0; i < 1000; i++ )
        {
            System.out.println( "Hebra: " + miId );
        }
    }
}

class EjemploCreacionRunnable
{
    public static void main(String[] args)
    {
        Thread h0 = new Thread( new MiRun( 0 ) );
        Thread h1 = new Thread( new MiRun( 1 ) );
        h0.run();
        h1.run();
    }
}
