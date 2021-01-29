package eu.triskell.client.sicav;

import java.util.ArrayList;
import java.util.logging.Logger;

import eu.triskell.client.sicav.TkAuthenticator;
import eu.triskell.client.sicav.TkConfigFile;
import eu.triskell.client.sicav.TkDataRequest;
import eu.triskell.client.sicav.TkConfigFile.TYPE_REPORT;

/**
 * @author vcailleaud
 *
 */
public class TkActions {

	/**
	 * @param args
	 */
	
	public static TkAuthenticator tkAuth;
	public static TkDataRequest tkRequest;
	
	public static String dataObjectDetails = null;
	
	public static ArrayList<String> listDataobjects = new ArrayList<>();
	
	static Logger logger=Logger.getLogger(TkActions.class.getName());
	
	Object result;
	
	public static String objectName = "";
	public static String codeGenerator = "";
	public static String defaultStage = "";
	public static String currency = "";
	public static String pool = "";
	public static String createParentRel = "";
	public static String userIdentifier = "";
	public static String dataObjectId = "";
	public static String name_generated = "";
	//public static String cresult = "";
	public static String dataObjectName = "";
	
	public Object getResult() {
		return result;
	}
	
	public void setResult(Object result) {
		this.result = result;
	}
	
	public Logger getLogger() {
		return logger;
	} 

	public void setLogger(Logger logger) {
		TkActions.logger = logger;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> GetTkAttrListValues(String reportId, String attrId, TYPE_REPORT type) throws NumberFormatException, InterruptedException {		
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		ArrayList<String> list_result = new ArrayList<>();
		ArrayList<String> list_tmp = new ArrayList<>();
		
		String reportMax = "";
		String valueParam= "";
		
		Integer nb = 0;
					
		boolean limitUsage = false;
		
		boolean bResult = false;
			
		String wsGetReportToPanel = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsReportDataToPanelURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());
		
		if ( reportId.indexOf(",") > 0) {
			reportMax = reportId.split(",")[1];
			reportId = reportId.split(",")[0];
			limitUsage = true;
		} else {
			limitUsage = false; 
		}
		
		//Until records to proceed (isTruncatedResult is True)
		while (tkRequest.getMore()) {
				
			if (limitUsage) {
				valueParam = TkConfigFile.parameters.get("ID_PARAM_OFFSET") + "#" + String.valueOf(nb) + "##" + 
							 TkConfigFile.parameters.get("ID_PARAM_COUNT") + "#" + reportMax + "##";
			} else {
				valueParam = "";
			}
			
			valueParam = valueParam + TkConfigFile.parameters.get("ID_PARAM_ID")+"#"+attrId;
							
			tkRequest.setValueParam(valueParam);

			bResult = tkRequest.getResultFromReport(wsGetReportToPanel, reportId, reportMax, type);
			
			if(!bResult){			
				logger.finest("Erreur: "+stackTrace[1].getMethodName());
				break;
			} else {
				list_tmp = (ArrayList<String>) tkRequest.getResult();
				
				list_result.addAll(list_tmp);
				
				nb = nb + list_tmp.size();
         	}
		}
		
		if(!bResult){			
			logger.finest("Erreur: "+stackTrace[1].getMethodName());
		} 
			
		return list_result;
	}	
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> GetDataobjectList(String reportId, String objectId, TYPE_REPORT type) throws NumberFormatException, InterruptedException {		
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		ArrayList<String> list_result = new ArrayList<>();
		ArrayList<String> list_tmp = new ArrayList<>();
		
		String reportMax = "";
		String valueParam= "";
		
		Integer nb = 0;
					
		boolean limitUsage = false;
		
		boolean bResult = false;
			
		String wsGetReportToPanel = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsReportDataToPanelURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());
		
		if ( reportId.indexOf(",") > 0) {
			reportMax = reportId.split(",")[1];
			reportId = reportId.split(",")[0];
			limitUsage = true;
		} else {
			limitUsage = false; 
		}
		
