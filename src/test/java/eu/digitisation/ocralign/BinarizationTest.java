/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.digitisation.ocralign;

import eu.digitisation.math.Counter;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author rafa
 */
public class BinarizationTest {

    /**
     * Test of std method, of class Binarization.
     */
    @Test
    public void testStd() throws Exception {
        System.out.println("std");
        Counter<Integer> hist = new Counter<>();

        hist.add(-1, 4);
        hist.add(0, 2);
        hist.add(2, 2);
        // average is 0, std = sqrt(1.5)

        double mu = Binarization.average(hist);
        double std = Binarization.std(hist);

        assertEquals(0, mu, 0.001);
        assertEquals(1.5, std * std, 0.001);
    }

}
