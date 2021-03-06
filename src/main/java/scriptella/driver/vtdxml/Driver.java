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

import scriptella.spi.AbstractScriptellaDriver;
import scriptella.spi.Connection;
import scriptella.spi.ConnectionParameters;
import scriptella.spi.DialectIdentifier;

/**
 * Represents a driver for querying XML files with XPath expressions 
 * using the vtd-xml library.
 *
 * @author Kammermann Florian
 */
public class Driver extends AbstractScriptellaDriver {
	
	/**
	 * The dialect supported by this driver.
	 */
    public static final DialectIdentifier DIALECT = new DialectIdentifier("XPath", "1.0");
    
    /**
     * Name of the <code>encoding</code> connection property.
     * Specifies charset encoding in text files.
     */
    public static final String ENCODING = "encoding";

    public Connection connect(ConnectionParameters connectionParameters) {
        return new VtdXmlXPathConnection(connectionParameters);
    }
}
