package eu.triskell.client.sicav;

import java.io.IOException;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * @author tahiana
 *
 */
public class TkAuthenticator {

	static Logger logger=Logger.getLogger(TkAuthenticator.class.getName());
	
	String jSessionId;
	String authHash;
	
	String proxyUser;
	String proxyPassword;
	String proxyHost;
	String proxyPort;

	public Logger getLogger() {
		return logger;
	} 

	public void setLogger(Logger logger) {
		TkAuthenticator.logger = logger;
	}
	
	public TkAuthenticator(){
		jSessionId = "";
		authHash ="";
	}
	public String getjSessionId() {
		return jSessionId;
	}

	public String getAuthHash() {
		return authHash;
	}
	
	public String getProxyUser() {
		return proxyUser;
	}
	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}
	public String getProxyPassword() {
		return proxyPassword;
	}
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
	public String getProxyHost() {
		return proxyHost;
	}
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	public String getProxyPort(){
		return proxyPort;
	}
	public void setProxyPort(String proxyPort){
		this.proxyPort = proxyPort;
	}

	/*
	 * Password encrypted by MD5 or SHA-256
	 */
	public String convertPasswordHash(String crypto, String password){
		byte[] uniqueKey = password.getBytes(StandardCharsets.UTF_8);
		byte[] hash      = null;
		try{
			hash = MessageDigest.getInstance(crypto).digest(uniqueKey);
		}
		catch(NoSuchAlgorithmException e){
			logger.severe("Password not encrypted. Please retry.");
			logger.severe("Error: " + e.getMessage());
		}
		StringBuilder hashString = new StringBuilder();
		for (int i = 0; i < hash.length; i++)
		{
		        String hex = Integer.toHexString(hash[i]);
		        if (hex.length() == 1)
		        {
		                hashString.append('0');
		                hashString.append(hex.charAt(hex.length() - 1));
		        }
		        else
		                hashString.append(hex.substring(hex.length() - 2));
		}
		return hashString.toString();			
	}
	
	/*
	 * Session ID returned by the authenticator request 
	 */
	public void setjSessionId(String header){
		String J_SESSION_ID = "";
		String[] param = header.split(",");	          
        for(String s : param){	        	    	  
      	  if(s.contains("JSESSIONID")){	        		
      		String[] p = s.split(";");
      		J_SESSION_ID = p[0];	        		
      		break;
      	  }
        }
        this.jSessionId = J_SESSION_ID.split("=")[1];  
	}
	
	/*
	 * An auth key returned by the authenticator request 
	 */
	public void setAuthHash(String serverCookies){
		if(serverCookies != null){
            String[] cookies = serverCookies.split(";");
            for(String s : cookies){	            	  
                s = s.trim();
                if(s.split("=")[0].equals("authash")){
              	  this.authHash = s.split("=")[1];
                    break;
                }
            }
        }		
	}
	
	public HttpURLConnection proxySetup(String urlInput){
		logger.finest("proxySetup : proxySetup starts");
        URL url;
        try {
            url = new URL(urlInput);
            
            int port = 80;            
            try{
            	port = Integer.parseInt(this.proxyPort);
            } catch(Exception e){
            	
            }

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyHost, port)); // or whatever your proxy is
            HttpURLConnection uc = (HttpURLConnection)url.openConnection(proxy);
            
            logger.finest("proxySetup : proxySetup host " + this.proxyHost);
            logger.finest("proxySetup : proxySetup port " + this.proxyPort);
            
            System.setProperty("https.proxyHost", this.proxyHost);
            System.setProperty("https.proxyPort", this.proxyPort);
            System.setProperty("http.proxyHost", this.proxyHost);
            System.setProperty("http.proxyPort", this.proxyPort);
            String encoded = new String(Base64.getEncoder().encode((this.proxyUser + ":" + this.proxyPassword).getBytes()));

            logger.finest("proxySetup - proxySetup username " + this.proxyUser);
            logger.finest("proxySetup - proxySetup password " + this.proxyPassword);
            
            uc.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
            Authenticator.setDefault(new ProxyAuthenticator(this.proxyUser, this.proxyPassword));

            logger.finest("proxySetup : proxySetup success");
            return uc;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.severe("ProxySetup : proxySetup - Failed");
            e.printStackTrace();
        }
        return null;
    }
	
	/*
	 * Authenticator request execution
	 */
	public boolean authenticatorRequest(String authURL){
		
		boolean authOK = false;
		
		try {
			  logger.finest(authURL);
			  HttpURLConnection httpConnection = null;
	          if(this.proxyHost.equalsIgnoreCase("") || this.proxyPort.equalsIgnoreCase("")){
	        	  URL restServiceURL = new URL(authURL);
	        	  httpConnection = (HttpURLConnection) restServiceURL.openConnection();
	          } else{
	        	  httpConnection = proxySetup(authURL);
	          }
	          httpConnection.setRequestMethod("GET");
	          
	          if (httpConnection.getResponseCode() != 200) {
	        	  logger.severe("Error:	Authenticattion failure: " + httpConnection.getResponseCode());
	              throw new RuntimeException("Error:	Authenticattion failure: " + httpConnection.getResponseCode());
	          }
	          else{
	        	   	authOK = true;
	        	   	String header = httpConnection.getHeaderFields().toString();
	        	   	this.setjSessionId(header);                        
	   
	        	   	String serverCookies = httpConnection.getHeaderField("Set-Cookie");
	        	   	this.setAuthHash(serverCookies);
	          }
	          
	         logger.finest("Authentication successful:  " + httpConnection.getResponseCode() + " - OK");	                   	          	          	          	              
	         httpConnection.disconnect();
		} 
		catch (MalformedURLException e1) {
	        e1.printStackTrace();
	        logger.severe("Error: connection " + e1.getMessage());
	    } 
		catch(ConnectException e3){
			logger.severe("Error:	Connection Time out. Please relaunch the console app." + e3.getMessage());
		}
		catch(UnknownHostException e){
			logger.severe("Error:	Unknown host. Please relaunch the console app." + e.getMessage());
		}
		catch (IOException e2) {
			logger.severe("Error:	" + e2.getMessage() + "\n was occured. Please contact the administrator.");
	    }		
		return authOK;
	}
	
	
	
	/*
	 * Anthenticator log out
	 */
	public boolean authenticatorLogout(String authURL){
		
		boolean authOK = false;
		
		try {          
	          HttpURLConnection httpConnection = null;
	          if(this.proxyHost.equalsIgnoreCase("") || this.proxyPort.equalsIgnoreCase("")){
	        	  URL restServiceURL = new URL(authURL);
	        	  httpConnection = (HttpURLConnection) restServiceURL.openConnection();
	          } else{
	        	  httpConnection = proxySetup(authURL);
	          }
	          httpConnection.setRequestMethod("GET");                      
	
	          if (httpConnection.getResponseCode() != 200) {
	        	  logger.severe("Error:	Logout failure: " + httpConnection.getResponseCode());
	              throw new RuntimeException("Error:	Logout failure: " + httpConnection.getResponseCode());
	          }
	          else{
	        	   	authOK = true;
	        	   	String header = httpConnection.getHeaderFields().toString();
	        	   	this.setjSessionId(header);                        
	   
	        	   	String serverCookies = httpConnection.getHeaderField("Set-Cookie");
	        	   	this.setAuthHash(serverCookies);
	          }

	         logger.finest("Log out successful:  " + httpConnection.getResponseCode() + " - OK");                    	          	          	          	              
	         httpConnection.disconnect();
		} 
		catch (MalformedURLException e1) {
			logger.severe("MalformedURLException: " + e1);
	        e1.printStackTrace();
	    } 
		catch(ConnectException e3){
			logger.severe("Not properly logged out: " + e3);
		}
		catch(UnknownHostException e){
			logger.severe("Not properly logged out: " + e);
		}
		catch (IOException e2) {
			logger.severe("Not properly logged out: " + e2);
	    }		
		return authOK;
	}
	
	class ProxyAuthenticator extends Authenticator {

	    private String user, password;

	    public ProxyAuthenticator(String user, String password) {
	        this.user = user;
	        this.password = password;
	    }

	    protected PasswordAuthentication getPasswordAuthentication() {
	        return new PasswordAuthentication(user, password.toCharArray());
	    }
	}

}
