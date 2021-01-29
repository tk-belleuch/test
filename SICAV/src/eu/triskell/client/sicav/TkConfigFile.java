package eu.triskell.client.sicav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import eu.triskell.client.sicav.import_SIVA_VNF_Main;

/**
 * @author vcailleaud
 *
 */
public class TkConfigFile {
	
	/**
	 * variable path
	 */
	public static String PATH_PROPERTIES = "";
	
	public static String serverURL = "";
		
	public static enum TYPE_REPORT {
		GET_LIST_ATTR_VALUES,
		GET_LIST_DATAOBJECTS,
		//GET_FIRST_DAY_OF_PERIOD,
		GET_LIST_TPA_ITEMS,
		GET_LIST_OBJ_ATTR_INHERITED,
		GET_LIST_OBJ_STAGES,
		GET_LIST_USERS,
		GET_LIST_DAYS,
		GET_LIST_MONTHS,
		GET_OBJ_DEF,
		GET_CA_DEF,
		ADD_TIMEPHASED_ITEM,
		SAVE_TIMEPHASED_DATA,
		SEARCH_USER,
		CREATE_TIMESHEET,
		CREATE_USER,
		CREATE_DATAOBJECT,
		UPDATE_DATAOBJECT,
		CHANGE_LIFECYCLE
	}
	
	String configPath = "";
	static Map<String, String> parameters;
	boolean fileFound = false;
	Logger logger;
		
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public boolean isFileFound() {
//		return fileFound;
		return true;
	}

	public void setFileFound(boolean fileFound) {
		this.fileFound = fileFound;
	}

	public TkConfigFile(){
		
		parameters = new HashMap<String, String>();		
	}
	
	public static boolean getParameter(String parameter_, Properties prop_, boolean paramGet) {
		try{
			parameters.put(parameter_, prop_.getProperty(parameter_).trim());
			paramGet = true;
		} catch(Exception e){
			paramGet = false;
		}
		return paramGet;
	}
	
	public static boolean checkParameter(String parameter_, boolean paramChecked) {
		
		String param = "";
		
		try{
			param = parameters.get(parameter_).toString();
			if(param == ""){
				System.out.println("Config file error: "+parameter_+" value is empty. Please add "+parameter_+" in the config file");
				System.out.println("Ex: "+parameter_+"=3");
				paramChecked = false;
			}
		}
		catch(NullPointerException e){
			System.out.println("Config file error: "+parameter_+" Not found. Please add "+parameter_+" in the config file");
			System.out.println("Ex: "+parameter_+"=3");
			paramChecked = false;
		}
		return paramChecked;
	  	}
	
