package eu.triskell.client.sicav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author vcailleaud
 *
 */
public class TkMessageFile {
	
	static Map<String, String> messages;
	boolean fileFound = false;
	Logger logger;
		
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public boolean isFileFound() {
		return fileFound;
	}

	public void setFileFound(boolean fileFound) {
		this.fileFound = fileFound;
	}

	public TkMessageFile(){
		
		messages = new HashMap<String, String>();		
	}
	
	public static boolean getParameter(String parameter_, Properties prop_, boolean paramGet) {
		try{			
			messages.put(parameter_, prop_.getProperty(parameter_).trim());;

			paramGet = true;
		} catch(Exception e){
			System.out.println(e.getMessage()); 
			paramGet = false;
		}
		return paramGet;
	}
	
	public static boolean checkMessagesFile()  throws IOException{
		
		boolean filefound = false; 

		try {
			Properties prop = new Properties();
			
			String propertiesPath = "";

			File jarPath=new File(TkMessageFile.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	        propertiesPath=jarPath.getParentFile().getAbsolutePath();
	        propertiesPath.replace(" ", "\\ ");
	        propertiesPath = propertiesPath.replace("\\", "/");
	        prop.load(new FileInputStream(propertiesPath+"/messages.properties"));
	        
	        String parameter_ = null;
			boolean paramGet = true;				
	        
	        parameter_ = "ob.stage.notallowed";
			paramGet = getParameter(parameter_, prop, paramGet);
			
	        parameter_ = "ob.year.notallowed";
			paramGet = getParameter(parameter_, prop, paramGet);
							
			parameter_ = "ob.projectcode.notexists";
			paramGet = getParameter(parameter_, prop, paramGet);
							
			parameter_ = "ob.projectcode.wrong";
			paramGet = getParameter(parameter_, prop, paramGet);
			
	        filefound=true;
		}
		catch(FileNotFoundException f){
			System.out.println("Error:	messages.properties file not found. Please add this file in the same directory as the Jar's.");
		}
		catch (Exception e) {
			System.out.println("Error: 	" + e);
		} 
		finally {
			
		}
		return filefound;
	}

}
