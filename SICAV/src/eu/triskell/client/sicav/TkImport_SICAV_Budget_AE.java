package eu.triskell.client.sicav;
/**
 * 
 */
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.FormulaEvaluator;  
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;

import eu.triskell.client.sicav.TkConfigFile.TYPE_REPORT;


/**
 * @author vcail
 *
 */
@SuppressWarnings("unused")
public class TkImport_SICAV_Budget_AE {

	/**
	 * @param args
	 */
	
	static Logger logger=Logger.getLogger(TkImport_SICAV_Budget_AE.class.getName());
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		TkImport_SICAV_Budget_AE.logger = logger;
	}
	
	static ArrayList<String> list_DIEOB = new ArrayList<>();
	static ArrayList<String> list_DIEEJ = new ArrayList<>();
	static ArrayList<String> list_DIEEJ_TPA_items = new ArrayList<>();
	static ArrayList<String> list_DIEEJ_inherit_ca = new ArrayList<>();
	static ArrayList<String> list_DIEEJ_stages = new ArrayList<>();
	static ArrayList<String> list_DIEEJ_Obj_Def = new ArrayList<>();
	
	static ArrayList<String> list_CALENDAR_Months = new ArrayList<>();
	
	static List<AE_input> list_aelines = new ArrayList<>();
	static String cvsSplitBy = ";";
	
	static String TPADetails = null;
	
	@SuppressWarnings("unchecked")
	public void Import_SICAV_files() throws IOException, NumberFormatException, InterruptedException {

		try {

			TkActions tk = new TkActions();
			tk.setLogger(logger);
							
			if (tk.login()) {
				
				//String _tkTodayDate = TkUtils.getTodaysDate("yyyy.MM.dd");
				String _currentYear = TkUtils.getTodaysDate("yyyy");
				String objectName = "";
				String pool = "";
				String code_generator = "";
				int linecount = 0;
		        String anim= "|/-\\";
		        String XMLstring = "";
		        
		        int nbDataObjectCreated = 0;
		        int nbDataObjectUpd = 0;
		        int nbDataObjectChgStage = 0;
		        int nbTpaItemCreated = 0;
		        int nbTpaItemUpd = 0;
				
				File dir = new File(TkConfigFile.parameters.get("file_ae_path")); 
				FileFilter fileFilter = new WildcardFileFilter(TkConfigFile.parameters.get("file_ae_filename_prefix")+"*"+TkConfigFile.parameters.get("file_ae_ext"), IOCase.INSENSITIVE); 
				File[] fileList = dir.listFiles(fileFilter); 
				
				if (fileList.length > 0) 
				{
					/** The oldest file comes first **/ 
					Arrays.sort(fileList, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR); 
					
					//Get Tk referentials
					
					//Get OB list
					logger.finest("Get OB list");
					list_DIEOB = (ArrayList<String>) tk.GetDataobjectList(TkConfigFile.parameters.get("ID_REPORT_GET_DIE_OB"), TkConfigFile.parameters.get("ID_OBJECTS_OB"), TYPE_REPORT.GET_LIST_DATAOBJECTS);
					logger.finest("Nb OB : " + list_DIEOB.size());
					
					//Get EJ/OB list
					logger.finest("Get EJ/OB list");
					list_DIEEJ = (ArrayList<String>) tk.GetDataobjectList(TkConfigFile.parameters.get("ID_REPORT_GET_DIE_EJ"), TkConfigFile.parameters.get("ID_OBJECTS_EJ"), TYPE_REPORT.GET_LIST_DATAOBJECTS);
					logger.finest("Nb OB : " + list_DIEEJ.size());
					
					//Get
					//logger.finest("Get TPA details");
					//TPADetails = tk.GetCustomAttributDetails(TkConfigFile.parameters.get("file_ae_tpa_attr_id"), TkConfigFile.parameters.get("file_ae_tpa_attr_type_id"));
					
					//Get EJ stages
					logger.finest("Get OB stage list");
					list_DIEEJ_stages = (ArrayList<String>) tk.GetObjectDetails(TkConfigFile.parameters.get("ID_REPORT_GET_OBJECT_STG"), TkConfigFile.parameters.get("ID_OBJECTS_EJ"), TYPE_REPORT.GET_LIST_OBJ_STAGES);
					logger.finest("Nb OB stages : " + list_DIEEJ_stages.size());
					
					//Get EJ inherited custom attributs
					logger.finest("Get OB inherited CA list");
					list_DIEEJ_inherit_ca = (ArrayList<String>) tk.GetObjectDetails(TkConfigFile.parameters.get("ID_REPORT_GET_OBJECT_INH_CA"), TkConfigFile.parameters.get("ID_OBJECTS_EJ"), TYPE_REPORT.GET_LIST_OBJ_ATTR_INHERITED);
					logger.finest("Nb OB inherited CA : " + list_DIEEJ_inherit_ca.size());
					
					TkXMLDocument.listRelationships.clear();
					TkXMLDocument.listAttributs.clear();
					TkXMLDocument.listAttributsExt.clear();
					
					for(String item_ : list_DIEEJ_inherit_ca){
						if (item_.split(TkDataRequest.strSeparator)[2].equals("true")) {
							TkXMLDocument.listAttributs.add(item_.split(TkDataRequest.strSeparator)[0]+TkDataRequest.strSeparator+"I"+TkDataRequest.strSeparator+"");
						}
	        		}
					
					//Get EJ definition
					logger.finest("Get EJ object def");
					list_DIEEJ_Obj_Def = (ArrayList<String>) tk.GetObjectDetails(TkConfigFile.parameters.get("ID_REPORT_GET_OBJECT_DEF"), TkConfigFile.parameters.get("ID_OBJECTS_EJ"), TYPE_REPORT.GET_OBJ_DEF);
					logger.finest("Nb OB object def : " + list_DIEEJ_Obj_Def.size());
					
					if (list_DIEEJ_Obj_Def.size() == 1) {
						//name+strSeparator+pool+strSeparator+code_generator
						objectName = list_DIEEJ_Obj_Def.get(0).split(TkDataRequest.strSeparator)[0];
						pool = list_DIEEJ_Obj_Def.get(0).split(TkDataRequest.strSeparator)[1];
						code_generator = list_DIEEJ_Obj_Def.get(0).split(TkDataRequest.strSeparator)[2];
						//System.out.println(objectName);
						//System.out.println(pool);
						//System.out.println(code_generator);
					} else {
						
					}
					
				} //filesList now contains all the xls files in sorted order
				
				String result = "";
				
				for (int i = 0; i < fileList.length; i++) {
					
					  String xlsFile = dir.toString();
					  String xlsFileResult = dir.toString();
					  if (SystemUtils.IS_OS_WINDOWS) {
						  xlsFileResult = xlsFileResult+"\\"+TkConfigFile.parameters.get("file_ae_result_subdir")+"\\"+fileList[i].getName();
						  xlsFile = xlsFile+"\\"+fileList[i].getName();
					  } else {
						  xlsFileResult = xlsFileResult+"/"+TkConfigFile.parameters.get("file_ae_result_subdir")+"/"+fileList[i].getName();
						  xlsFile = xlsFile+"/"+fileList[i].getName();
					  }
					  
					  xlsFileResult = xlsFileResult.replace(TkConfigFile.parameters.get("file_ae_ext"), "_"+TkUtils.getTodaysDate("yyyyMMddhhmmss")+TkConfigFile.parameters.get("file_ae_ext"));
					  
					  String xlsFileKO = xlsFileResult.replace(TkConfigFile.parameters.get("file_ae_ext"), TkConfigFile.parameters.get("file_ae_ext_result"));
					  
					  
					  if (fileList[i].isFile()) {
						System.out.println("*****************************************");
						System.out.println("Proceed budget AE File " + fileList[i].getName());
						System.out.println("*****************************************");
					    logger.info("*****************************************");
					    logger.info("Proceed budget AE File " + fileList[i].getName());
					    logger.info("*****************************************");
				
						//obtaining input bytes from a file  
						FileInputStream fis=new FileInputStream(new File(xlsFile));
						
						//creating workbook instance that refers to .xls file  
						@SuppressWarnings("resource")
						HSSFWorkbook wb=new HSSFWorkbook(fis);   
						
						System.out.println("Sheet index:" + wb.getSheetIndex(TkConfigFile.parameters.get("file_ae_file_sheet")));
						
						//creating a Sheet object to retrieve the object  
						HSSFSheet sheet=wb.getSheetAt(wb.getSheetIndex(TkConfigFile.parameters.get("file_ae_file_sheet")));  
				    	
				    	String _ae_stage_values = TkConfigFile.parameters.get("file_ae_stage_values");
				    	List<String> ejStageList = Arrays.asList(_ae_stage_values.split("[\\s;]+"));
				    	
				    	String _ae_mont_eng_total_mapping = TkConfigFile.parameters.get("file_ae_mont_eng_total_mapping").split(",")[0];
				    	List<String> ejmontEngTotalList = Arrays.asList(_ae_mont_eng_total_mapping.split("[\\s;]+"));
				    	String _ae_mont_eng_total = TkConfigFile.parameters.get("file_ae_mont_eng_total_mapping").split(",")[1];
				    	
				    	String annee_mont_eng = "";
				    	String _ae_annee_eng = "";
				    	//String annee_cell = "";

				    	
						//evaluating cell type
						for(Row row: sheet)     //iteration over row using for each loop  
						{  
							linecount += 1;
			            	
			                String data = "\r" + anim.charAt(linecount % anim.length()) + " " + linecount;
			                System.out.write(data.getBytes());
			            	
			            	System.out.flush();
			            	
							if (row != null && row.getRowNum() > 1) {
								TkXMLDocument.listAttributsExt.clear();
			            		TkXMLDocument.listAttributsExt = (ArrayList<String>) TkXMLDocument.listAttributs.clone();
								
			            		String _ae_col_mill_ej = TkUtils.getEJFileColumnData(row, "file_ae_mill_ej_mapping", true); //B
								
			            		String _ae_col_status = TkUtils.getEJFileColumnData(row, "file_ae_status_mapping", false); //Q
			            		
								//If EJ stage is not allowed
				            	if ( _ae_stage_values.indexOf(_ae_col_status) == -1) {
				            		logger.finest("line : "+linecount+ " - [EJ status : " + _ae_col_status + " not allowed]");
				            		TkUtils.write(xlsFileKO, "Row number : "+row.getRowNum()+cvsSplitBy+TkMessageFile.messages.get("ej.stage.notallowed"));
				            		
				            		//next line
				            		continue;
				            	}
				            	
				            	String _tk_stage_id = "0";
				            	String _tk_stage_name = "";
				        		
				        		for(String item_ : ejStageList){
				        			if (item_.split(TkDataRequest.strSeparator)[0].equals(_ae_col_status) == true) {
				        				_tk_stage_id = item_.split(TkDataRequest.strSeparator)[1];
				        				break;
				        			}
				        		}
				        		
				        		if (_tk_stage_id.equals("0")) {
				        			//Next line
				        			continue;
				        		}
				        		
				        		result = TkUtils.getListItem(list_DIEEJ_stages,_tk_stage_id);
				        		if (result.length() > 0) {
						        	logger.finest("line : "+linecount+ " - [Stage id : " + _tk_stage_id + " exists]");
						        	
						        	_tk_stage_name = result.split(TkDataRequest.strSeparator)[0];
		        				} else {
		        					//Anomaly
		        					System.out.println("line : "+linecount+ " - [Stage id : " + _tk_stage_id + " does not exists]");
		        					logger.finest("line : "+linecount+ " - [Stage id : " + _tk_stage_id + " does not exists]");
		        					
		        					//Next record
		        					continue;
		        				}
				            	
				            	//If EJ year is not equal with the current year
				            	//if ( _currentYear.equals(_ae_mill_ope) == false) {
				        		if (TkConfigFile.parameters.get("file_ae_year_allowed").contains(_ae_col_mill_ej) == false) {
				            		logger.finest("line : "+linecount+ " - [EJ year : " + _ae_col_mill_ej + " not allowed]");
				            		TkUtils.write(xlsFileKO, "Row number : "+row.getRowNum()+cvsSplitBy+TkMessageFile.messages.get("ej.year.notallowed"));
				            		
				            		//next line
				            		continue;
				            	}
				        		
				        		String _ae_col_mill_prg = TkUtils.getEJFileColumnData(row, "file_ae_mill_prg_mapping", false); //X
				        		String _ae_col_code_prg = TkUtils.getEJFileColumnData(row, "file_ae_code_prg_mapping", false); //Y
				        		String _ae_col_mill_ope = TkUtils.getEJFileColumnData(row, "file_ae_mill_ope_mapping", false); //AA
				        		String _ae_col_code_ope = TkUtils.getEJFileColumnData(row, "file_ae_code_ope_mapping", false); //AB
								
								//OP ident
								String _num_OP = _ae_col_mill_prg+_ae_col_code_prg+_ae_col_mill_ope+_ae_col_code_ope;
								String dataobject_parent_id = "";
								
								//Check OB exists with code
				                result = TkUtils.getListItem(list_DIEOB, _num_OP);
				        		if (result.length() > 0) {
						        	logger.finest("OB : "+ _num_OP + " exists]");
						        	
						        	dataobject_parent_id = result.split(TkDataRequest.strSeparator)[1];
				        		} else {
				            		logger.finest("line : "+linecount+ " - [OB : " + _num_OP + " not exists]");
				            		TkUtils.write(xlsFileKO, "Row number : "+row.getRowNum()+cvsSplitBy+"OB : "+_num_OP+cvsSplitBy+TkMessageFile.messages.get("ob.notexists"));
				            		
				            		//next line
				            		continue;
				        		}
				        		
				        		String timephaseditem_id = "";
								
				        		_ae_annee_eng = TkUtils.getEJFileColumnData(row, "file_ae_annee_eng_mapping", false); //B
								
				        		if (_ae_annee_eng.equals("2017")) {
									annee_mont_eng = TkUtils.getEJFileColumnData(row, "file_ae_mont_eng_2017_mapping", false); //AK;
								} else if (_ae_annee_eng.equals("2018")) {
									annee_mont_eng = TkUtils.getEJFileColumnData(row, "file_ae_mont_eng_2018_mapping", false); //AM;
								} else if (_ae_annee_eng.equals("2019")) {
									annee_mont_eng = TkUtils.getEJFileColumnData(row, "file_ae_mont_eng_2019_mapping", false); //AO;
								} else if (_ae_annee_eng.equals("2020")) {
									annee_mont_eng = TkUtils.getEJFileColumnData(row, "file_ae_mont_eng_2020_mapping", false); //AQ;
								} else if (_ae_annee_eng.equals("2021")) {
									annee_mont_eng = TkUtils.getEJFileColumnData(row, "file_ae_mont_eng_2021_mapping", false); //AS;
								} else if (_ae_annee_eng.equals("2022")) {
									annee_mont_eng = TkUtils.getEJFileColumnData(row, "file_ae_mont_eng_2022_mapping", false); //AU;
								} else {
									continue;
								}
				        		
				        		String _ae_col_os = TkUtils.getEJFileColumnData(row, "file_ae_os_mapping", true); //A;
								String _ae_col_code_ej = TkUtils.getEJFileColumnData(row, "file_ae_code_ej_mapping", true); //D;
				        		String _ae_col_type_ej = TkUtils.getEJFileColumnData(row, "file_ae_type_ej_mapping", true); //F;				        		
				        		String _ae_col_desc = TkUtils.getEJFileColumnData(row, "file_ae_description_mapping", false); //N;
								
								//EJ ident
								String _num_EJ = _ae_col_os+_ae_col_mill_ej+_ae_col_code_ej;
								String dataobject_id = "";
								
								//Check EJ exists with code
				                result = TkUtils.getListItem(list_DIEEJ, _num_EJ);
				        		if (result.length() > 0) {
						        	logger.finest("EJ : "+ _num_EJ + " exists]");
						        	
						        	//Check EJ right attached to the OB
						        	if (dataobject_parent_id.equals(result.split(TkDataRequest.strSeparator)[3]) &&
						        		_num_OP.equals(result.split(TkDataRequest.strSeparator)[2])) {
						        		//EJ Exists
						        		dataobject_id = result.split(TkDataRequest.strSeparator)[1];
						        		
							        	String dataobject_lifecycle = result.split(TkDataRequest.strSeparator)[4];
							        	String stage_closure = TkConfigFile.parameters.get("file_ob_stage_closure").split(TkDataRequest.strSeparator)[0];
							        	
							        	//If EJ stage equal Soldé in file AND EJ stage not equal Soldé in TK
							        	if (_tk_stage_name.equals(stage_closure) == true &&
							        		dataobject_lifecycle.equals(stage_closure) == false ) {
							        		
							        		try{
												double id_double = Double.parseDouble(dataobject_id);
											} catch(Exception e){
												System.out.println("error: " + e);
												dataobject_id = "";
											}
							        		
							        		String payload = "";
							        		
							        		//Close OB
											if (dataobject_id.length() > 0 ) {
												payload  = "{\"id\":0, \"params\":{ " + 
															"\"dataObjectId\":\""+dataobject_id+"\"" +
															", \"newLifecycle\":\""+TkConfigFile.parameters.get("file_ob_stage_closure").split(TkDataRequest.strSeparator)[1]+"\"" +
															", \"commentToTransition\":\""+""+"\"" +
															", \"attachmentsToTransition\":\""+""+"\"" +
															" }, \"objects\":null}";
												logger.finest("payload: " + payload);
												
												if (Boolean.valueOf(TkConfigFile.parameters.get("file_ob_commit")) == true) {
													if (tk.ChangeLifecycle(payload, TYPE_REPORT.CHANGE_LIFECYCLE)){
														logger.finest("Action done !\r\n");
														nbDataObjectChgStage += 1;
								                    }
											    } else {
											    	logger.info(payload);
											    }
											}
							        	} else {
							        		//do nothing
							        	}
						        	} else {
					            		logger.finest("line : "+linecount+ " - [EJ : " + _num_EJ + " not attached to the same OB]");
					            		TkUtils.write(xlsFileKO, "Row number : "+"Row number : "+row.getRowNum()+cvsSplitBy+"EJ : "+_num_EJ+cvsSplitBy+TkMessageFile.messages.get("ej.otherob.attached"));
						        		
						        		//next line
						        		continue;
						        	}
				        		} else {
				            		logger.finest("line : "+linecount+ " - [EJ : " + _num_EJ + " not exists]");
				            		//TkUtils.write(xlsFileKO, "Row number : "+row.getRowNum()+cvsSplitBy+"EJ : "+_num_EJ+cvsSplitBy+TkMessageFile.messages.get("ej.notexists"));
				            		
				            		for(String item_ : list_DIEEJ_inherit_ca){
		        						if (item_.split(TkDataRequest.strSeparator)[1].equals(TkConfigFile.parameters.get("file_ae_primary_key"))) {
		        							TkXMLDocument.listAttributsExt.add(item_.split(TkDataRequest.strSeparator)[0]+TkDataRequest.strSeparator+"N"+TkDataRequest.strSeparator+_num_EJ);
		        							break;
		        						}
		        		    		}
		        					
		        					/* INHERITED CA
		        					*/
		        					
		        					//Create OB
		        					TkXMLDocument tkxml = new TkXMLDocument();
								    tkxml.setLogger(logger);
								   
								    XMLstring = tkxml.dataobjectsOBCreationXML(
								    		objectName,
								    		dataobject_parent_id,
								    		_num_EJ,
								    		_ae_col_desc,
											_tk_stage_name,
											pool,
											TkConfigFile.parameters.get("login").split("@")[0],
											"",
											_num_EJ,
											true, 
											false, 
											true);
								   
								    logger.finest("dataobjectsCreationXML : XML doc generated.\r\n");
								    logger.finest(XMLstring);
								    if (Boolean.valueOf(TkConfigFile.parameters.get("file_ae_commit")) == true) {
									    if (tk.CreateDataObject(XMLstring, TYPE_REPORT.CREATE_DATAOBJECT) == true) {
									    	logger.finest("EJ generated !\r\n");
									    	nbDataObjectCreated += 1;
									    	dataobject_id = (String) tk.getResult();
									    	list_DIEEJ.add(_num_EJ+TkDataRequest.strSeparator+dataobject_id+TkDataRequest.strSeparator+_num_OP+TkDataRequest.strSeparator+dataobject_parent_id+TkDataRequest.strSeparator+_tk_stage_name);
									    }
								    } else {
								    	logger.info(XMLstring);
								    }
				        		}
								
								float _montal_total = 0;
				        		
				        		for(String item_ : ejmontEngTotalList){
				        			Cell tmp = row.getCell(TkUtils.getExcelColumnNumber(item_));
				        			String _val = tmp.toString();
									if (TkUtils.isNullOrEmpty(_val)) {
										_val = "0";
									}
									_montal_total = _montal_total + Float.parseFloat(_val);
				        		}
				        		
				        		//If null or empty value
				        		if (TkUtils.isNullOrEmpty(annee_mont_eng)) {
				        			annee_mont_eng = "0";
				        		}
				        		
								logger.finest("Cell montant year "+_ae_annee_eng+" value:" + annee_mont_eng);
								
								for(String item_ : TkImport_SICAV_Budget_AE.list_DIEEJ_inherit_ca){
									if (item_.split(TkDataRequest.strSeparator)[1].equals(_ae_mont_eng_total)) {
										TkXMLDocument.listAttributsExt.add(item_.split(TkDataRequest.strSeparator)[0]+TkDataRequest.strSeparator+"N"+TkDataRequest.strSeparator+annee_mont_eng);
										break;
									}
					    		}
								
								if (dataobject_id.length() > 0) {
									//Update Montant total on EJ
									String params = "\"attr_"+_ae_mont_eng_total+"\":\""+annee_mont_eng+"\"";
									if (tk.updateDataobject(dataobject_id,params)) {
			                        	nbDataObjectUpd += 1;
			                    		logger.finest(".Dataobject ID : " + dataobject_id + " updated !\r\n");
				                    } else {
										logger.severe("Dataobject ID : " + dataobject_id + ", NOT updated\\r\\n");
									}
									
									try {
										list_aelines.add(new AE_input(dataobject_id+TkDataRequest.strSeparator+_ae_annee_eng,Double.parseDouble(annee_mont_eng)));
									} catch (Exception e) {
										System.out.println(e.toString());
										logger.severe(e.toString());
									}
								} else {
									logger.finest("line : "+linecount+ " - [EJ : " + _num_EJ + " not exists, dataobject_id is empty]");
				            		TkUtils.write(xlsFileKO, "Row number : "+row.getRowNum()+cvsSplitBy+"EJ : "+_num_EJ+cvsSplitBy+TkMessageFile.messages.get("ej.notexists"));
								}
							}
						}
						
						//Get EJ TPA items
						logger.finest("Get EJ TPA items list");
						list_DIEEJ_TPA_items = (ArrayList<String>) tk.GetTimephasedItemList(TkConfigFile.parameters.get("ID_REPORT_GET_DIE_EJ_TPA_ITEMS"), TkConfigFile.parameters.get("file_ae_tpa_attr_id"), TkConfigFile.parameters.get("file_ae_tpa_version"), TkConfigFile.parameters.get("file_ae_tpa_cost_type_val_id"), TYPE_REPORT.GET_LIST_TPA_ITEMS);				
						logger.finest("Nb OB TPA items : " + list_DIEEJ_TPA_items.size());
					
						//Get Calendar first months list
						logger.finest("Get Calendar all first months list");
						list_CALENDAR_Months = (ArrayList<String>) tk.GetTkFirstMonth(TkConfigFile.parameters.get("ID_REPORT_GET_CALENDAR_MONTHS"), 
																					  TkConfigFile.parameters.get("file_ae_tpa_calendar_id"), 
																					  TkConfigFile.parameters.get("file_ae_tpa_period_type"),
																					  TYPE_REPORT.GET_LIST_MONTHS);
						logger.finest("Nb Calendar first months : " + list_CALENDAR_Months.size());
						
						//Group by : code de l’opération constitué des colonnes 
						// dataobject_id
						System.out.println("*****************************************");
						System.out.println("Proceed budget AE unit aggregation");
						System.out.println("*****************************************");
					    logger.info("*****************************************");
					    logger.info("Proceed budget AE unit aggregation");
					    logger.info("*****************************************");
					    
			            Map<String, Double> _inputByKey = new HashMap<>();
			            
			            System.out.println("\n");
			            System.out.println("=====>> Control : Number of Line to proceed : " + list_aelines.size());
			            logger.info("\r=====>> Control : Number of Line to proceed : " + list_aelines.size());
			            
			            _inputByKey =  list_aelines.stream().collect(
		                         Collectors.groupingBy(AE_input::getKey, Collectors.summingDouble(AE_input::getunits)));
			            
			            logger.finest("Lines grouped by key : " + _inputByKey);
			            
			            System.out.println("\n");
			            System.out.println("=====>> Control : Number of Line to proceed (after grouping by key) : " + _inputByKey.size());
			            logger.info("\r=====>> Control : Number of Line to proceed (after grouping by key) : " + _inputByKey.size());
			            
			            System.out.println("*****************************************");
						System.out.println("Proceed budget AE unit import");
						System.out.println("*****************************************");
					    logger.info("*****************************************");
					    logger.info("Proceed budget AE unit import");
					    logger.info("*****************************************");
					    
			            String _units = "";
			            result = "";
			            Boolean success = false;
			            linecount = 0;
			            
			            // using for-each loop for iteration over Map.entrySet() 
			            for (Map.Entry<String,Double> entry : _inputByKey.entrySet()) {
			            	linecount += 1;
			            	
			                String data = "\r" + anim.charAt(linecount % anim.length()) + " " + linecount;
			                System.out.write(data.getBytes());
			            	
			            	System.out.flush();
			            	
			            	Double u = entry.getValue();
			            	_units = String.valueOf(u);
			            	logger.finest("Key = " + entry.getKey() + ", Value = " + _units);
					        
				        	String dataobject_id = entry.getKey().split(TkDataRequest.strSeparator)[0];
				        	_ae_annee_eng = entry.getKey().split(TkDataRequest.strSeparator)[1];
				        	
				        	String timephaseditem_id = "";
				        	
				        	//Contrôle existance du code de l'item Investissement dans le TPA DIE_Budget AE, version Engagé
				        	result = TkUtils.getListItem(list_DIEEJ_TPA_items, dataobject_id);
			        		if (result.length() > 0) {
		                		//System.out.println("OB (ligne investissement) : "+ dataobject_id+ " exists]");
					        	logger.finest("EJ (ligne investissement) : "+ dataobject_id+ " exists]");
					        	
					        	//On met à jour (plus bas)
					        	timephaseditem_id = result.split(TkDataRequest.strSeparator)[2];
	        				} else {
	        					//On créé
	        					logger.finest("EJ (ligne investissement) : create");
	        					success = tk.AddTimephasedItem(dataobject_id, 
			        							TkConfigFile.parameters.get("file_ae_tpa_attr_id"), 
			        							TkConfigFile.parameters.get("file_ae_tpa_version"),
			        							TkConfigFile.parameters.get("file_ae_tpa_cost_type_attr_id"),
			        							TkConfigFile.parameters.get("file_ae_tpa_cost_type_val_id"));
	        					//Récupérer item id
	        					if (success) {
	        						nbTpaItemCreated += 1;
	        						timephaseditem_id = (String) tk.getResult();
	        					}
	        				}
			        		
			        		result = TkUtils.getListItem(list_CALENDAR_Months, _ae_annee_eng); 
			        		
			        		//update
			        		logger.finest("EJ (ligne investissement) : update");
			        		success = tk.SaveTimephasedData(dataobject_id,
			        										TkConfigFile.parameters.get("file_ae_tpa_attr_id"), 
						        							TkConfigFile.parameters.get("file_ae_tpa_version"),
						        							timephaseditem_id,
						        							result.split(TkDataRequest.strSeparator)[1],
															_units,
															TkConfigFile.parameters.get("file_ae_tpa_input_currency"));
			        		//Check update success
        					if (success) {
        						nbTpaItemUpd += 1;
        					}
			            }
			            
			            logger.info("\r\n");
		                logger.info("\r Number of procedeed : " + linecount);
		                logger.info("\r Number of EJ created : " + nbDataObjectCreated);
		                logger.info("\r Number of EJ updated : " + nbDataObjectUpd);
		                logger.info("\r Number of EJ lifecycle updated : " + nbDataObjectChgStage); 
		                logger.info("\r Number of AE TPA line created : " + nbTpaItemCreated);
		                logger.info("\r Number of AE TPA line updated : " + nbTpaItemUpd);
		                
					} else if (fileList[i].isDirectory()) {
						logger.finest("Directory " + fileList[i].getName());
					}
					
					Path resultMove = Files.move 
						        (Paths.get(xlsFile),  
						         Paths.get(xlsFileResult));
					  
				    if(resultMove != null) 
				    { 
		              logger.finest("File renamed and moved successfully to results dir"); 
				    } else { 
		              logger.finest("Failed to move the file to results dir"); 
				    } 
				}
			} else {
				logger.severe("Enable to authenticate to the Triskell");
			}
		} finally {  
			logger.info("End of Process");
    	}
	} 

   class AE_input{
		private String key;
		private Double units;
		
		public AE_input(String key, Double units) {
			this.key = key;
			this.units = units;
		}
		
		public String getKey() {
			return key;
		}
		
		public void setKey(String key) {
			this.key = key;
		}
		
		public Double getunits() {
			return units;
		}
		
		public void setunits(Double units) {
			this.units = units;
		}
		
		@Override 
		public String toString() { 
				return String.format("%s;%.4f", key, units); 
		}
		
		@Override
		public int hashCode() {
			int hash = 7;
			hash = 79 * hash + Objects.hashCode(this.key);
			hash = (int) (79 * hash + units);
			return hash;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null ) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final AE_input other = (AE_input) obj;
			if (!Objects.equals(this.key,  other.key)) {
				return false;
			}
			if (this.units != other.units) {
				return false;
			}
			return true;
		}
   }
}
