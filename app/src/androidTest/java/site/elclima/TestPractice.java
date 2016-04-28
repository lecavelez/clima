package site.elclima;

import android.test.AndroidTestCase;

/**
 * Created by lvelez on 4/28/16.
 */
public class TestPractice extends AndroidTestCase {

    @Override
    //Siempre se va ejecutar al inicio de cada test
    protected void setUp() throws Exception{
        super.setUp();
    }

    //Al final de cada test
    public void testThatDemonstrateAssertions() throws Throwable{
        int a = 5;
        int b = 3;
        int c = 5;
        int d = 10;

        //Lo que queremos saber que exista
        assertEquals("X should be equal", a,c); //Son iguales ambos?, en caso que no poner el mensaje
        assertTrue("Y should be true", d > a); //Se pregunta si es verdadera
        assertFalse("Z should be false", a==b);

        if(b>d){
            fail("XX should never happen");
        }
    }

    @Override
    protected  void tearDown() throws Exception{
        super.tearDown();
    }
}
