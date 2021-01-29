package eu.triskell.client.sicav;

import java.io.IOException;
import java.text.ParseException;

import eu.triskell.client.sicav.TkConfigFile;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.lang3.SystemUtils;

/**
 * @author vcailleaud
 *
 *
ERROR: Any error/exception that is or might be critical. Our Logger automatically sends an email for each such message on our servers (usage: logger.error("message"); )

WARN: Any message that might warn us of potential problems, e.g. when a user tried to log in with wrong credentials - which might indicate an attack if that happens often or in short periods of time (usage: logger.warn("message"); )

INFO: Anything that we want to know when looking at the log files, e.g. when a scheduled job started/ended (usage: logger.info("message"); )

DEBUG: As the name says, debug messages that we only rarely turn on. (usage: logger.debug("message"); )
 *
 *
 *
 *
 */


public class import_SIVA_VNF_Main {

	/**
	 * @param args
	 */
	
	public static Logger logger;
			
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws NumberFormatException, SecurityException, IOException, InterruptedException, ParseException {

		logger = Logger.getLogger(import_SIVA_VNF_Main.class.getName());
		System.out.println(import_SIVA_VNF_Main.class.getName());
		
		boolean configCheck = false;
		boolean messagesCheck = false;
		String versionAppId = "v1.0.0_20200106";
		
		configCheck = TkConfigFile.requestCheck();
		
		System.out.println("JAVA VERSION : "+System.getProperty("java.runtime.version"));
		System.out.println("LOG LEVEL : "+TkConfigFile.parameters.get("logLevel"));	
		System.out.println("APP VERSION : "+versionAppId);
		System.out.println("WINDOWS OS ? " + SystemUtils.IS_OS_WINDOWS);
		
		try {
			configCheck = TkConfigFile.requestCheck();
			
			if (TkConfigFile.parameters.get("logLevel").equals("DEBUG")) {
				logger.setLevel(Level.FINEST);
			} else if (TkConfigFile.parameters.get("logLevel").equals("INFO")) {
				logger.setLevel(Level.INFO);
			} else if (TkConfigFile.parameters.get("logLevel").equals("WARNING")) {
				logger.setLevel(Level.WARNING);
			} else if (TkConfigFile.parameters.get("logLevel").equals("ERROR")) {
				logger.setLevel(Level.SEVERE);
			}
			// Print log level 
	        System.out.println("Log Level set = " + logger.getLevel()); 
			
	        TkMessageFile messagesFile = new TkMessageFile();
	        messagesCheck = messagesFile.checkMessagesFile();
	        
			if(configCheck & messagesCheck){						
			
				try{				
					FileHandler fileLog = new FileHandler(TkConfigFile.parameters.get("logPath")+"log_"+import_SIVA_VNF_Main.class.getName().replace("Main", "")+ TkUtils.getTodaysDate("yyyyMMddhhmmss")+".log");
					logger.addHandler(fileLog);
					SimpleFormatter formatter = new SimpleFormatter();
					fileLog.setFormatter(formatter);					
					
					logger.info("JAVA VERSION : "+System.getProperty("java.runtime.version"));
					logger.info("LOG LEVEL : "+TkConfigFile.parameters.get("logLevel"));	
					logger.info("APP VERSION : "+versionAppId);
					logger.info("WINDOWS OS ? " + SystemUtils.IS_OS_WINDOWS);
					logger.info("Program starts");
				}
				catch(Exception ex){
					System.out.println(ex.getMessage());
					logger.severe(ex.getMessage());
				}
				
				//If FTP download enable
				if (Boolean.valueOf(TkConfigFile.parameters.get("ftp_download")) == true) {
					TkFTP tkftp = new TkFTP();
					tkftp.setLogger(logger);
					
					//Download files from customers FTP
					if (TkFTP.FTP_download(TkConfigFile.parameters.get("ftp_server"), 
							Integer. parseInt(TkConfigFile.parameters.get("ftp_port")),
							TkConfigFile.parameters.get("ftp_user"),
							TkConfigFile.parameters.get("ftp_pass"),
							TkConfigFile.parameters.get("ftp_local_path"))) {
						logger.info("Some files downloaded from FTP");
					} else {
						logger.info("NO file downloaded from FTP");
					}
				}
				
				//If OB proccess import is enable
				if (Boolean.valueOf(TkConfigFile.parameters.get("file_ob_import_process")) == true) {
					TkImport_SICAV_OB tkob = new TkImport_SICAV_OB();
					tkob.setLogger(logger);
					
					tkob.Import_SICAV_files();
				}
				
				//If CP proccess import is enable
				if (Boolean.valueOf(TkConfigFile.parameters.get("file_cp_import_process")) == true) {
					TkImport_SICAV_Budget_CP tkcp = new TkImport_SICAV_Budget_CP();
					tkcp.setLogger(logger);
					
					tkcp.Import_SICAV_files();
				}
				
				//If AE proccess import is enable
				if (Boolean.valueOf(TkConfigFile.parameters.get("file_ae_import_process")) == true) {
					TkImport_SICAV_Budget_AE tkae = new TkImport_SICAV_Budget_AE();
					tkae.setLogger(logger);
					
					tkae.Import_SICAV_files();
				}

			} else {
				logger.info("Error: Please check if files config.properties and messages.properties exists and check if contents are well sets");
			}
		} finally {  
			logger.info("End of Process");
    	}
	}
}