		//Until records to proceed (isTruncatedResult is True)
		while (tkRequest.getMore()) {
				
			if (limitUsage) {
				valueParam = TkConfigFile.parameters.get("ID_PARAM_OFFSET") + "#" + String.valueOf(nb) + "##" + 
							 TkConfigFile.parameters.get("ID_PARAM_COUNT") + "#" + reportMax + "##";
			} else {
				valueParam = "";
			}
			
			valueParam = valueParam + TkConfigFile.parameters.get("ID_PARAM_ID")+"#"+objectId;
							
			tkRequest.setValueParam(valueParam);

			bResult = tkRequest.getResultFromReport(wsGetReportToPanel, reportId, reportMax, type);
			
			if(!bResult){			
				logger.finest("Erreur: "+stackTrace[1].getMethodName());
				break;
			} else {
				list_tmp = (ArrayList<String>) tkRequest.getResult();
				
				list_result.addAll(list_tmp);
				
				nb = nb + list_tmp.size();
         	}
		}
		
		if(!bResult){			
			logger.finest("Erreur: "+stackTrace[1].getMethodName());
		} 
			
		return list_result;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> GetTimephasedItemList(String reportId, String attrId, String versionId, String attrValId, TYPE_REPORT type) throws NumberFormatException, InterruptedException {		
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		ArrayList<String> list_result = new ArrayList<>();
		ArrayList<String> list_tmp = new ArrayList<>();
		
		String reportMax = "";
		String valueParam= "";
		
		Integer nb = 0;
					
		boolean limitUsage = false;
		
		boolean bResult = false;
			
		String wsGetReportToPanel = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsReportDataToPanelURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());
		
		if ( reportId.indexOf(",") > 0) {
			reportMax = reportId.split(",")[1];
			reportId = reportId.split(",")[0];
			limitUsage = true;
		} else {
			limitUsage = false; 
		}
		
		//Until records to proceed (isTruncatedResult is True)
		while (tkRequest.getMore()) {
				
			if (limitUsage) {
				valueParam = TkConfigFile.parameters.get("ID_PARAM_OFFSET") + "#" + String.valueOf(nb) + "##" + 
							 TkConfigFile.parameters.get("ID_PARAM_COUNT") + "#" + reportMax + "##";
			} else {
				valueParam = "";
			}
			
			valueParam = valueParam + TkConfigFile.parameters.get("ID_PARAM_ATTR_ID")+"#"+attrId + "##" +
									  TkConfigFile.parameters.get("ID_PARAM_VERSION_ID")+"#"+versionId + "##" +
									  TkConfigFile.parameters.get("ID_PARAM_ATTR_VAL_ID")+"#"+attrValId;
							
			tkRequest.setValueParam(valueParam);

			bResult = tkRequest.getResultFromReport(wsGetReportToPanel, reportId, reportMax, type);
			
			if(!bResult){			
				logger.finest("Erreur: "+stackTrace[1].getMethodName());
				break;
			} else {
				list_tmp = (ArrayList<String>) tkRequest.getResult();
				
				list_result.addAll(list_tmp);
				
				nb = nb + list_tmp.size();
         	}
		}
		
		if(!bResult){			
			logger.finest("Erreur: "+stackTrace[1].getMethodName());
		} 
			
		return list_result;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> GetUserList(String reportId, String str, TYPE_REPORT type) throws NumberFormatException, InterruptedException {		
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		ArrayList<String> list_result = new ArrayList<>();
		ArrayList<String> list_tmp = new ArrayList<>();
		
		String reportMax = "";
		String valueParam= "";
		
		Integer nb = 0;
					
		boolean limitUsage = false;
		
		boolean bResult = false;
			
		String wsGetReportToPanel = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsReportDataToPanelURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());
		
		if ( reportId.indexOf(",") > 0) {
			reportMax = reportId.split(",")[1];
			reportId = reportId.split(",")[0];
			limitUsage = true;
		} else {
			limitUsage = false; 
		}
		
		//Until records to proceed (isTruncatedResult is True)
		while (tkRequest.getMore()) {
				
			if (limitUsage) {
				valueParam = TkConfigFile.parameters.get("ID_PARAM_OFFSET") + "#" + String.valueOf(nb) + "##" + 
							 TkConfigFile.parameters.get("ID_PARAM_COUNT") + "#" + reportMax + "##";
			} else {
				valueParam = "";
			}
			
			valueParam = valueParam + TkConfigFile.parameters.get("ID_PARAM_FILTER")+"#"+str;
							
			tkRequest.setValueParam(valueParam);

			bResult = tkRequest.getResultFromReport(wsGetReportToPanel, reportId, reportMax, type);
			
			if(!bResult){			
				logger.finest("Erreur: "+stackTrace[1].getMethodName());
				break;
			} else {
				list_tmp = (ArrayList<String>) tkRequest.getResult();
				
				list_result.addAll(list_tmp);
				
				nb = nb + list_tmp.size();
         	}
		}
		
