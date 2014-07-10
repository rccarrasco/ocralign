/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.digitisation.ocralign;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rafa
 */
public class MainTest {

    public MainTest() {
    }

    /**
     * Test of main method, of class Main.
     */
    @Test
    public void testMain() throws URISyntaxException, IOException {
        System.out.println("main");
        URL resourceUrl1 = getClass().getResource("/00672944.tif");
        File file1 = new File(resourceUrl1.toURI());
        URL resourceUrl2 = getClass().getResource("/00672944.xml");
        File file2 = new File(resourceUrl2.toURI());
        String[] args = {file1.getCanonicalPath(), file2.getCanonicalPath()};
        Main.main(args);
    }

}
