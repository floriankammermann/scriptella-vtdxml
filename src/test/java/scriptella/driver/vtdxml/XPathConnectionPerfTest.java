/*
 * Copyright 2006-2012 The Scriptella Project Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scriptella.driver.vtdxml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scriptella.configuration.StringResource;
import scriptella.spi.AbstractConnection;
import scriptella.spi.Connection;
import scriptella.spi.ConnectionParameters;
import scriptella.spi.ParametersCallback;

import com.ximpleware.VTDNav;

/**
 * Tests for {@link scriptella.driver.xpath.XPathConnection}.
 *
 * @author Fyodor Kupolov
 * @version 1.0
 */
public class XPathConnectionPerfTest extends AbstractTestCase {
	
	private final Logger log = LoggerFactory.getLogger(AbstractTestCase.class);
	
    /**
     * History:
     * 07.05.2013 - intel core i5 - 3.1 GHz 164 ms
     */
    public void testQuery() {
        //Create a configuration with non default values
        Map<String, String> props = new HashMap<String, String>();
        URL url = getClass().getResource("/excel.xml");
        ConnectionParameters cp = new ConnectionParameters(new MockConnectionEl(props, url), MockDriverContext.INSTANCE);

        Connection con = new Driver().connect(cp);
        IndexedQueryCallback queryCallback = new IndexedQueryCallback() {
            protected void processRow(final ParametersCallback parameters, final int rowNumber) {
                parameters.getParameter("Cell");
            }
        };
        //Quering 200 times.
        for (int i = 0; i < 200; i++) {
            con.executeQuery(new StringResource("/Workbook/Worksheet/Table/Row"), MockParametersCallbacks.NAME, queryCallback);
        }
        assertEquals(600, queryCallback.getRowsNumber());
    }

    /**
     * History:
     * 07.05.2013 - intel core i5 - 3.1 GHz 925 ms
     */
    public void testQueryLargeDOM() throws ParserConfigurationException, IOException {

    	VTDNav vtdNav = VTDNavCreator.getVTDNav(new File("src/test/resources/table.xml").toURI().toURL());
        
        long startMillis = System.currentTimeMillis();
        
        //Querying 200 times.
        XPathQueryExecutor qe = new XPathQueryExecutor(vtdNav, new StringResource("/table/row[@id mod 2=0]"), 
        											   new AbstractConnection.StatementCounter(), false);
        for (int i = 0; i < 20; i++) {
            IndexedQueryCallback queryCallback = new IndexedQueryCallback() {
                protected void processRow(final ParametersCallback parameters, final int rowNumber) {
                    assertTrue(Integer.parseInt((String) parameters.getParameter("@id")) % 2 == 0);
                }
            };
            qe.execute(queryCallback, MockParametersCallbacks.NULL);
            assertEquals(2000, queryCallback.getRowsNumber());
        }
        
        long usedMillis = System.currentTimeMillis() - startMillis;

    }
    
    /**
     * History:
     * 07.05.2013 - intel core i5 - 3.1 GHz 713 ms
     * the more asserts you execute, longer it takes
     */
    public void testQueryLargeFile() throws ParserConfigurationException, IOException {
    	VTDNav vtdNav = VTDNavCreator.getVTDNav(new File("src/test/resources/large.xml").toURI().toURL());
        
        long startMillis = System.currentTimeMillis();
        
        //Querying 200 times.
        XPathQueryExecutor qe = new XPathQueryExecutor(vtdNav, new StringResource("/DATA/UNIT"), 
        											   new AbstractConnection.StatementCounter(), false);
        IndexedQueryCallback queryCallback = new IndexedQueryCallback() {
        	
            protected void processRow(final ParametersCallback parameters, final int rowNumber) {
                assertEquals(Integer.parseInt((String)parameters.getParameter("NUMBER")), 1234);
                //assertEquals((String)parameters.getParameter("BLE_03_01"), "1234.4321");
                //assertEquals((String)parameters.getParameter("AT"), "IT");
            }
        };
        qe.execute(queryCallback, MockParametersCallbacks.NULL);
        
        int rowsNumber = queryCallback.getRowsNumber();
        assertEquals(42620, rowsNumber);
        
        long usedMillis = System.currentTimeMillis() - startMillis;
    }
    


}