	public Map<String, String> getParameters()  throws IOException{

		try {
			Properties prop = new Properties();
			
			String propertiesPath = "";

			File jarPath=new File(TkConfigFile.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	        propertiesPath=jarPath.getParentFile().getAbsolutePath();
	        propertiesPath.replace(" ", "\\ ");
	        propertiesPath = propertiesPath.replace("\\", "/");
	        prop.load(new FileInputStream(propertiesPath+"/config.properties"));
	        
	        fileFound = true;
	        
			String parameter_ = null;
			boolean paramGet = true;
			
			parameter_ = "login";
			paramGet = getParameter(parameter_, prop, paramGet);
	        
			parameter_ = "password_non_hash";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "password_hash";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "password_crypto";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "proxyUser";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "proxyPassword";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "proxyHost";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "proxyPort";
			paramGet = getParameter(parameter_, prop, paramGet);	
			
			parameter_ = "logLevel";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			try{
				String logPath = prop.getProperty("logPath");
				logPath = logPath.replace("\"","");
				if(logPath.substring(logPath.length()-1).equals("/")){
					
				} else {
					logPath = logPath+"/";
				}

				if(logPath == null || logPath.isEmpty()){
					logPath = propertiesPath+"/";
				}
				else{
					File f = new File(logPath);
					if(!f.exists())
						logPath = propertiesPath+"/";					
				}				
				parameters.put("logPath", logPath);
			} catch(Exception e){
				
			}
			
			try{
				serverURL = prop.getProperty("serverName");
				String contains = "";
				if(serverURL.contains("'")) contains = "'";
				if(serverURL.contains("\"")) contains = "\"";
				String[] split_server = serverURL.split(contains);
				try{
					serverURL = split_server[1];
				} catch(Exception e){
					
				}
				if(!serverURL.substring((serverURL.length()-1)).equals("/")) serverURL += "/";
				parameters.put("serverName", serverURL);
			} catch(Exception e){
				
			}
			
			//****************************************************************************************
			//[FTP connection]
			parameter_ = "ftp_download";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ftp_server";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ftp_port";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ftp_user";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ftp_pass";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ftp_local_path";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			//[Opération Budgétaire file information]
			parameter_ = "file_ob_import_process";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_path";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_result_subdir";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_ext";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_ext_result";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_separator";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_header";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_filename_prefix";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_mill_prg_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_code_prg_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_mill_ope_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_code_ope_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_mont_glb_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_description_column";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ob_stage_column";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_stage_values";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_stage_closure";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_code_projet_column";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_primary_key";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_currency";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_inherit_ca";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_administrator";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_year_allowed";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ob_commit";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			//[files budget CP information]
			parameter_ = "file_cp_import_process";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_path";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_result_subdir";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_ext";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_ext_result";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_header_number";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_filename_prefix";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_file_sheet";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_mill_prg_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_code_prg_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_mill_ope_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_code_ope_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_annee_dec_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_mont_dec_2017_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_mont_dec_2018_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_mont_dec_2019_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_mont_dec_2020_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_mont_dec_2021_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_mont_dec_2022_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ae_mont_eng_total_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_tpa_attr_id";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_tpa_attr_type_id";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_tpa_version";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_tpa_cost_type_attr_id";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_tpa_cost_type_val_id";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_tpa_input_currency";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_tpa_calendar_id";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_cp_tpa_period_type";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_cp_commit";
			paramGet = getParameter(parameter_, prop, paramGet);

			//[files budget EJ information]
			parameter_ = "file_ae_import_process";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_path";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_result_subdir";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_ext";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_ext_result";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_header_number";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_filename_prefix";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_file_sheet";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_mill_prg_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_code_prg_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_mill_ope_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_code_ope_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_annee_eng_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_os_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_mill_ej_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_code_ej_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_type_ej_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_description_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_status_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_mont_eng_2017_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_mont_eng_2018_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_mont_eng_2019_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_mont_eng_2020_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_mont_eng_2021_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_mont_eng_2022_mapping";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_tpa_attr_id";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_tpa_attr_type_id";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_tpa_version";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_tpa_cost_type_attr_id";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_tpa_cost_type_val_id";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ae_stage_values";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ae_stage_closure";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_tpa_input_currency";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_tpa_calendar_id";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_tpa_period_type";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_year_allowed";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "file_ae_primary_key";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "file_ae_commit";
			paramGet = getParameter(parameter_, prop, paramGet);

			//[Triskell OB reports]
			parameter_ = "ID_REPORT_GET_OBJECT_DEF";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_REPORT_GET_OBJECT_STG";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_REPORT_GET_OBJECT_INH_CA";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_REPORT_GET_USER_NAME";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_REPORT_GET_DIE_PRJ";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_REPORT_GET_DIE_OB";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_REPORT_GET_DIE_EJ";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			//[Triskell budget CP reports]
			parameter_ = "ID_REPORT_GET_DIE_OB_TPA_ITEMS";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_REPORT_GET_CALENDAR_MONTHS";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_REPORT_GET_DIE_EJ_TPA_ITEMS";
			paramGet = getParameter(parameter_, prop, paramGet);

			//[Triskell objects]
			parameter_ = "ID_OBJECTS_OB";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_OBJECTS_EJ";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			//[Triskell parameters]			
			parameter_ = "ID_PARAM_OFFSET";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_PARAM_COUNT";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_PARAM_ID";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_PARAM_CALENDAR";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_PARAM_PERIOD_TYPE";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_PARAM_ATTR_ID";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_PARAM_VERSION_ID";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "ID_PARAM_ATTR_VAL_ID";
			paramGet = getParameter(parameter_, prop, paramGet);

			parameter_ = "SLEEP_RETRY_CALL";
			paramGet = getParameter(parameter_, prop, paramGet);
			
			parameter_ = "NB_RETRY_CALL";
			paramGet = getParameter(parameter_, prop, paramGet);

		}
		catch(FileNotFoundException f){
			System.out.println("Error:	config.properties file not found. Please add this file in the same directory as the Jar's.");
		}
		catch (Exception e) {
			System.out.println("Error: 	" + e);
		} 
		finally {
			
		}
		return parameters;
	}

	public String getConfigPath() {
		return parameters.get("csvpath");
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	
	public boolean checkParameters(Map<String, String> parameters){
		
		String param = "";
		boolean paramChecked = true;
		
		String parameter_ = null;
		
		parameter_ = "login";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "password_non_hash";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "password_hash";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "password_crypto";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "logPath";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "logLevel";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		if(parameters.get("password_non_hash").isEmpty() && parameters.get("password_hash").isEmpty()){
			System.out.println("Config file error: password_non_hash and password_hash parameters can't be empty in the same time. Please add password in the config file");
			paramChecked = false;
		} else{
			paramChecked = true;
		}
		
		if(parameters.get("logPath").isEmpty()){
			System.out.println("Config file error: logpath parameter can't be empty. Please fill a log path in the config file");
			paramChecked = false;
		} else{
			paramChecked = true;
		}
		
		if(parameters.get("password_crypto").isEmpty()){
			System.out.println("Config file error: password_crypto parameter can't be empty. Please fill a cpryto(MD5, SHA256) in the config file");
			paramChecked = false;
		} else{
			paramChecked = true;
		}

		
		//***********************************************************************************************
		//[FTP connection]
		parameter_ = "ftp_download";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ftp_server";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ftp_port";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ftp_user";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ftp_pass";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ftp_local_path";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		//[Opération OB information]
		parameter_ = "file_ob_import_process";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_path";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_result_subdir";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_ext";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_ext_result";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_separator";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_header";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_filename_prefix";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_mill_prg_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_code_prg_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_mill_ope_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_code_ope_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_mont_glb_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_description_column";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ob_stage_column";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_stage_values";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_stage_closure";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_code_projet_column";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_primary_key";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_currency";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_inherit_ca";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_administrator";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_year_allowed";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ob_commit";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		//[files budget CP information]
		parameter_ = "file_cp_import_process";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_path";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_result_subdir";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_ext";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_ext_result";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_header_number";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_filename_prefix";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_file_sheet";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_mill_prg_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_code_prg_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_mill_ope_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_code_ope_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_annee_dec_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_mont_dec_2017_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_mont_dec_2018_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_mont_dec_2019_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_mont_dec_2020_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_mont_dec_2021_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_mont_dec_2022_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ae_mont_eng_total_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_tpa_attr_id";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_tpa_attr_type_id";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_cp_tpa_version";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_tpa_cost_type_attr_id";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_tpa_cost_type_val_id";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_tpa_input_currency";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_tpa_calendar_id";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_cp_tpa_period_type";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_cp_commit";
		paramChecked = checkParameter(parameter_, paramChecked);

		//[files budget AE information]
		parameter_ = "file_ae_import_process";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_path";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_result_subdir";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_ext";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_ext_result";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_header_number";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_filename_prefix";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_file_sheet";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_mill_prg_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_code_prg_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_mill_ope_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_code_ope_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_annee_eng_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_os_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_mill_ej_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_code_ej_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_type_ej_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_description_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_status_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_mont_eng_2017_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_mont_eng_2018_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_mont_eng_2019_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_mont_eng_2020_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_mont_eng_2021_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_mont_eng_2022_mapping";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_tpa_attr_id";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_tpa_attr_type_id";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_tpa_version";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_tpa_cost_type_attr_id";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_tpa_cost_type_val_id";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ae_stage_values";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ae_stage_closure";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_tpa_input_currency";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_tpa_calendar_id";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_tpa_period_type";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_year_allowed";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "file_ae_primary_key";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "file_ae_commit";
		paramChecked = checkParameter(parameter_, paramChecked);
				
		//[Triskell OB reports]
		parameter_ = "ID_REPORT_GET_OBJECT_DEF";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_REPORT_GET_OBJECT_STG";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_REPORT_GET_OBJECT_INH_CA";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_REPORT_GET_USER_NAME";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_REPORT_GET_DIE_PRJ";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_REPORT_GET_DIE_OB";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_REPORT_GET_DIE_EJ";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		//[Triskell budget CP reports]
		parameter_ = "ID_REPORT_GET_DIE_OB_TPA_ITEMS";
		paramChecked = checkParameter(parameter_, paramChecked);;
		
		parameter_ = "ID_REPORT_GET_CALENDAR_MONTHS";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_REPORT_GET_DIE_EJ_TPA_ITEMS";
		paramChecked = checkParameter(parameter_, paramChecked);

		//[Triskell objects]
		parameter_ = "ID_OBJECTS_OB";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_OBJECTS_EJ";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		//[Triskell parameters]			
		parameter_ = "ID_PARAM_OFFSET";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_PARAM_COUNT";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_PARAM_ID";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_PARAM_CALENDAR";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_PARAM_PERIOD_TYPE";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_PARAM_ATTR_ID";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_PARAM_VERSION_ID";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "ID_PARAM_ATTR_VAL_ID";
		paramChecked = checkParameter(parameter_, paramChecked);

		parameter_ = "SLEEP_RETRY_CALL";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		parameter_ = "NB_RETRY_CALL";
		paramChecked = checkParameter(parameter_, paramChecked);
		
		//***********************************************************************************************************

		try{
			param = parameters.get("serverName").toString();
			if(param == ""){
				System.out.println("Config file error: serverName value is empty. Please add serverName in the config file");
				paramChecked = false;
			}
		}
		catch(NullPointerException e){
			System.out.println("Config file error: serverName value is empty. Please add serverName in the config file");
			paramChecked = false;
		}		
		
		return paramChecked;
	}
	
	public static boolean requestCheck () {

		boolean requestCheck = false;
		
		try {
			Map<String, String> authConfig = new HashMap<String, String>();
					
			TkConfigFile configFile = new TkConfigFile();						
			try{
				authConfig = configFile.getParameters();
			} catch(Exception e){
				import_SIVA_VNF_Main.logger.severe(e.getMessage());
			}							
			
			if(configFile.isFileFound()){
				if(configFile.checkParameters(authConfig)){						
					requestCheck = true;						
				}
			} else {
				import_SIVA_VNF_Main.logger.info("Erreur: Veuillez verifier si le fichier config contient tous les paramètres requis");
			}
		} catch (Exception e) {
		System.err.println("Server Connection error : " + e.getMessage());
		import_SIVA_VNF_Main.logger.severe("Server Connection error : " + e.getMessage());
		}
		return requestCheck;
	}
}
