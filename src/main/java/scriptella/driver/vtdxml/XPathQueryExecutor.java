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

import java.io.IOException;

import scriptella.expression.PropertiesSubstitutor;
import scriptella.spi.AbstractConnection;
import scriptella.spi.ParametersCallback;
import scriptella.spi.QueryCallback;
import scriptella.spi.Resource;
import scriptella.util.IOUtils;
import scriptella.util.StringUtils;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

/**
 * Executor for XPath queries with vtd-xml.
 *
 * @author Kammermann Florian
 */
public class XPathQueryExecutor implements ParametersCallback {
    private PropertiesSubstitutor substitutor = new PropertiesSubstitutor();
    private VTDNav vtdNav;
    private String expressionStr;
    private AbstractConnection.StatementCounter counter;
    private boolean returnArrays;

    /**
     * Crates executor to query document using a specified xpath expression.
     *
     * @param context       thread local for sharing current node between queries.
     *                      The instance of thread local is shared between all connection queries.
     * @param document      document to query.
     * @param xpathResource resource with xpath expression.
     * @param compiler      xpath expression compiler
     * @param counter       statement counter.
     * @param returnArrays  true if string arrays should be returned for variables.
     */
    public XPathQueryExecutor(VTDNav vtdNav, Resource xpathResource, AbstractConnection.StatementCounter counter, boolean returnArrays) {
        this.vtdNav = vtdNav;
        this.counter = counter;
        this.returnArrays = returnArrays;
        try {
            expressionStr = IOUtils.toString(xpathResource.open());
        } catch (IOException e) {
            throw new VtdXmlXPathProviderException("Unable to read XPath query content");
        }
    }

    /**
     * Executes a query and notifies queryCallback for each found node.
     *
     * @param queryCallback    callback to notify for each found node.
     * @param parentParameters parent parameters to inherit.
     */
    public void execute(final QueryCallback queryCallback, final ParametersCallback parentParameters) {
    	
    	// expressionStr = /TEST/TESTER
        AutoPilot ap = new AutoPilot(vtdNav);
       
        try {
			ap.selectXPath(expressionStr);
		} catch (XPathParseException e) {
			throw new VtdXmlXPathProviderException("while selecting the xpath: " + expressionStr + " an exception occured.", e);
		} finally {
            substitutor.setParameters(null);
        }
        
        int result = -1;
        try {
			while((result = ap.evalXPath()) != -1){
			    queryCallback.processRow(this);
			}
		} catch(XPathEvalException e) {
			throw new VtdXmlXPathProviderException("couldn't evaluate the xpath: " + expressionStr, e);
		} catch (NavException e) {
			throw new VtdXmlXPathProviderException("couldn't navigate to the xpath: " + expressionStr, e);
		} finally {
            substitutor.setParameters(null);
        }
    }
 
    public Object getParameter(final String name) {
    	
    	try {
    		vtdNav.push();
    		
    		String elementText = null;
    		AutoPilot ap = new AutoPilot(vtdNav);
    		if(! StringUtils.isEmpty(name)) {
    			ap.selectXPath(name);
    			elementText = ap.evalXPathToString();
    		} else {
    			ap.selectXPath(".");
    			elementText = ap.evalXPathToString();
    		}
            
            vtdNav.pop();
			
            return elementText;
		} catch (Exception e) {
			throw new VtdXmlXPathProviderException(e);
		}
    }
}
