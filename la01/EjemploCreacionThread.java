class MiHebra extends Thread
{
    private int miId;
    public MiHebra( int miId )
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
class EjemploCreacionThread
{
    public static void main( String args[] )
    {
        MiHebra h0 = new MiHebra(0);
        MiHebra h1 = new MiHebra(1);
        h0.start();
        h1.start();
    }
}
