package scriptella.driver.vtdxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ximpleware.ParseException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

public class VTDNavCreator {
	
	private static final Logger log = LoggerFactory.getLogger(VTDNavCreator.class);

	public static VTDNav getVTDNav(URL url) {
        try {
            VTDNav vtdNav = createVTDNavFromXmlFile(url.getFile());
            return vtdNav;
        } catch (Exception e) {
            throw new VtdXmlXPathProviderException("Unable to parse document " + url, e);
        }
    }

    private static VTDNav createVTDNavFromXmlFile(String suggestedFilePath) {
		
		VTDGen vg = new VTDGen();
		String effectiveFilePath = null;

		if (new File(suggestedFilePath).exists()) {
			effectiveFilePath = new File(suggestedFilePath).toString();
		}
		if (VTDNavCreator.class.getClassLoader().getResource(suggestedFilePath) != null) {
			effectiveFilePath = VTDNavCreator.class.getClassLoader().getResource(suggestedFilePath).getFile();
		}

		if (null == effectiveFilePath) {
			log.error("suggestedFilePath: " + suggestedFilePath + " can't be loaded over file system or classpath");
			throw new VtdXmlXPathProviderException("suggestedFilePath: " + suggestedFilePath + " can't be loaded over file system or classpath");
		}
		
		try { 

			// this code is copied from the vtd-xml sourcecode (VTDGen)
			// the problem is, that VTDGen consumes all execptions and only return true/false
			// but we wanna have the exceptions
			File f = new File(effectiveFilePath);
		    FileInputStream fis = new FileInputStream(f);
	        byte[] b = new byte[(int) f.length()];
            int offset = 0;
            int numRead = 0;
            int numOfBytes = 1048576;

            //I choose this value randomally, 
            //any other (not too big) value also can be here.
            if (b.length-offset<numOfBytes){
            	numOfBytes=b.length-offset;
            }
            	
            while (offset < b.length && (numRead=fis.read(b, offset, numOfBytes)) >= 0) {                                 
                offset += numRead;
                if (b.length-offset<numOfBytes) {numOfBytes=b.length-offset;}        
            }
            
	    	vg.setDoc(b);
	    	fis.close();

	    	vg.parse(false);  // set namespace awareness to false
	    	return vg.getNav();
			
		} catch(IOException e) {
			log.error("error while parsing the file: " + effectiveFilePath);
			throw new VtdXmlXPathProviderException(e);
		} catch(ParseException e) {
			log.error("error while parsing the file: " + effectiveFilePath);
			throw new VtdXmlXPathProviderException(e);
		}
	}
}
