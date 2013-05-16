package scriptella.driver.vtdxml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
		
		try {
			url.openStream();
		} catch (IOException e) {
			throw new VtdXmlXPathProviderException("expection while opening the stream for: " + url.toString(), e);
		}

		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		InputStream is = null;
		
		try {
			is = url.openStream();
			byte[] byteChunk = new byte[4096]; // Or whatever size you want to
												// read in at a time.
			int n;

			while ((n = is.read(byteChunk)) > 0) {
				bais.write(byteChunk, 0, n);
			}
		} catch (IOException e) {
			throw new VtdXmlXPathProviderException("Failed while reading bytes from url: " + url.toString(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("couldn't close the input stream for url: " + url);
				}
			}
		}

		VTDGen vg = new VTDGen();
		byte[] b = bais.toByteArray();
		
		vg.setDoc(b);

		try {
			vg.parse(false);
		} catch (Exception e) {
			throw new VtdXmlXPathProviderException("Exception while parsing the url: " + url.toString(), e);
		} 
		
		return vg.getNav();
	}
}
