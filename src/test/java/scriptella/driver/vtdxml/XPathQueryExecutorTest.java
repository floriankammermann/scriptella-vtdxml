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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import scriptella.configuration.StringResource;
import scriptella.spi.AbstractConnection;
import scriptella.spi.ParametersCallback;
import scriptella.spi.Resource;

import com.ximpleware.VTDNav;

/**
 * Tests for {@link XPathQueryExecutor}.
 *
 * @author Fyodor Kupolov
 * @version 1.0
 */
public class XPathQueryExecutorTest extends AbstractTestCase {
    private ThreadLocal<VTDNav> context;

    protected void setUp() throws Exception {
        context = new ThreadLocal<VTDNav>();
    }

    public void test() throws ParserConfigurationException, IOException, SAXException {
    	VTDNav vtdNav = VTDNavCreator.getVTDNav(new File("src/test/resources/xml1.xml").toURI().toURL());
    	
        Resource res = new StringResource("/html/body/table/tr");
        XPathQueryExecutor exec = new XPathQueryExecutor(context, vtdNav, res, new AbstractConnection.StatementCounter(), true);
        IndexedQueryCallback callback = new IndexedQueryCallback() {

            protected void processRow(final ParametersCallback parameters, final int rowNumber) {
                if (rowNumber == 0) {
                    assertEquals("red", parameters.getParameter("@bgcolor"));
                    assertEquals("Column1", parameters.getParameter("th"));                 
                } else {
                    assertEquals(String.valueOf(rowNumber * 2 - 1),  parameters.getParameter("td"));
                }
            }
        };
        exec.execute(callback, MockParametersCallbacks.NULL);
        assertEquals(3,callback.getRowsNumber());
    }

    public void test2() throws ParserConfigurationException, IOException, SAXException {
    	VTDNav vtdNav1 = VTDNavCreator.getVTDNav(new File("src/test/resources/xml2.xml").toURI().toURL());
        Resource res = new StringResource("/xml/element[@attribute=1]");
        XPathQueryExecutor exec = new XPathQueryExecutor(context, vtdNav1, res, new AbstractConnection.StatementCounter(), false);
        IndexedQueryCallback callback = new IndexedQueryCallback() {

            protected void processRow(final ParametersCallback parameters, final int rowNumber) {
                if (rowNumber == 0) {
                    assertEquals("2", parameters.getParameter("attribute"));
                } else {
                    assertEquals("", parameters.getParameter("element"));
                }
            }
        };
        exec.execute(callback, MockParametersCallbacks.NULL);
        assertEquals(1,callback.getRowsNumber());
        //Now select element2, also test substitution
        VTDNav vtdNav2 = VTDNavCreator.getVTDNav(new File("src/test/resources/xml2.xml").toURI().toURL());
        res = new StringResource(" /xml/element2 ");
        exec = new XPathQueryExecutor(context, vtdNav2, res, new AbstractConnection.StatementCounter(), false);
        callback = new IndexedQueryCallback() {

            protected void processRow(final ParametersCallback parameters, final int rowNumber) {
                if (rowNumber == 0) {
                    assertEquals("1", parameters.getParameter("attribute"));
                } else {
                    assertEquals("el2", parameters.getParameter(""));
                }
            }
        };
        exec.execute(callback, MockParametersCallbacks.NAME);
        assertEquals(2,callback.getRowsNumber());
    }

}
