package eu.triskell.client.sicav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.triskell.client.sicav.TkConfigFile.TYPE_REPORT;

/**
 * @author vcailleaud
 *
 */
public class TkDataRequest {
	
	public static String aunthenticatorURL ="triskell/service/rest/login/user/";
	public static String aunthenticatorLogOutURL ="triskell/service/rest/logout";
	public static String wsCreateUserURL = "triskell/service/rest/user/create";
	public static String wsUpdateUserURL = "triskell/service/rest/proxy/operation/execute/UsersRestService/UpdateUserData";
	public static String wsAddTimephasedItemURL = "triskell/service/rest/proxy/operation/execute/TimephasedAttributeAS/AddTimephasedItem";
	public static String wsReportDataToPanelURL = "triskell/service/rest/proxy/operation/execute/ReportService/GetReportDataToPanel";
	public static String wsReadDataObjectservice= "triskell/service/rest/proxy/operation/execute/DataobjectReaderAS/readDataObject";
	public static String wsCreatetimeSheetURL = "triskell/service/rest/timesheet/create";
	public static String wsSearchUserByName = "triskell/service/rest/user/search/xxx/at/NAME";
	public static String wsAddResourceToPool = "triskell/service/rest/proxy/operation/execute/ResourcePanelAS/AddResourceToPool";
	public static String wsSaveResourceData = "triskell/service/rest/proxy/operation/execute/ResourcePanelAS/SaveResourceData";
	public static String wsUpdateParentAssignments = "triskell/service/rest/proxy/operation/execute/ResourcePanelAS/UpdateParentAssignments";
	public static String wsGenerateResourceProfile = "triskell/service/rest/proxy/operation/execute/ResourcePanelAS/GenerateResourceProfile";
	public static String wsCreateDataObjectURL = "triskell/service/rest/dataobject/create";
	public static String wsChangeLifecycleAttrURL = "triskell/service/rest/proxy/operation/execute/ObjectPropertiesPanelAS/AdvancedLifecycleChange";
	public static String wsLoadCustomAttrValuesByTypeURL = "triskell/service/rest/proxy/operation/execute/CustomAttributeService/LoadCustomAttrValuesByType";
	public static String wsSaveTimephasedDataURL = "triskell/service/rest/proxy/operation/execute/TimephasedAttributeAS/SaveTimephasedData";
	public static String wsUpdateAttrURL = "triskell/service/rest/proxy/operation/execute/CustomAttrPanelAS/CustomAttrPanelPutValues";
	
	public static String JsonContentType = "application/json";
	public static String XmlContentType = "application/xml";
	
	public static String strSeparator = "§";
	
	Logger logger=Logger.getLogger(TkActions.class.getName());
	
	String authHash;
	String jSessionId;
	String xmlResultLocation;
	Object result;
	Object response;
	String valueParam;
	String nameAttr;
	
	String proxyUser;
	String proxyPassword;
	String proxyHost;
	String proxyPort;
	
	boolean more = true;
	Integer resultCount = 0; 

	public Logger getLogger() {
		return logger;
	} 

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getXmlResultLocation() {
		return xmlResultLocation;
	}

	public void setXmlResultLocation(String xmlResultLocation) {
		this.xmlResultLocation = xmlResultLocation;
	}

	public String getAuthHash() {
		return authHash;
	}

	public void setAuthHash(String authHash) {
		this.authHash = authHash;
	}

	public String getjSessionId() {
		return jSessionId;
	}

