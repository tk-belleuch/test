package eu.triskell.client.sicav;

import java.util.logging.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;


/**
 * @author vcailleaud
 *
 */
public class TkFTP {

	/**
	 * @param args
	 */

	
	static Logger logger=Logger.getLogger(TkFTP.class.getName());
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		TkFTP.logger = logger;
	}

	/**

	 *
	 */

	public static boolean FTP_download(String server, 
									int port,
									String user,
									String pass,
									String localDIR) {
		
		boolean success = false;

		FTPClient ftpClient = new FTPClient();

		try {
			ftpClient.connect(server, port);
			ftpClient.login(user, pass);

			// use local passive mode to pass firewall
			ftpClient.enterLocalPassiveMode();
			
			// lists files and directories in the current working directory
			FTPFile[] files = ftpClient.listFiles();
			 
			// iterates over the files and prints details for each
			DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 
			for (FTPFile file : files) {
			    String details = file.getName();
			    if (file.isDirectory()) {
			        details = "[" + details + "]";
			    }
			    details += "\t\t" + file.getSize();
			    details += "\t\t" + dateFormater.format(file.getTimestamp().getTime());
			    System.out.println(details);
			    logger.info(details);
			    
			    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			    
			    //get output stream
                OutputStream output;
                output = new FileOutputStream(localDIR + "/" + file.getName());
                //get the file from the remote system
                ftpClient.retrieveFile(file.getName(), output);
                //close output stream
                output.close();

                //delete the file
                ftpClient.deleteFile(file.getName());
			}

			// get details of a file or directory
			String remoteFilePath = "/";

			FTPFile ftpFile = ftpClient.mlistFile(remoteFilePath);
			if (ftpFile != null) {
				String name = ftpFile.getName();
				long size = ftpFile.getSize();
				String timestamp = ftpFile.getTimestamp().getTime().toString();
				String type = ftpFile.isDirectory() ? "Directory" : "File";

				System.out.println("Name: " + name);
				System.out.println("Size: " + size);
				System.out.println("Type: " + type);
				System.out.println("Timestamp: " + timestamp);
				logger.info("Name: " + name);
				logger.info("Size: " + size);
				logger.info("Type: " + type);
				logger.info("Timestamp: " + timestamp);
			} else {
				System.out.println("The specified file/directory may not exist!");
				logger.finest("The specified file/directory may not exist!");
			}

			ftpClient.logout();
			ftpClient.disconnect();
			
			success = true;

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return success;
	}
	 
}