		if(!bResult){			
			logger.finest("Erreur: "+stackTrace[1].getMethodName());
		} 
			
		return list_result;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> GetObjectDetails(String reportId, String objectId, TYPE_REPORT type) throws NumberFormatException, InterruptedException {
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		ArrayList<String> list_result = new ArrayList<>();
		ArrayList<String> list_tmp = new ArrayList<>();
		
		String reportMax = "";
		String valueParam= "";
		
		Integer nb = 0;
					
		boolean limitUsage = false;
		
		boolean bResult = false;
			
		String wsGetReportToPanel = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsReportDataToPanelURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());
		
		if ( reportId.indexOf(",") > 0) {
			reportMax = reportId.split(",")[1];
			reportId = reportId.split(",")[0];
			limitUsage = true;
		} else {
			limitUsage = false; 
		}
		
		//Until records to proceed (isTruncatedResult is True)
		while (tkRequest.getMore()) {
				
			if (limitUsage) {
				valueParam = TkConfigFile.parameters.get("ID_PARAM_OFFSET") + "#" + String.valueOf(nb) + "##" + 
							 TkConfigFile.parameters.get("ID_PARAM_COUNT") + "#" + reportMax + "##";
			} else {
				valueParam = "";
			}
			
			valueParam = valueParam + TkConfigFile.parameters.get("ID_PARAM_ID")+"#"+objectId;
							
			tkRequest.setValueParam(valueParam);

			bResult = tkRequest.getResultFromReport(wsGetReportToPanel, reportId, reportMax, type);
			
			if(!bResult){			
				logger.finest("Erreur: "+stackTrace[1].getMethodName());
				break;
			} else {
				list_tmp = (ArrayList<String>) tkRequest.getResult();
				
				list_result.addAll(list_tmp);
				
				nb = nb + list_tmp.size();
         	}
		}
		
		if(!bResult){			
			logger.finest("Erreur: "+stackTrace[1].getMethodName());
		} 
			
