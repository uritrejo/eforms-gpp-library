package it.polimi.gpplib;

import org.junit.Test;

public class AppTest {

    @Test
    public void testCreateAndPrintXML() throws Exception {
        // This will print the pretty XML to stdout.
        // You could redirect System.out and assert on the output if needed.
        App.createAndPrintXML();
    }
}