	public void setjSessionId(String jSessionId) {
		this.jSessionId = jSessionId;
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

	public String getValueParam() {
		return valueParam;
	}

	public void setValueParam(String valueParam) {
		this.valueParam = valueParam;
	}
	
	public String getNameAttr() {
		return nameAttr;
	}

	public void setNameAttr(String nameAttr) {
		this.nameAttr = nameAttr;
	}

	public Object getResult() {
		return result;
	}
	
	public void setResult(Object result) {
		this.result = result;
	}
	
	public Object getResponse() {
		return response;
	}
	
	public void setResponse(Object response) {
		this.response = response;
	}

	public TkDataRequest(){
		authHash = "";
		jSessionId = "";
		xmlResultLocation ="";
		result = null;
	}
	
	public boolean getMore() {
		return more;
	}

	public Object getResultCount() {
		return resultCount;
	}
	
	public boolean sendData(String URL, String method, String contentType, String payload, TYPE_REPORT type) throws NumberFormatException, InterruptedException{
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		logger.finest("authHash= " + this.authHash + ", JSESSIONID= " + this.jSessionId);
		
		boolean success = false;
		int count = 1;
	    int maxTries = Integer.parseInt(TkConfigFile.parameters.get("NB_RETRY_CALL"));

		/*
		CREATE_DATAOBJECT
		ADD_TIMEPHASED_ITEM
		SAVE_TIMEPHASED_ITEM
		UPDATE_DATAOBJECT
		CHANGE_LIFECYCLE
		GET_CA_DEF
		*/
		
		if(type == TYPE_REPORT.CREATE_DATAOBJECT ||
		   type == TYPE_REPORT.ADD_TIMEPHASED_ITEM ||
		   type == TYPE_REPORT.SAVE_TIMEPHASED_DATA ||
		   type == TYPE_REPORT.UPDATE_DATAOBJECT ||
		   type == TYPE_REPORT.CHANGE_LIFECYCLE ||
		   type == TYPE_REPORT.GET_CA_DEF
		   ){
	  	  	try {
	  	  		while(true) {
				  HttpURLConnection httpConnection = null;
		          if(proxyHost.equalsIgnoreCase("") || proxyPort.equalsIgnoreCase("")){
		        	  URL restServiceURL = new URL(URL);
		        	  httpConnection = (HttpURLConnection) restServiceURL.openConnection();
		          } else{
		        	  httpConnection = proxySetup(URL);
		          }
		          
		          httpConnection.setRequestMethod(method);
		          httpConnection.setRequestProperty("Cookie","authash="+ this.authHash + "; JSESSIONID=" + this.jSessionId);
		          
		          if (method == "POST") {
		        	  httpConnection.setRequestProperty("Accept", contentType);
		        	  httpConnection.setDoOutput(true);
			          httpConnection.setDoInput(true);
			          httpConnection.setRequestProperty("Content-Type", "text"+contentType.substring(contentType.indexOf("/"))+"; charset=utf-8"); 
			          
			          logger.finest("payload: " + payload);
			            
		        	  OutputStreamWriter outputStream = new OutputStreamWriter(httpConnection.getOutputStream(), "UTF-8");
				      char[] postBytes = String.valueOf(payload).toCharArray();
				      outputStream.write(postBytes);
				      outputStream.flush();
		          }	      	
		          
		          logger.finest("httpConnection.getResponseCode(): " + httpConnection.getResponseCode() + ", " + httpConnection.getResponseMessage());
		          	          
		          if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
		        	  httpConnection.disconnect();
		        	  logger.severe("Error:	HTTP "+ method + " Request Failed with Error code : " + httpConnection.getResponseCode());	              
		          } else  {

			          BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream()), "UTF-8"));
			          String output = "";		          
			          
			          StringBuffer response = new StringBuffer();
	
			          while ((output = responseBuffer.readLine()) != null) {
			        	  logger.finest("response: " + output);
			        	  if (contentType.equalsIgnoreCase(TkDataRequest.JsonContentType)) {
			        		  success = getIdDatasFromJson(output, "0", type);
						  } else if (contentType.equalsIgnoreCase(TkDataRequest.XmlContentType)) {
							  output = output.replace(" & "," &amp; ");
							  success = getIdDatasFromXml(output, type);
						  } 
			          }
			          httpConnection.disconnect();
			          logger.finest(response.toString()); 
			          try
			          {
				          if (success == false && (
				        		  getResult().toString().contains("Server error")
					        	||
					        	  getResult().toString().contains("parallel execution is locked")
				        		||
				        		  getResult().toString().contains("USER_REPORT_ALREADY_EXECUTING")
				        		||
				        		  getResult().toString().contains("Rules Engine - Could not get Dataobject Attr.")
				        		||
				        		  getResult().toString().contains("User is allready executing a request")
				        		  )) {
				        	  if (count == maxTries) {
				        		  logger.info(stackTrace[1].getMethodName()+"KO, " + getResult().toString());
				        		  break;
			       		   	  } else {
			       		   		  count +=1;
			       		   		  Thread.sleep(5000);
			       		   	  }
				          } else if (success == false && getResult().toString().contains("Not Logged on !!!")) {
				        	  //TkActions tk = new TkActions();	
				        	  //tk.login();
				        	  more = false;
				        	  break;
			      	   	  } else {
			      	   		  	break;
			      	   	  }
			          } catch (Exception e) {
							logger.severe(e.getMessage());
							break;
					  }
		          }
	  	  		}
			} 
			catch (MalformedURLException e) {
				logger.severe(e.getMessage());
		    } 
			catch (IOException e) {
				logger.severe(e.getMessage());
		    }
		}
		return success;
	}
	
	public boolean getResultFromReport(String URL, String reportId, String reportMax, TYPE_REPORT type) throws NumberFormatException, InterruptedException{
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		logger.finest("authHash= " + this.authHash + ", JSESSIONID= " + this.jSessionId);
			
		boolean success = false;
		try {
			  int count = 1;
		      int maxTries = Integer.parseInt(TkConfigFile.parameters.get("NB_RETRY_CALL"));
		      while(true) {  
				  HttpURLConnection httpConnection = null;
		          if(proxyHost.equalsIgnoreCase("") || proxyPort.equalsIgnoreCase("")){
		        	  URL restServiceURL = new URL(URL);
		        	  httpConnection = (HttpURLConnection) restServiceURL.openConnection();
		          } else{
		        	  httpConnection = proxySetup(URL);
		          }
		          
		          httpConnection.setRequestMethod("POST");
		          httpConnection.setRequestProperty("Accept", "application/json");   
		          httpConnection.setRequestProperty("Cookie","authash="+ this.authHash + "; JSESSIONID=" + this.jSessionId);
		          httpConnection.setDoOutput(true);
		          httpConnection.setDoInput(true);
		          httpConnection.setRequestProperty("Content-Type", "text/json; charset=utf-8");		          		          
		         
		          String query = "{\"id\":0, \"params\":{ \"REPORTID\":\""+reportId+"\", \"valuesByParams\":\""+getValueParam()+"\" }, \"objects\":null}";
		          
		          logger.finest("query: " + query);
		          
			      char[] postBytes = String.valueOf(query).toCharArray();
	
		          OutputStreamWriter outputStream = new OutputStreamWriter(httpConnection.getOutputStream(), "UTF-8");
		          outputStream.write(postBytes);
		          outputStream.flush();	
		          
		          logger.finest("httpConnection.getResponseCode(): " + httpConnection.getResponseCode() + ", " + httpConnection.getResponseMessage());
		          
		          if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
		        	  logger.severe("Error:	HTTP POST Request Failed with Error code : " + httpConnection.getResponseCode());
		        	  httpConnection.disconnect();
		        	  more = false;
		          } else {
		          
			          BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream()), "UTF-8"));
			          String output = "";		          
			
			          while ((output = responseBuffer.readLine()) != null) {
			        	   logger.finest(stackTrace[1].getMethodName()+": output, " + output);
			        	   success = getIdDatasFromJson(output, reportMax, type);
			        	   if(success){
		            		   logger.finest("The XML response file is found in:" + this.xmlResultLocation);
		            	   } else {
		            		   success = false;
		            		   break;
		            	   }
			          }		          
			          httpConnection.disconnect();
			         
			          if (success == false && (
			        		  getResult().toString().contains("parallel execution is locked")
			        		||
			        		  getResult().toString().contains("USER_REPORT_ALREADY_EXECUTING")
			        		||
			        		  getResult().toString().contains("Rules Engine - Could not get Dataobject Attr.")
			        		||
			        		  getResult().toString().contains("User is allready executing a request")
			        		  )) {
			        	  if (count == maxTries) {
			        		  logger.info(stackTrace[1].getMethodName()+"KO, " + getResult().toString());
			        		  break;
		       		   	  } else {
		       		   		  count +=1;
		       		   		  Thread.sleep(Integer.parseInt(TkConfigFile.parameters.get("SLEEP_RETRY_CALL")));
		       		   	  }
			          } else if (success == false && getResult().toString().contains("Not Logged on !!!")) {
			        	  //TkActions tk = new TkActions();	
			        	  //tk.login();
			        	  more = false;
			        	  break;
		      	   	  } else {
		      	   		  	break;
		      	   	  }
		          }
		      }
		} 
		catch (MalformedURLException e) {
			logger.severe(e.getMessage());
	    } 
		catch (IOException e) {
			logger.severe(e.getMessage());
	    }
		return success;
	}
	
	public boolean getIdDatasFromJson(String output, String reportMax, TYPE_REPORT type){
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		boolean done = false;
		
		String description = "";
		String nbOfResult = "";
		
		String dataobject_id = "";
		String dataobject_name = "";
		String dataobject_parent_id = "";
		String dataobject_parent_name = "";
		String dataobject_lifecycle = "";
		//String dataobject_path = "";
		String custattrlistval_id = "";
		String timephaseditem_id = "";
		String label = "";
		String value_id = "";
		String user_id = "";
		String user_name = "";
		String id = "";
		String name = "";
		String login = "";
		String email = ""; 
		String management_entity = "";
		String sdate = "";
		String nb_wk_days ="";
		String pool = "";
		String code_generator = "";
		String inherit = "";
		
		String numAttrRate = "";
		String isAllVersionComments = "";
		String calendarId = "";
		String editable = "";
		String attrname1 = "";
		String attrname2 = "";
		String attrname3 = "";
		String attrname4 = "";
		String attrname5 = "";
		String isCurrency = "";
		String managementMode = "";
		String isMultiLine = "";
		String isHeadcount = "";
		String attr1 = "";
		String attr2 = "";
		String attr3 = "";
		String attr4 = "";
		String attr5 = "";
		String minInputLevel = "";
		String rateId = "";
		String period_id = "";
		String labellong  = "";
		
		ArrayList<String> list_ = new ArrayList<>();
		
		String keyObject = "";
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		boolean success = false;
		
		try{
			obj = (JSONObject) parser.parse(output);
			
			try{
				  success = Boolean.parseBoolean(obj.get("success").toString());
				  if (!success) {
					  try{
						  String message = obj.get("message").toString();
						  logger.severe(message);
						  setResult(message);
					  } catch(Exception e1){
						  logger.severe("unable to get message info : " + e1.getMessage());
						  try{
							  String message = obj.get("ret_i18n_code").toString();
							  logger.severe(message);
							  setResult(message);
						  } catch(Exception e2){
							  logger.severe("unable to get ret_i18n_code info : " + e2.getMessage());
						  }
					  }
				  }
				  done = success;
			} catch(Exception e){
				  logger.severe("unable to parse json response, server error : " + e.getMessage());
				  success = false;
			}
			
			if (success) {
				
				if(type == TYPE_REPORT.ADD_TIMEPHASED_ITEM) {
					JSONArray objData = null; 
					objData = (JSONArray) obj.get("data");
					if(objData != null){
		      			   for(int index=0; index<objData.size(); index++){
		      				   JSONObject jsonObject = (JSONObject) objData.get(index);
		      				   //System.out.println(jsonObject.get("timephasedItemId").toString());
		      				   setResult(jsonObject.get("timephasedItemId").toString());
						   }
		      		  }
				} else {
								
					JSONObject objData = null;	
					try{
						objData = (JSONObject) obj.get("data");	
					} catch(Exception e){}
					
					if(objData != null){
						@SuppressWarnings("unchecked")
						Set<Map.Entry<String, JSONObject>> set = objData.entrySet();
					    Iterator<Map.Entry<String, JSONObject>> iterator = set.iterator();
					    
					    while (iterator.hasNext()) {
					    	Map.Entry<String, JSONObject> entry = iterator.next();
					        String key = entry.getKey();		        
					        keyObject = key;		        		        		        	       
					    }					
					    //refreshRoleContext

						JSONObject objGetData = null;			
						JSONArray jsonArrayRes = null;
						try {
							objGetData = (JSONObject) objData.get(keyObject);			
							jsonArrayRes = (JSONArray) objGetData.get("res");
						} catch(Exception e){}
						
						if(jsonArrayRes == null){
							setResult("");
							done = true;
						}
						
						try{
							description = obj.get("description").toString();
							nbOfResult = description.substring(description.indexOf("#$#") + 3, description.indexOf(","));
		
							try{
								resultCount = (int)Double.parseDouble(nbOfResult);
							} catch(Exception e){
								resultCount=0;
							}
							
							//Check nb of record...
							//If max nob of result
							//Use LIMIT ##OFFSET##, MAX (default 1000)
							if (resultCount == (int)Double.parseDouble(reportMax)) {
								more = true;
							//Do use LIMIT
							} else {
								more = false;
							}
							
						} catch(Exception e){
							//System.out.println("nbOfResult= ERROR : " + e.getMessage());
							more = false; // STOP!!
						}
						
						/*
						GET_LIST_ATTR_VALUES,
						//GET_FIRST_DAY_OF_PERIOD,
						GET_LIST_USERS,
						GET_LIST_DAYS,
						GET_LIST_DATAOBJECTS,
						GET_LIST_OBJ_ATTR_INHERITED,
						GET_LIST_OBJ_STAGES,
						GET_OBJ_DEF,
						GET_CA_DEF,
						GET_LIST_MONTHS
						*/
						
						if(type == TYPE_REPORT.GET_LIST_ATTR_VALUES) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								custattrlistval_id = jsonObject.get("custattrlistval_id").toString();
								label = jsonObject.get("label").toString();
								value_id = jsonObject.get("value_id").toString();
								try{
									custattrlistval_id = String.valueOf((int)Double.parseDouble(custattrlistval_id));
									list_.add(label+strSeparator+custattrlistval_id+strSeparator+value_id);
								} catch(Exception e){
									custattrlistval_id="";
								}
							}
							setResult(list_);
							
							done = true;
						} else if(type == TYPE_REPORT.GET_LIST_DATAOBJECTS) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								dataobject_id = jsonObject.get("dataobject_id").toString();
								dataobject_name = jsonObject.get("dataobject_name").toString();
								dataobject_parent_id = jsonObject.get("dataobject_parent_id").toString();
								dataobject_parent_name = jsonObject.get("dataobject_parent_name").toString();
								dataobject_lifecycle = jsonObject.get("lifecycle").toString();
								try{
									dataobject_id = String.valueOf((int)Double.parseDouble(dataobject_id));
									list_.add(dataobject_name+strSeparator+dataobject_id+strSeparator+dataobject_parent_name+strSeparator+dataobject_parent_id+strSeparator+dataobject_lifecycle);
								} catch(Exception e){
									dataobject_id="";
								}
							}
							setResult(list_);
							
							done = true;
						} else if(type == TYPE_REPORT.GET_LIST_MONTHS) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								period_id = jsonObject.get("period_id").toString();
								labellong  = jsonObject.get("labellong").toString();
								
								list_.add(labellong+strSeparator+period_id);
							}
							setResult(list_);
							
							done = true;
						} else if(type == TYPE_REPORT.GET_LIST_TPA_ITEMS) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								dataobject_id = jsonObject.get("dataobject_id").toString();
								dataobject_name = jsonObject.get("dataobject_name").toString();
								timephaseditem_id = jsonObject.get("timephaseditem_id").toString();
								try{
									dataobject_id = String.valueOf((int)Double.parseDouble(dataobject_id));
									list_.add(dataobject_name+strSeparator+dataobject_id+strSeparator+timephaseditem_id);
								} catch(Exception e){
									dataobject_id="";
								}
							}
							setResult(list_);
							
							done = true;
						} else if(type == TYPE_REPORT.GET_LIST_OBJ_ATTR_INHERITED) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								id = jsonObject.get("id").toString();
								name = jsonObject.get("name").toString();
								inherit = jsonObject.get("inherit_parent").toString();
								try{
									id = String.valueOf((int)Double.parseDouble(id));
									list_.add(name+strSeparator+id+strSeparator+inherit);
								} catch(Exception e){
									id="";
								}
							}
							setResult(list_);
							
							done = true;
						} else if(type == TYPE_REPORT.GET_LIST_OBJ_STAGES) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								id = jsonObject.get("id").toString();
								name = jsonObject.get("name").toString();
								try{
									id = String.valueOf((int)Double.parseDouble(id));
									list_.add(name+strSeparator+id);
								} catch(Exception e){
									id="";
								}
							}
							setResult(list_);
							
							done = true;
						} else if(type == TYPE_REPORT.GET_LIST_USERS) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								user_id = jsonObject.get("user_id").toString();
								user_name = jsonObject.get("user_name").toString();
								login = jsonObject.get("login").toString();
								email = jsonObject.get("email").toString(); 
								management_entity = jsonObject.get("management_entity").toString();
								try{
									user_id = String.valueOf((int)Double.parseDouble(user_id));
									list_.add(user_id+strSeparator+
											  user_name+strSeparator+
											  login+strSeparator+
											  email+strSeparator+ 
											  management_entity);
								} catch(Exception e){
									user_id="";
								}
							}
							setResult(list_);
							
							done = true;	
						
						/*	
						} else if(type == TYPE_REPORT.GET_FIRST_DAY_OF_PERIOD) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								_string = jsonObject.get("startdate").toString()+strSeparator+jsonObject.get("lockedperiod").toString()+strSeparator+jsonObject.get("enddate").toString();
								list_.add(_string);
							}
							setResult(list_);
							
							done = true;*/
							
						} else if (type == TYPE_REPORT.GET_LIST_DAYS) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								sdate = jsonObject.get("sdate").toString();
								nb_wk_days  = jsonObject.get("nb_wk_days").toString();
								
								list_.add(sdate+strSeparator+nb_wk_days );
							}
							setResult(list_);
							
							done = true;	
							
						} else if (type == TYPE_REPORT.GET_OBJ_DEF) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								name = jsonObject.get("name").toString();
								pool  = jsonObject.get("pool").toString();
								code_generator  = jsonObject.get("code_generator").toString();
								
								list_.add(name+strSeparator+pool+strSeparator+code_generator);
							}
							setResult(list_);
							
							done = true;	
						
						} else if (type == TYPE_REPORT.GET_CA_DEF) {
							for(int index=0; index<jsonArrayRes.size(); index++){
								JSONObject jsonObject = (JSONObject) jsonArrayRes.get(index);
								numAttrRate = jsonObject.get("numAttrRate").toString();
								isAllVersionComments = jsonObject.get("isAllVersionComments").toString();
								calendarId = jsonObject.get("calendarId").toString();
								editable = jsonObject.get("editable").toString();
								try {attrname1 = jsonObject.get("attrname1").toString();} catch (Exception x) {};
								try {attrname2 = jsonObject.get("attrname2").toString();} catch (Exception x) {};
								try {attrname3 = jsonObject.get("attrname3").toString();} catch (Exception x) {};
								try {attrname4 = jsonObject.get("attrname4").toString();} catch (Exception x) {};
								try {attrname5 = jsonObject.get("attrname5").toString();} catch (Exception x) {};
								isCurrency = jsonObject.get("isCurrency").toString();
								managementMode = jsonObject.get("managementMode").toString();
								isMultiLine = jsonObject.get("isMultiLine").toString();
								isHeadcount = jsonObject.get("isHeadcount").toString();
								try {attr1 = jsonObject.get("attr1").toString();} catch (Exception x) {};
								try {attr2 = jsonObject.get("attr2").toString();} catch (Exception x) {};
								try {attr3 = jsonObject.get("attr3").toString();} catch (Exception x) {};
								try {attr4 = jsonObject.get("attr4").toString();} catch (Exception x) {};
								try {attr5 = jsonObject.get("attr5").toString();} catch (Exception x) {};
								minInputLevel = jsonObject.get("minInputLevel").toString();
								rateId= jsonObject.get("rateId").toString();
								
								list_.add(numAttrRate+strSeparator+
											isAllVersionComments+strSeparator+
											calendarId+strSeparator+
											editable+strSeparator+
											attrname1+strSeparator+
											attrname2+strSeparator+
											attrname3+strSeparator+
											attrname4+strSeparator+
											attrname5+strSeparator+
											isCurrency+strSeparator+
											managementMode+strSeparator+
											isMultiLine+strSeparator+
											isHeadcount+strSeparator+
											attr1+strSeparator+
											attr2+strSeparator+
											attr3+strSeparator+
											attr4+strSeparator+
											attr5+strSeparator+
											minInputLevel+strSeparator+
											rateId);
							}
							setResult(list_);
							
							done = true;	
						
						} else  {
							setResult(null);
						}
					}
				} 
			}					
		} catch(Exception e){
			logger.severe(e.getMessage());
		}
		
		return done;
	}
	
	public boolean getIdDatasFromXml(String output, TYPE_REPORT type){
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		boolean done = false;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;	
		InputSource is;
      
		try {
			builder = factory.newDocumentBuilder();
            is = new InputSource(new StringReader(output));
            Document doc = builder.parse(is);
            if(type == TYPE_REPORT.SEARCH_USER) {
            	NodeList users = doc.getElementsByTagName("identifier");
  			  	try {
  			  		String key = users.item(0).getTextContent().toString();
        			if (key != "") {
        				TkActions.userIdentifier = key;
        				done = true;
        			} 
  			  	}  catch(Exception e){
  			  		logger.finest("Error search user, " + e.getMessage());
  			  	}
            } else { 
            	if (type == TYPE_REPORT.CREATE_TIMESHEET || type == TYPE_REPORT.CREATE_USER) {
	            	try {
	            		NodeList code = doc.getElementsByTagName("code");
			            NodeList result = doc.getElementsByTagName("result");
			            NodeList error = doc.getElementsByTagName("error");
			            NodeList timesheetid = doc.getElementsByTagName("timesheetid");
	
			            for(int i = 0; i < result.getLength(); i++) {
				            if (result.item(i).getTextContent().toString().equalsIgnoreCase("KO")) {
				            	if (type == TYPE_REPORT.CREATE_TIMESHEET) {
				            		logger.finest("Error timesheet creation, " + error.item(i).getTextContent());
				            		setResult(result.item(i).getTextContent().toString() + " - " + timesheetid.item(i).getTextContent());
				            	} else {
				            		logger.finest("Error user creation, " + error.item(i).getTextContent() + " : " + code.item(i).getTextContent());
				            		setResult(result.item(i).getTextContent().toString() + " - " + error.item(i).getTextContent() + " : " + code.item(i).getTextContent());
				            	}
				            } else {
				            	setResult(result.item(i).getTextContent().toString());
	
				            	done = true;
				            }
			            }  
	  				} catch(Exception e){
	  					logger.finest("Error user/timesheet creation, " + e.getMessage());
	  				} 
            	} else if (type == TYPE_REPORT.CREATE_DATAOBJECT) {
            		NodeList name = doc.getElementsByTagName("name");
		            NodeList result = doc.getElementsByTagName("result");
		            NodeList error = doc.getElementsByTagName("error");
		            NodeList dataobjectid = doc.getElementsByTagName("dataobjectid");
		            NodeList name_generated = doc.getElementsByTagName("name_generated");
		            try {
			            if(error.item(0).getTextContent().toString().contains("Server error")) {
			            	setResult("Server error");
			            } 
	  				} catch(Exception e){
	  					logger.finest("Error dataobject creation, " + e.getMessage());
	  				} 
		            for(int i = 0; i < result.getLength(); i++) {
			            if (result.item(i).getTextContent().toString().equalsIgnoreCase("KO")) {
			            	logger.finest("Error dataobject creation, " + error.item(i).getTextContent());
			            	setResult(result.item(i).getTextContent().toString());
			            	
			            } else {
			            	setResult(result.item(i).getTextContent().toString());
			            	TkActions.dataObjectId = dataobjectid.item(0).getTextContent().toString();
			            	TkActions.dataObjectName = name.item(0).getTextContent().toString();
			            	TkActions.name_generated = name_generated.item(0).getTextContent().toString();

			            	done = true;
			            }
		            }
            	}
            }
        } catch (ParserConfigurationException e) {
        	logger.finest("unable to parse xml response, server error");
        } catch (SAXException e) {
        } catch (IOException e) {
        }
		
		return done;
	}
	
	public boolean readDataObjectFromRest(String URL, String dataobjectId){
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
			
			boolean tkSendTimeState = false;
			try {
		          
				  HttpURLConnection httpConnection = null;
		          if(this.proxyHost.equalsIgnoreCase("") || this.proxyPort.equalsIgnoreCase("")){
		        	  URL restServiceURL = new URL(URL);
		        	  httpConnection = (HttpURLConnection) restServiceURL.openConnection();
		          } else{
		        	  httpConnection = proxySetup(URL);
		          }
		          
		          httpConnection.setRequestMethod("POST");
		          httpConnection.setRequestProperty("Accept", "application/json");   
		          httpConnection.setRequestProperty("Cookie","authash="+ this.authHash + "; JSESSIONID=" + this.jSessionId);
		          httpConnection.setDoOutput(true);
		          httpConnection.setDoInput(true);
		          httpConnection.setRequestProperty("Content-Type", "text/json; charset=utf-8");		          		          
		         
		          String query = "{\"id\":0, \"params\":{ \"dataobjectId\":\""+dataobjectId+"\"}, \"objects\":null}";
		          	          
		          logger.finest("query: " + query);	          
		          
			      char[] postBytes = String.valueOf(query).toCharArray();

		          OutputStreamWriter outputStream = new OutputStreamWriter(httpConnection.getOutputStream(), "UTF-8");
		          outputStream.write(postBytes);
		          outputStream.flush();	
		          
		          logger.finest("httpConnection.getResponseCode(): " + httpConnection.getResponseCode() + ", " + httpConnection.getResponseMessage());
		           	           		          	          
		          if (httpConnection.getResponseCode() != 200) {
		        	  logger.finest("Error:	HTTP POST Request Failed with Error code : " + httpConnection.getResponseCode());	              
		          }
		          tkSendTimeState = true;
		          BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream()), "UTF-8"));
		          String output = "";		          
		
		          logger.finest("sendDataGetObjectDef: Ok, " + tkSendTimeState);
		          while ((output = responseBuffer.readLine()) != null) {
	            	   setResponse(output);
		        	  //logger.finest("response:" + output);
		        	  
		         }		          
		         httpConnection.disconnect();
			} 
			catch (MalformedURLException e) {
		        logger.severe(e.getMessage());
		    } 
			catch (IOException e) {
		         logger.severe(e.getMessage());
		    }
			return tkSendTimeState;
		}

	public boolean LoadCustomAttrValuesByType(String URL, String attrId, String typecomboid) throws NumberFormatException, InterruptedException{
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
			
		boolean success = false;
		try {
			  int count = 1;
		      int maxTries = Integer.parseInt(TkConfigFile.parameters.get("NB_RETRY_CALL"));
		      while(true) { 
		          HttpURLConnection httpConnection = null;
		          if(this.proxyHost.equalsIgnoreCase("") || this.proxyPort.equalsIgnoreCase("")){
		        	  URL restServiceURL = new URL(URL);
		        	  httpConnection = (HttpURLConnection) restServiceURL.openConnection();
		          } else{
		        	  httpConnection = proxySetup(URL);
		          }
		          
		          httpConnection.setRequestMethod("POST");
		          httpConnection.setRequestProperty("Accept", "application/json");   
		          httpConnection.setRequestProperty("Cookie","authash="+ this.authHash + "; JSESSIONID=" + this.jSessionId);
		          httpConnection.setDoOutput(true);
		          httpConnection.setDoInput(true);
		          httpConnection.setRequestProperty("Content-Type", "text/json; charset=utf-8");		          		          
		         
		          String query = "{\"id\":0, \"params\":{ \"dataobjectId\":\""+attrId+"\" }, \"typecomboid\":\""+typecomboid+"\" \"objects\":null}";
		          	          
		          logger.finest("query: " + query);	          
		          
			      char[] postBytes = String.valueOf(query).toCharArray();

		          OutputStreamWriter outputStream = new OutputStreamWriter(httpConnection.getOutputStream(), "UTF-8");
		          outputStream.write(postBytes);
		          outputStream.flush();	
		          
		          logger.finest("httpConnection.getResponseCode(): " + httpConnection.getResponseCode() + ", " + httpConnection.getResponseMessage());
		           	           		          	          
		          if (httpConnection.getResponseCode() != 200) {
		        	  logger.finest("Error:	HTTP POST Request Failed with Error code : " + httpConnection.getResponseCode());	              
		          }

		          BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream()), "UTF-8"));
		          String output = "";		          
		
		          while ((output = responseBuffer.readLine()) != null) {
		        	   logger.finest(stackTrace[1].getMethodName()+": output, " + output);
		        	   success = getIdDatasFromJson(output, "1", TYPE_REPORT.GET_CA_DEF);
		        	   if(success){
	            		   
	            	   } else {
	            		   success = false;
	            		   break;
	            	   }
		          }			          
		          httpConnection.disconnect();
		          
		          if (success == false && (
		        		  getResult().toString().contains("parallel execution is locked")
		        		||
		        		  getResult().toString().contains("USER_REPORT_ALREADY_EXECUTING")
		        		||
		        		  getResult().toString().contains("User is allready executing a request")
		        		  )) {
		        	  if (count == maxTries) {
		        		  logger.info(stackTrace[1].getMethodName()+"KO, " + getResult().toString());
		        		  break;
	       		   	  } else {
	       		   		  count +=1;
	       		   		  Thread.sleep(Integer.parseInt(TkConfigFile.parameters.get("SLEEP_RETRY_CALL")));
	       		   	  }
		          } else if (success == false && getResult().toString().contains("Not Logged on !!!")) {
		        	  more = false;
		        	  break;
	      	   	  } else {
	      	   		  	break;
	      	   	  }
		      	}
			} 
			catch (MalformedURLException e) {
		        logger.severe(e.getMessage());
		    } 
			catch (IOException e) {
		         logger.severe(e.getMessage());
		    }
			return success;
		}
	
	public HttpURLConnection proxySetup(String urlInput){
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
        URL url;
        try {
            url = new URL(urlInput);
            
            int port = 80;            
            try{
            	port = Integer.parseInt(proxyPort);
            } catch(Exception e){
            	
            }

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, port)); // or whatever your proxy is
            HttpURLConnection uc = (HttpURLConnection)url.openConnection(proxy);
            
            logger.finest(stackTrace[1].getMethodName()+" : proxySetup host " + proxyHost);
            logger.finest(stackTrace[1].getMethodName()+" : proxySetup port " + proxyPort);
            
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", proxyPort);
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", proxyPort);
            String encoded = "";
            try{
            	encoded = new String(DatatypeConverter.printBase64Binary((proxyUser + ":" + proxyPassword).getBytes()));
            } catch(Exception e){
            	
            }

            logger.finest(stackTrace[1].getMethodName()+" - proxySetup username " + proxyUser);
            logger.finest(stackTrace[1].getMethodName()+" - proxySetup password " + proxyPassword);
            
            uc.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
            Authenticator.setDefault(new ProxyAuthenticator(proxyUser, proxyPassword));

            logger.info(stackTrace[1].getMethodName()+" : proxySetup success");
            return uc;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.severe(stackTrace[1].getMethodName()+" : proxySetup - Failed"+e.getMessage());
        }
        return null;
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

