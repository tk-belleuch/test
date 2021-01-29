package eu.triskell.client.sicav;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.SystemUtils;

import eu.triskell.client.sicav.TkActions;
import eu.triskell.client.sicav.TkConfigFile;
import eu.triskell.client.sicav.TkXMLDocument;
import eu.triskell.client.sicav.TkConfigFile.TYPE_REPORT;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author vcailleaud
 *

 *
 */


public class TkImport_SICAV_OB {

	/**
	 * @param args
	 */
	
	static Logger logger=Logger.getLogger(TkImport_SICAV_OB.class.getName());
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		TkImport_SICAV_OB.logger = logger;
	}
	
	static ArrayList<String> list_DIEProjects = new ArrayList<>();
	static ArrayList<String> list_DIEOB = new ArrayList<>();
	static ArrayList<String> list_DIEOB_inherit_ca = new ArrayList<>();
	static ArrayList<String> list_DIEOB_stages = new ArrayList<>();
	static ArrayList<String> list_DIEOB_Obj_Def = new ArrayList<>();
		
	@SuppressWarnings({ "unchecked", "unused" })
	public void Import_SICAV_files() throws NumberFormatException, InterruptedException, IOException {
		
		try {

			TkActions tk = new TkActions();
			tk.setLogger(logger);
							
			if (tk.login()) {
				
				//String _tkTodayDate = TkUtils.getTodaysDate("yyyy.MM.dd");
				String _currentYear = TkUtils.getTodaysDate("yyyy");
				String objectName = "";
				String pool = "";
				String code_generator = "";
				
		        int nbDataObjectCreated = 0;
		        int nbDataObjectChgStage = 0;
				
				File dir = new File(TkConfigFile.parameters.get("file_ob_path")); 
				FileFilter fileFilter = new WildcardFileFilter(TkConfigFile.parameters.get("file_ob_filename_prefix")+"*"+TkConfigFile.parameters.get("file_ob_ext"), IOCase.INSENSITIVE); 
				File[] fileList = dir.listFiles(fileFilter); 
				
				if (fileList.length > 0) 
				{ 
					
					/** The oldest file comes first **/ 
					Arrays.sort(fileList, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR); 
					
					//Get Tk referentials
					
					//Get Projects list
					logger.finest("Get Projects list");
					list_DIEProjects = (ArrayList<String>) tk.GetDataobjectList(TkConfigFile.parameters.get("ID_REPORT_GET_DIE_PRJ"), TkConfigFile.parameters.get("ID_OBJECTS_OB"), TYPE_REPORT.GET_LIST_DATAOBJECTS);				
					logger.finest("Nb Projects : " + list_DIEProjects.size());
				
					//Get OB list
					logger.finest("Get OB list");
					list_DIEOB = (ArrayList<String>) tk.GetDataobjectList(TkConfigFile.parameters.get("ID_REPORT_GET_DIE_OB"), TkConfigFile.parameters.get("ID_OBJECTS_OB"), TYPE_REPORT.GET_LIST_DATAOBJECTS);
					logger.finest("Nb OB : " + list_DIEOB.size());
					
					//Get OB inherited custom attributs
					logger.finest("Get OB inherited CA list");
					list_DIEOB_inherit_ca = (ArrayList<String>) tk.GetObjectDetails(TkConfigFile.parameters.get("ID_REPORT_GET_OBJECT_INH_CA"), TkConfigFile.parameters.get("ID_OBJECTS_OB"), TYPE_REPORT.GET_LIST_OBJ_ATTR_INHERITED);
					logger.finest("Nb OB inherited CA : " + list_DIEOB_inherit_ca.size());
					
					TkXMLDocument.listRelationships.clear();
					TkXMLDocument.listAttributs.clear();
					TkXMLDocument.listAttributsExt.clear();
					
					for(String item_ : list_DIEOB_inherit_ca){
						if (item_.split(TkDataRequest.strSeparator)[2].equals("true")) {
							TkXMLDocument.listAttributs.add(item_.split(TkDataRequest.strSeparator)[0]+TkDataRequest.strSeparator+"I"+TkDataRequest.strSeparator+"");
						}
	        		}
					
					//Get OB stages
					logger.finest("Get OB stage list");
					list_DIEOB_stages = (ArrayList<String>) tk.GetObjectDetails(TkConfigFile.parameters.get("ID_REPORT_GET_OBJECT_STG"), TkConfigFile.parameters.get("ID_OBJECTS_OB"), TYPE_REPORT.GET_LIST_OBJ_STAGES);
					logger.finest("Nb OB stages : " + list_DIEOB_stages.size());
					
					//Get OB definition
					logger.finest("Get OB object def");
					list_DIEOB_Obj_Def = (ArrayList<String>) tk.GetObjectDetails(TkConfigFile.parameters.get("ID_REPORT_GET_OBJECT_DEF"), TkConfigFile.parameters.get("ID_OBJECTS_OB"), TYPE_REPORT.GET_OBJ_DEF);
					logger.finest("Nb OB object def : " + list_DIEOB_Obj_Def.size());
					
					if (list_DIEOB_Obj_Def.size() == 1) {
						//name+strSeparator+pool+strSeparator+code_generator
						objectName = list_DIEOB_Obj_Def.get(0).split(TkDataRequest.strSeparator)[0];
						pool = list_DIEOB_Obj_Def.get(0).split(TkDataRequest.strSeparator)[1];
						code_generator = list_DIEOB_Obj_Def.get(0).split(TkDataRequest.strSeparator)[2];
						//System.out.println(objectName);
						//System.out.println(pool);
						//System.out.println(code_generator);
					} else {
						
					}
	
				} //filesList now contains all the csv files in sorted order
				
				for (int i = 0; i < fileList.length; i++) {
				  String csvFile = dir.toString();
				  String csvFileResult = dir.toString();
				  if (SystemUtils.IS_OS_WINDOWS) {
					  csvFileResult = csvFileResult+"\\"+TkConfigFile.parameters.get("file_ob_result_subdir")+"\\"+fileList[i].getName();
					  csvFile = csvFile+"\\"+fileList[i].getName();
				  } else {
					  csvFileResult = csvFileResult+"/"+TkConfigFile.parameters.get("file_ob_result_subdir")+"/"+fileList[i].getName();
					  csvFile = csvFile+"/"+fileList[i].getName();
				  }
				  csvFileResult = csvFileResult.replace(TkConfigFile.parameters.get("file_ob_ext"), "_"+TkUtils.getTodaysDate("yyyyMMddhhmmss")+TkConfigFile.parameters.get("file_ob_ext"));
				  
				  String csvFileKO = csvFileResult.replace(TkConfigFile.parameters.get("file_ob_ext"), TkConfigFile.parameters.get("file_ob_ext_result"));
				  
				  if (fileList[i].isFile()) {
				    System.out.println("Proceed OB File " + fileList[i].getName());
				    logger.info("*****************************************");
				    logger.info("Proceed OB File File " + fileList[i].getName());
				    logger.info("*****************************************");
				    			        
			        String cvsSplitBy = TkConfigFile.parameters.get("file_ob_separator");
			        String line = "";
			        String result = "";
			        String resultOB = "";
			        String resultPrj = "";
			        BufferedReader br = null;
			        int linecount = 0;
			        String anim= "|/-\\";
			        String XMLstring = "";
			        
			        String _ob_stage_values = TkConfigFile.parameters.get("file_ob_stage_values");
			        List<String> obStageList = Arrays.asList(_ob_stage_values.split("[\\s;]+"));
	
			        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");  
	    			//Date todayDate = dateFormat.parse(_tkTodayDate);
	                
			        try {
	
			            //br = new BufferedReader(new FileReader(csvFile));
			        	br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8));
			        	
			        	//Line by line...
			            while ((line = br.readLine()) != null) {
			            	
			            	linecount += 1;
			            	
			                String data = "\r" + anim.charAt(linecount % anim.length()) + " " + linecount;
			                System.out.write(data.getBytes());
			            	
			            	System.out.flush();
			                
			            	/*
			            	 * file_ob_mill_prg_mapping=1,684
							 * file_ob_code_prg_mapping=2,687
							 * file_ob_mill_ope_mapping=4,686
							 * file_ob_code_ope_mapping=5,685
							 * file_ob_mont_glb_mapping=11,740
							 * file_ob_description_column=7
							 * file_ob_stage_column=19
							 * file_ob_stage_values=E§177,C§191
							 * file_ob_code_projet_column=31
							 * file_ob_primary_key=628
							 * file_ob_currency=6
							 * file_ob_inherit_ca=true
							 * file_ob_administrator=149
							 * file_ob_commit=true
			            	 */
			            	if (linecount == 1 && Boolean.valueOf(TkConfigFile.parameters.get("file_ob_header")) == true) {
			            		//System.out.println("Skip header");
			            	} else {
			            		TkXMLDocument.listAttributsExt.clear();
			            		TkXMLDocument.listAttributsExt = (ArrayList<String>) TkXMLDocument.listAttributs.clone();
	
				            	String _ob_mill_ope = TkUtils.getCsvFileColumnData(line, "file_ob_mill_ope_mapping", cvsSplitBy, true); //E
				            	String _ob_stage = TkUtils.getCsvFileColumnData(line, "file_ob_stage_column", cvsSplitBy, false); //T
				            	
				            	//If OB stage is not allowed
				            	if ( _ob_stage_values.indexOf(_ob_stage) == -1) {
				            		logger.finest("line : "+linecount+ " - [OB status : " + _ob_stage + " not allowed]");
				            		TkUtils.write(csvFileKO, line+cvsSplitBy+TkMessageFile.messages.get("ob.stage.notallowed"));
				            		
				            		//next line
				            		continue;
				            	}
				            	
				            	String _tk_stage_id = "0";
				            	String _tk_stage_name = "";
				        		
				        		for(String item_ : obStageList){
				        			if (item_.split(TkDataRequest.strSeparator)[0].equals(_ob_stage) == true) {
				        				_tk_stage_id = item_.split(TkDataRequest.strSeparator)[1];
				        				break;
				        			}
				        		}
				        		
				        		if (_tk_stage_id.equals("0")) {
				        			//Next line
				        			continue;
				        		}
				        		
				        		result = TkUtils.getListItem(list_DIEOB_stages,_tk_stage_id);
				        		if (result.length() > 0) {
			                		//System.out.println("line : "+linecount+ " - [Stage id : " + _tk_stage_id + " exists]");
						        	logger.finest("line : "+linecount+ " - [Stage id : " + _tk_stage_id + " exists]");
						        	
						        	_tk_stage_name = result.split(TkDataRequest.strSeparator)[0];
		        				} else {
		        					//Anomaly
		        					System.out.println("line : "+linecount+ " - [Stage id : " + _tk_stage_id + " does not exists]");
		        					logger.finest("line : "+linecount+ " - [Stage id : " + _tk_stage_id + " does not exists]");
		        					
		        					//Next record
		        					continue;
		        				}
				            	
				            	//If OB year is not equal with the current year
				            	//if ( _currentYear.equals(_ob_mill_ope) == false) {
				        		if (TkConfigFile.parameters.get("file_ob_year_allowed").contains(_ob_mill_ope) == false) {
				            		logger.finest("line : "+linecount+ " - [OB year : " + _ob_mill_ope + " not allowed]");
				            		TkUtils.write(csvFileKO, line+cvsSplitBy+TkMessageFile.messages.get("ob.year.notallowed"));
				            		
				            		//next line
				            		continue;
				            	}
				            	
				            	//System.out.println(line);
				            	
				            	String _ob_mill_prg = TkUtils.getCsvFileColumnData(line, "file_ob_mill_prg_mapping", cvsSplitBy, true); //B
				            	String _ob_code_prg = TkUtils.getCsvFileColumnData(line, "file_ob_code_prg_mapping", cvsSplitBy, true); //C
				            	String _ob_code_ope = TkUtils.getCsvFileColumnData(line, "file_ob_code_ope_mapping", cvsSplitBy, true); //F
				            	String _ob_mont_glb = TkUtils.getCsvFileColumnData(line, "file_ob_mont_glb_mapping", cvsSplitBy, true); //L
				            	String _ob_description = TkUtils.getCsvFileColumnData(line, "file_ob_description_column", cvsSplitBy, false); //H
				            	String _ob_code_projet = TkUtils.getCsvFileColumnData(line, "file_ob_code_projet_column", cvsSplitBy, false); //AF
				            	
				            	if (_ob_code_projet.length() == 0) {
				            		//next line
				            		continue;
				            	}
				            	/*
				            	System.out.println(_ob_mill_prg);
				            	System.out.println(_ob_code_prg);
				            	System.out.println(_ob_mill_ope);
				            	System.out.println(_ob_code_ope);
				            	System.out.println(_ob_mont_glb);
				            	System.out.println(_ob_description);
				            	System.out.println(_ob_stage);
				            	System.out.println(_ob_stage_values);
				            	System.out.println(_ob_code_projet);*/
				            	/*
				            	4113
				            	D1C0101
				            	2016
				            	4100260
				            	25000
				            	Affouillement écluse 27
				            	E
								E§177;C§191
				            	VCA*/
				            	
				            	String dataobject_id = "";
				            	
				            	//check Project code exists
			                    resultPrj = TkUtils.getListItem(list_DIEProjects,_ob_code_projet+TkDataRequest.strSeparator);
			                	if (resultPrj.length() > 0) {
						        	logger.finest("line : "+linecount+ " - [Project code : " + _ob_code_projet + " exists]");
						        	dataobject_id = resultPrj.split(TkDataRequest.strSeparator)[1];
		        				} else {
		        					//Anomaly
		        					logger.finest("line : "+linecount+ " - [Project code : " + _ob_code_projet + " does not exists]");
		        					TkUtils.write(csvFileKO, line+cvsSplitBy+TkMessageFile.messages.get("ob.projectcode.notexists"));
		        					
		        					//Next record
		        					continue;
		        				}
				            	
				            	String _primary_key = _ob_mill_prg+_ob_code_prg+_ob_mill_ope+_ob_code_ope;
				            	
		            			//check OB exists
			                    resultOB = TkUtils.getListItem(list_DIEOB,_primary_key+TkDataRequest.strSeparator);
			                	if (resultOB.length() > 0) {
						        	logger.finest("line : "+linecount+ " - [OB : " + _primary_key + " exists]");
						        	
						        	String dataobject_lifecycle = resultOB.split(TkDataRequest.strSeparator)[4];
						        	String stage_closure = TkConfigFile.parameters.get("file_ob_stage_closure").split(TkDataRequest.strSeparator)[0];
						        	
						        	//If project code equals OB project code and stage equal C
						        	if (resultOB.contains(_ob_code_projet) == true && 
						        		_ob_stage.equals(stage_closure) == true/* &&
						        		dataobject_lifecycle.equals(stage_closure) == false*/ ) {
						        		Integer dataobject_id1 = 0;
						        		
						        		try{
											double id_double = Double.parseDouble(resultOB.split(TkDataRequest.strSeparator)[1]);
											dataobject_id1 = (int) id_double;
										} catch(Exception e){
											System.out.println("error: " + e);
											dataobject_id1 = 0;
										}
						        		
						        		String payload = "";
						        		
						        		//Close OB
										if (dataobject_id1 > 0 ) {
											payload  = "{\"id\":0, \"params\":{ " + 
														"\"dataObjectId\":\""+dataobject_id1+"\"" +
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
						        		//Anomaly, do nothing
						        	}
						        	
						        	//Next record
						        	continue;
		        				} else {
		        					//If project code equals OB project code and stage equal C
						        	if (resultOB.indexOf(_ob_code_projet) > -1 || resultOB.toString() == "") {
			        					//Not an anomaly, need to create OB
			        					//System.out.println("line : "+linecount+ " - [OB : " + _primary_key + " does not exists]");
			        					logger.finest("line : "+linecount+ " - [OB : " + _primary_key + " does not exists]");
			        					
			        					for(String item_ : list_DIEOB_inherit_ca){
			        						if (item_.split(TkDataRequest.strSeparator)[1].equals(TkConfigFile.parameters.get("file_ob_primary_key"))) {
			        							TkXMLDocument.listAttributsExt.add(item_.split(TkDataRequest.strSeparator)[0]+TkDataRequest.strSeparator+"N"+TkDataRequest.strSeparator+_primary_key);
			        							break;
			        						}
			        		    		}
			        					
			        					/* INHERITED CA
			        					 id	 	name
			        					 619	DIE_Etiquette_Itineraire
			        					 620	DIE_Code_Itineraire
			        					 621	DIE_Etiquette_Regroupement_Itineraire
			        					 622	DIE_Code_Regroupement
			        					 715	DIE_Etiquette Direction
			        					*/
			        					
			        					//Create OB
			        					TkXMLDocument tkxml = new TkXMLDocument();
									    tkxml.setLogger(logger);
									   
									    XMLstring = tkxml.dataobjectsOBCreationXML(
									    		objectName,
									    		dataobject_id,
									    		_primary_key,
												_ob_description,
												_tk_stage_name,
												pool,
												TkConfigFile.parameters.get("login").split("@")[0],
												"",
												_primary_key,
												true, 
												false, 
												true);
									   
									    logger.finest("dataobjectsCreationXML : XML doc generated.\r\n");
									    logger.finest(XMLstring);
									    if (Boolean.valueOf(TkConfigFile.parameters.get("file_ob_commit")) == true) {
										    if (tk.CreateDataObject(XMLstring, TYPE_REPORT.CREATE_DATAOBJECT) == true) {
										    	logger.finest("OB generated !\r\n");
										    	nbDataObjectCreated += 1;
										    }
									    } else {
									    	logger.info(XMLstring);
									    }
						        	} else {
						        		//Anomaly
			        					logger.finest("line : "+linecount+ " - [OB : " + resultOB + " exist but under another Project code]");
			        					TkUtils.write(csvFileKO, line+cvsSplitBy+TkMessageFile.messages.get("ob.projectcode.wrong"));
			        					
			        					//Next record
			        					continue;
						        	}
		        				}
			            	}
			            }
			            
			            logger.info("\r\n");
		                logger.info("\r Number of procedeed : " + linecount);
		                logger.info("\r Number of OB created : " + nbDataObjectCreated);
		                logger.info("\r Number of OB lifecycle updated : " + nbDataObjectChgStage); 
		                
			        } catch (FileNotFoundException e) {
			            e.printStackTrace();
			        } catch (IOException e) {
			            e.printStackTrace();
			        } finally {
			            if (br != null) {
			                try {
			                    br.close();
			                } catch (IOException e) {
			                    e.printStackTrace();
			                }
			            }
			        }
				  } else if (fileList[i].isDirectory()) {
					logger.finest("Directory " + fileList[i].getName());
				  }
				  
				  Path result = Files.move 
					        (Paths.get(csvFile),  
					        Paths.get(csvFileResult)); 
					  
				  if(result != null) 
				  { 
		            logger.finest("File renamed and moved successfully to results dir"); 
				  } else { 
		            logger.finest("Failed to move the file to results dir"); 
				  } 
				}
				
				tk.logout();
			} else {
				logger.severe("Enable to authenticate to the Triskell");
			}
		} finally {  
			logger.info("End of Process");
    	}
	}
}

