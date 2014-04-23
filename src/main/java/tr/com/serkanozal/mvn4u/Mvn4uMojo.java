package tr.com.serkanozal.mvn4u;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name="url.fetcher")
public class Mvn4uMojo extends AbstractMojo {
	
	@Parameter
	private String[] wsldUrls;
	
	@Parameter
	private String[] wsdlNames;
	
	@Parameter
	private String saveLocation;
	
    public void execute() throws MojoExecutionException {
    	if (wsldUrls.length != wsdlNames.length) {
    		getLog().error("The WSDL url list and WSDL names array length must be same.");
    		throw new MojoExecutionException("The WSDL url list and WSDL names array length must be same.");
    	}
    	else {
    		for (int i = 0; i < wsldUrls.length; i++) {
            	URL website;
				try {
					website = new URL(wsldUrls[i]);
				} 
				catch (MalformedURLException e) {
					throw new MojoExecutionException("Cannot fetch wsdl from url:" + wsldUrls[i].toString());
				}
            	ReadableByteChannel rbc;
				try {
					rbc = Channels.newChannel(website.openStream());
				} 
				catch (IOException e) {
					getLog().error(e.getMessage(), e);
					throw new MojoExecutionException("Cannot open stream " + wsldUrls[i].toString());
				}
				
				File wsdlFileDir = new File(saveLocation);
				wsdlFileDir.mkdirs();
				File wsdlFile = new File(wsdlFileDir, wsdlNames[i]);
				if (!wsdlFile.exists()) {
					try {
						wsdlFile.createNewFile();
					} 
					catch (IOException e) {
						getLog().error(e.getMessage(), e);
						throw new MojoExecutionException("Cannot create WSDL file " + saveLocation + "/" + wsdlNames[i]);
					}
				} 
				
            	FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(saveLocation + "/" + wsdlNames[i]);
				} 
				catch (FileNotFoundException e) {
					getLog().error(e.getMessage(), e);
					throw new MojoExecutionException("Cannot open WSDL file " + saveLocation + "/" + wsdlNames[i]);
				};
			
            	try {
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
				} 
            	catch (IOException e) {
            		getLog().error(e.getMessage(), e);
					throw new MojoExecutionException("Cannot Write WSDL File.");
				}
				
            }
    	}
    }
    
}
