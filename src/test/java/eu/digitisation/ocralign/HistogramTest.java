/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.digitisation.ocralign;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rafa
 */
public class HistogramTest {

    /**
     * Test of std method
     * @throws java.lang.Exception
     */
    @Test
    public void testStd() throws Exception {
        System.out.println("std");
        Histogram hist = new Histogram();

        hist.add(-1, 4);
        hist.add(0, 2);
        hist.add(2, 2);
        // average is 0, std = sqrt(1.5)

        double mu = hist.average();
        double std = hist.std();

        assertEquals(0, mu, 0.001);
        assertEquals(1.5, std * std, 0.001);
    }

}
