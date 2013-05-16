package scriptella.driver.vtdxml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

/**
 * Responsible to read the xml from the url and create the VTDNav of vtd-xml.
 * All the data of the url will be parsed in this class and is then available
 * for inmemory processing.
 *  
 * @author Kammermann Florian
 */
public class VTDNavCreator {

	private static final Logger log = LoggerFactory.getLogger(VTDNavCreator.class);

	public static VTDNav getVTDNav(URL url) {
		try {
			VTDNav vtdNav = createVTDNavFromXmlFile(url);
			return vtdNav;
		} catch (Exception e) {
			throw new VtdXmlXPathProviderException("Unable to parse document " + url, e);
		}
	}

	private static VTDNav createVTDNavFromXmlFile(URL url) {
		
		byte[] b = null;
		try {
			url.openStream();
			InputStream is = url.openStream();
			b = IOUtils.toByteArray(is);
		} catch (IOException e) {
			throw new VtdXmlXPathProviderException("expection while converting the url into a byte array: " + url.toString(), e);
		}

		VTDGen vg = new VTDGen();
		vg.setDoc(b);

		try {
			vg.parse(false);
		} catch (Exception e) {
			throw new VtdXmlXPathProviderException("Exception while parsing the url: " + url.toString(), e);
		} 
		
		return vg.getNav();
	}
}