		return list_result;

	}
	
	public static String GetDataObjectDetails(String dataObjectId) {		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");	
		
		boolean bResult = false;
		String dataObjectDetails =null;
				
		String wsReadDataObjectservice = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsReadDataObjectservice;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());		

		bResult = tkRequest.readDataObjectFromRest(wsReadDataObjectservice, dataObjectId);
		if(bResult){
			dataObjectDetails =(String) tkRequest.getResponse();
			
			TkActions.dataObjectDetails = (String) tkRequest.getResult();
		}
		
		return dataObjectDetails;
	}
	
	public String GetCustomAttributDetails(String attrId, String typecomboid) throws NumberFormatException, InterruptedException {		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");	
		
		boolean bResult = false;
		String CustomAttributDetails =null;
				
		String wsCustomAttributDetails = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsLoadCustomAttrValuesByTypeURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());		

		bResult = tkRequest.LoadCustomAttrValuesByType(wsCustomAttributDetails, attrId, typecomboid);
		if(bResult){
			CustomAttributDetails =(String) tkRequest.getResponse();
		}
		
		return CustomAttributDetails;
	}
	
	public boolean CreateDataObject(String payload, TYPE_REPORT type) throws NumberFormatException, InterruptedException {		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");		
		boolean bResult = false;
	
		String wsCreateObjectURL = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsCreateDataObjectURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());						

		bResult = tkRequest.sendData(wsCreateObjectURL, "POST", TkDataRequest.XmlContentType, payload, type);
		
		if(!bResult){			
			logger.finest("Erreur: "+stackTrace[1].getMethodName());
			setResult(tkRequest.getResult());
		} else {
			setResult(TkActions.dataObjectId);
		}

		return bResult;
	}
	
	public boolean ChangeLifecycle(String payload, TYPE_REPORT type) throws NumberFormatException, InterruptedException {		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");		
		boolean bResult = false;
				
		String wsCreateObjectURL = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsChangeLifecycleAttrURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());						

		bResult = tkRequest.sendData(wsCreateObjectURL, "POST", TkDataRequest.JsonContentType, payload, type);
		
		if(!bResult){			
			logger.finest("Erreur: ChangeLifecycle");
		} 
			
		return bResult;
	}
	
	public boolean updateDataobject(String dataobject_id_str, String params) throws NumberFormatException, InterruptedException {		

		// getStackTrace() method return 
        // current method name at 0th index 
        String nameofCurrMethod = new Throwable() 
                                      .getStackTrace()[0] 
                                      .getMethodName(); 

		logger.finest(nameofCurrMethod+": start");
		
		boolean bResult = false;
	    		
		String payload = "{\"id\":0, \"params\":{ "+
							"\"DATAOBJECT_ID\":\""+dataobject_id_str+"\","+
							params +
						 "}, \"objects\":null}";
		
		logger.finest(payload);
			
		 /* {
			  'id' : 0, 
			  'params' : {
			    "DATAOBJECT_ID":"1064", 
			    "attr_476": "1",
			    "attr_350": "3",
			    "attr_346": "1",
			    "attr_436": "L'objectif est de mettre à jour les attributs",
			    "attr_351": "2",
			    "attr_169": "2018.01.01",
			    "attr_170": "2018.12.31",
			    "attr_172": "37"
			  } , 
			  'objects' : null 
			}
		*/
				
		String sURL = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsUpdateAttrURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());						

		bResult = tkRequest.sendData(sURL, "POST", TkDataRequest.JsonContentType, payload, TYPE_REPORT.UPDATE_DATAOBJECT);
		
		if(!bResult){			
			logger.finest(nameofCurrMethod+": error");
		}

		return bResult;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> GetTkOpenDaysPeriod(String reportId, 
												 String startDate,
												 String endDate,
												 TYPE_REPORT type) throws NumberFormatException, InterruptedException {		
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		ArrayList<String> list_result = new ArrayList<>();
		ArrayList<String> list_tmp = new ArrayList<>();
		
		String reportMax = "";
		String valueParam= "";
		
		Integer nb = 0;
					
		boolean limitUsage = false;
		
		boolean bResult = false;
			
		String wsGetReportToPanel = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsReportDataToPanelURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());
		
		if ( reportId.indexOf(",") > 0) {
			reportMax = reportId.split(",")[1];
			reportId = reportId.split(",")[0];
			limitUsage = true;
		} else {
			limitUsage = false; 
		}
		
		//Until records to proceed (isTruncatedResult is True)
		while (tkRequest.getMore()) {
				
			if (limitUsage) {
				valueParam = TkConfigFile.parameters.get("ID_PARAM_OFFSET") + "#" + String.valueOf(nb) + "##" + 
							 TkConfigFile.parameters.get("ID_PARAM_COUNT") + "#" + reportMax + "##";
			} else {
				valueParam = "";
			}
			
			valueParam = valueParam + TkConfigFile.parameters.get("ID_PARAM_STARTDATE")+"#"+startDate + "##" +
					  				  TkConfigFile.parameters.get("ID_PARAM_ENDDATE")+"#"+endDate;
							
			tkRequest.setValueParam(valueParam);

			bResult = tkRequest.getResultFromReport(wsGetReportToPanel, reportId, reportMax, type);
			
			if(!bResult){			
				logger.finest("Erreur: "+stackTrace[1].getMethodName());
				break;
			} else {
				list_tmp = (ArrayList<String>) tkRequest.getResult();
				
				list_result.addAll(list_tmp);
				
				nb = nb + list_tmp.size();
         	}
		}
		
		if(!bResult){			
			logger.finest("Erreur: "+stackTrace[1].getMethodName());
		} 
			
		return list_result;
	}	
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> GetTkFirstMonth (String reportId, 
											  String calendar_id,
											  String period_type_id,
											  TYPE_REPORT type) throws NumberFormatException, InterruptedException {		
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		ArrayList<String> list_result = new ArrayList<>();
		ArrayList<String> list_tmp = new ArrayList<>();
		
		String reportMax = "";
		String valueParam= "";
		
		Integer nb = 0;
					
		boolean limitUsage = false;
		
		boolean bResult = false;
			
		String wsGetReportToPanel = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsReportDataToPanelURL;
		tkRequest = new TkDataRequest();
		tkRequest.setLogger(logger);
		tkRequest.setAuthHash(tkAuth.getAuthHash());
		tkRequest.setjSessionId(tkAuth.getjSessionId());																			
		tkRequest.setProxyUser(tkAuth.getProxyUser());
		tkRequest.setProxyPassword(tkAuth.getProxyPassword());
		tkRequest.setProxyHost(tkAuth.getProxyHost());
		tkRequest.setProxyPort(tkAuth.getProxyPort());
		
		if ( reportId.indexOf(",") > 0) {
			reportMax = reportId.split(",")[1];
			reportId = reportId.split(",")[0];
			limitUsage = true;
		} else {
			limitUsage = false; 
		}
		
		//Until records to proceed (isTruncatedResult is True)
		while (tkRequest.getMore()) {
				
			if (limitUsage) {
				valueParam = TkConfigFile.parameters.get("ID_PARAM_OFFSET") + "#" + String.valueOf(nb) + "##" + 
							 TkConfigFile.parameters.get("ID_PARAM_COUNT") + "#" + reportMax + "##";
			} else {
				valueParam = "";
			}
			
			valueParam = valueParam + TkConfigFile.parameters.get("ID_PARAM_CALENDAR")+"#"+calendar_id + "##" +
									  TkConfigFile.parameters.get("ID_PARAM_PERIOD_TYPE")+"#"+period_type_id;
							
			tkRequest.setValueParam(valueParam);

			bResult = tkRequest.getResultFromReport(wsGetReportToPanel, reportId, reportMax, type);
			
			if(!bResult){			
				logger.finest("Erreur: "+stackTrace[1].getMethodName());
				break;
			} else {
				list_tmp = (ArrayList<String>) tkRequest.getResult();
				
				list_result.addAll(list_tmp);
				
				nb = nb + list_tmp.size();
         	}
		}
		
		if(!bResult){			
			logger.finest("Erreur: "+stackTrace[1].getMethodName());
		} 
			
		return list_result;
	}
	
	public boolean AddTimephasedItem(String dataobject_id, 
									 String attr_id, 
									 String version_id,
									 String _attr_id,
									 String _attr_value_id) throws NumberFormatException, InterruptedException {		

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		boolean bResult = false;
		
		String payload = "{\"id\":0, \"params\":{ "+
											"\"dataObjectId\":\""+dataobject_id+"\","+
											"\"attrId\":\""+attr_id+"\","+
											"\"versionId\":\""+version_id+"\","+
											"\"ATTRID_"+_attr_id+"\":\""+_attr_value_id+"\""+
										"}, \"objects\":null}";
		
			/* {
			  'id' : 0  , 
			  'params' : {
			    "dataObjectId":"xxx", 
			    "attrId":"482",       -- Budget MD
			    "versionId": "1",     -- Version capacity
			    "ATTRID_474" : "28",  -- Profile
			    "ATTRID_476" : "30",  -- Cost type
			    "ATTRID_475" : "30",  -- Category
			    "ATTRID_481" : "30"   -- Cost center
			  } , 
			  'objects' : null 
			}
			*/
			
			String sURL = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsAddTimephasedItemURL;
			tkRequest = new TkDataRequest();
			tkRequest.setLogger(logger);
			tkRequest.setAuthHash(tkAuth.getAuthHash());
			tkRequest.setjSessionId(tkAuth.getjSessionId());																			
			tkRequest.setProxyUser(tkAuth.getProxyUser());
			tkRequest.setProxyPassword(tkAuth.getProxyPassword());
			tkRequest.setProxyHost(tkAuth.getProxyHost());
			tkRequest.setProxyPort(tkAuth.getProxyPort());						
			
			bResult = tkRequest.sendData(sURL, "POST", TkDataRequest.JsonContentType, payload, TYPE_REPORT.ADD_TIMEPHASED_ITEM);
			
			if(!bResult){			
				logger.finest("Erreur: "+stackTrace[1].getMethodName());
			}
			setResult(tkRequest.getResult());
		
		return bResult;
		}
	
	public boolean SaveTimephasedData(String dataobject_id, 
									 String attr_id, 
									 String version_id,
									 String timephased_item_id,
									 String period_id,
									 String units,
									 String currency_id) throws NumberFormatException, InterruptedException {		

		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		boolean bResult = false;
		
		String payload = "{\"id\":0, \"params\":{ "+
											"\"dataObjectId\":\""+dataobject_id+"\","+
											"\"attrId\":\""+attr_id+"\","+
											"\"versionId\":\""+version_id+"\","+
											"\"ID\":\""+timephased_item_id+"\","+
											"\"PERIODID\":\""+period_id+"\","+
											"\"UNITS\":\""+units+"\","+
											"\"currencyId\":\""+currency_id+"\""+
										"}, \"objects\":null}";
		
			/* {
				  'id' : 0  , 
				  'params' : {
				    "dataObjectId":"3876",
				    "attrId":"626",
				    "versionId": "19",
				    "ID" : "3619",
				    "PERIODID": "25639",
				    "UNITS": "100000",
				    "currencyId" : "6"
				  } , 
				  'objects' : null 
				}
			*/
			
			String sURL = TkConfigFile.parameters.get("serverName") + TkDataRequest.wsSaveTimephasedDataURL;
			tkRequest = new TkDataRequest();
			tkRequest.setLogger(logger);
			tkRequest.setAuthHash(tkAuth.getAuthHash());
			tkRequest.setjSessionId(tkAuth.getjSessionId());																			
			tkRequest.setProxyUser(tkAuth.getProxyUser());
			tkRequest.setProxyPassword(tkAuth.getProxyPassword());
			tkRequest.setProxyHost(tkAuth.getProxyHost());
			tkRequest.setProxyPort(tkAuth.getProxyPort());						
			
			bResult = tkRequest.sendData(sURL, "POST", TkDataRequest.JsonContentType, payload, TYPE_REPORT.SAVE_TIMEPHASED_DATA);
			
			if(!bResult){			
				logger.finest("Erreur: "+stackTrace[1].getMethodName());
			}
		
		return bResult;
		}


	public boolean login() {		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		logger.finest(stackTrace[1].getMethodName()+": start");
		
		boolean authOK = false;
		boolean bResult = false;
		
		/* Anthentication request by using login and password
		 * provided in the config properties file.
		*/					
		tkAuth = new TkAuthenticator();	
		tkAuth.setLogger(logger);
			
		tkAuth.setProxyUser(TkConfigFile.parameters.get("proxyUser"));
		tkAuth.setProxyPassword(TkConfigFile.parameters.get("proxyPassword"));
		tkAuth.setProxyHost(TkConfigFile.parameters.get("proxyHost"));
		tkAuth.setProxyPort(TkConfigFile.parameters.get("proxyPort"));
			
		try{
			String aunthenticatorURL = TkConfigFile.parameters.get("serverName") + TkDataRequest.aunthenticatorURL + TkConfigFile.parameters.get("login") + "/passwd/";
			if(!TkConfigFile.parameters.get("password_hash").isEmpty()){
				aunthenticatorURL = aunthenticatorURL + TkConfigFile.parameters.get("password_hash");
			} else{
				aunthenticatorURL = aunthenticatorURL + tkAuth.convertPasswordHash(TkConfigFile.parameters.get("password_crypto"), TkConfigFile.parameters.get("password_non_hash"));
			}
			authOK = tkAuth.authenticatorRequest(aunthenticatorURL);
		} catch(Exception e){
			logger.severe(e.getMessage());
		}
			
		if(authOK){
			logger.info("log in successfull");
			bResult = true;
		}
		else{
			logger.finest("log in error");
		}
		return bResult;
	}

	public boolean logout() {
		/*
		* Log out after everything is done.	
		*/
		boolean bResult = false;
		try{
			tkAuth.authenticatorLogout(TkConfigFile.parameters.get("serverName") + TkDataRequest.aunthenticatorLogOutURL);
			logger.info("log out successfull");
			bResult = true;
		} catch(Exception e){
			logger.severe("log out error");
			logger.severe(e.getMessage());
		}
		return bResult;
	}
}


