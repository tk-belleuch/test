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
public class TkImport_SICAV_Budget_CP {

	/**
	 * @param args
	 */
	
	static Logger logger=Logger.getLogger(TkImport_SICAV_Budget_CP.class.getName());
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		TkImport_SICAV_Budget_CP.logger = logger;
	}
	
	static ArrayList<String> list_DIEOB = new ArrayList<>();
	static ArrayList<String> list_DIEOB_TPA_items = new ArrayList<>();
	static ArrayList<String> list_CALENDAR_Months = new ArrayList<>();
	
	static List<CP_input> list_cplines = new ArrayList<>();
	static String cvsSplitBy = ";";
	
	static String TPADetails = null;
	
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
		        int nbTpaItemCreated = 0;
		        int nbTpaItemUpd = 0;
				
				File dir = new File(TkConfigFile.parameters.get("file_cp_path")); 
				FileFilter fileFilter = new WildcardFileFilter(TkConfigFile.parameters.get("file_cp_filename_prefix")+"*"+TkConfigFile.parameters.get("file_cp_ext"), IOCase.INSENSITIVE); 
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
					
					//Get OB TPA items
					logger.finest("Get OB TPA items list");
					list_DIEOB_TPA_items = (ArrayList<String>) tk.GetTimephasedItemList(TkConfigFile.parameters.get("ID_REPORT_GET_DIE_OB_TPA_ITEMS"), TkConfigFile.parameters.get("file_cp_tpa_attr_id"), TkConfigFile.parameters.get("file_cp_tpa_version"), TkConfigFile.parameters.get("file_cp_tpa_cost_type_val_id"), TYPE_REPORT.GET_LIST_TPA_ITEMS);				
					logger.finest("Nb OB TPA items : " + list_DIEOB_TPA_items.size());
				
					//Get Calendar first months list
					logger.finest("Get Calendar all first months list");
					list_CALENDAR_Months = (ArrayList<String>) tk.GetTkFirstMonth(TkConfigFile.parameters.get("ID_REPORT_GET_CALENDAR_MONTHS"), 
																				  TkConfigFile.parameters.get("file_cp_tpa_calendar_id"), 
																				  TkConfigFile.parameters.get("file_cp_tpa_period_type"),
																				  TYPE_REPORT.GET_LIST_MONTHS);
					logger.finest("Nb Calendar first months : " + list_CALENDAR_Months.size());
					
					//Get
					//logger.finest("Get TPA details");
					//TPADetails = tk.GetCustomAttributDetails(TkConfigFile.parameters.get("file_cp_tpa_attr_id"), TkConfigFile.parameters.get("file_cp_tpa_attr_type_id"));
					
					
				} //filesList now contains all the xls files in sorted order
				
				for (int i = 0; i < fileList.length; i++) {
					
					  String xlsFile = dir.toString();
					  String xlsFileResult = dir.toString();
					  if (SystemUtils.IS_OS_WINDOWS) {
						  xlsFileResult = xlsFileResult+"\\"+TkConfigFile.parameters.get("file_cp_result_subdir")+"\\"+fileList[i].getName();
						  xlsFile = xlsFile+"\\"+fileList[i].getName();
					  } else {
						  xlsFileResult = xlsFileResult+"/"+TkConfigFile.parameters.get("file_cp_result_subdir")+"/"+fileList[i].getName();
						  xlsFile = xlsFile+"/"+fileList[i].getName();
					  }
					  
					  xlsFileResult = xlsFileResult.replace(TkConfigFile.parameters.get("file_cp_ext"), "_"+TkUtils.getTodaysDate("yyyyMMddhhmmss")+TkConfigFile.parameters.get("file_cp_ext"));
					  
					  String xlsFileKO = xlsFileResult.replace(TkConfigFile.parameters.get("file_cp_ext"), TkConfigFile.parameters.get("file_cp_ext_result"));
					  
					  
					  if (fileList[i].isFile()) {
						System.out.println("*****************************************");
						System.out.println("Proceed budget CP File " + fileList[i].getName());
						System.out.println("*****************************************");
					    logger.info("*****************************************");
					    logger.info("Proceed budget CP File " + fileList[i].getName());
					    logger.info("*****************************************");
				
						//obtaining input bytes from a file  
						FileInputStream fis=new FileInputStream(new File(xlsFile));
						
						//creating workbook instance that refers to .xls file  
						@SuppressWarnings("resource")
						HSSFWorkbook wb=new HSSFWorkbook(fis);   
						
						System.out.println("Sheet index:" + wb.getSheetIndex(TkConfigFile.parameters.get("file_cp_file_sheet")));
						
						//creating a Sheet object to retrieve the object  
						HSSFSheet sheet=wb.getSheetAt(wb.getSheetIndex(TkConfigFile.parameters.get("file_cp_file_sheet")));  
						
						String _cp_col_mill_prg = TkConfigFile.parameters.get("file_cp_mill_prg_mapping"); //"U";
				    	String _cp_col_code_prg = TkConfigFile.parameters.get("file_cp_code_prg_mapping"); //"V";
				    	String _cp_col_mill_ope = TkConfigFile.parameters.get("file_cp_mill_ope_mapping"); //"X";
				    	String _cp_col_code_ope = TkConfigFile.parameters.get("file_cp_code_ope_mapping"); //"Y";
				    	String _cp_annee_dec 	= TkConfigFile.parameters.get("file_cp_annee_dec_mapping");//"H";
				    	String _cp_mont_dec_2017= TkConfigFile.parameters.get("file_cp_mont_dec_2017_mapping"); //"AE";
				    	String _cp_mont_dec_2018= TkConfigFile.parameters.get("file_cp_mont_dec_2018_mapping"); //"AF";
				    	String _cp_mont_dec_2019= TkConfigFile.parameters.get("file_cp_mont_dec_2019_mapping"); //"AG";
				    	String _cp_mont_dec_2020= TkConfigFile.parameters.get("file_cp_mont_dec_2020_mapping"); //"AH";
				    	String _cp_mont_dec_2021= TkConfigFile.parameters.get("file_cp_mont_dec_2021_mapping"); //"AI";
				    	String _cp_mont_dec_2022= TkConfigFile.parameters.get("file_cp_mont_dec_2022_mapping");	//"AJ";
				    	
				    	String annee_mont_dec_column = "";
				    	String annee_cell = "";
				    	linecount = 0;
				    	
						//evaluating cell type
						for(Row row: sheet)     //iteration over row using for each loop  
						{  
							linecount += 1;
			            	
			                String data = "\r" + anim.charAt(linecount % anim.length()) + " " + linecount;
			                System.out.write(data.getBytes());
			            	
			            	System.out.flush();
			            	
							if (row != null && row.getRowNum() > 1) {
								Cell c1 = row.getCell(TkUtils.getExcelColumnNumber(_cp_col_mill_prg));
								if (c1 != null) {
									logger.finest("Cell Millesime programme value:" + c1.toString());
								}
								Cell c2 = row.getCell(TkUtils.getExcelColumnNumber(_cp_col_code_prg));
								if (c2 != null) {
									logger.finest("Cell Code programme value:" + c2.toString());
								}
								Cell c3 = row.getCell(TkUtils.getExcelColumnNumber(_cp_col_mill_ope));
								if (c3 != null) {
									logger.finest("Cell Millesime operation value:" + c3.toString());
								}
								Cell c4 = row.getCell(TkUtils.getExcelColumnNumber(_cp_col_code_ope));
								if (c4 != null) {
									logger.finest("Cell Code operation value:" + c4.toString());
								}
								Cell c5 = row.getCell(TkUtils.getExcelColumnNumber(_cp_annee_dec));
								
								try {
									annee_cell = c5.toString();
								} catch (Exception e) {
									continue;
								}
								if (annee_cell.equals("2017")) {
									annee_mont_dec_column = _cp_mont_dec_2017;
								} else if (annee_cell.equals("2018")) {
									annee_mont_dec_column = _cp_mont_dec_2018;
								} else if (annee_cell.equals("2019")) {
									annee_mont_dec_column = _cp_mont_dec_2019;
								} else if (annee_cell.equals("2020")) {
									annee_mont_dec_column = _cp_mont_dec_2020;
								} else if (annee_cell.equals("2021")) {
									annee_mont_dec_column = _cp_mont_dec_2021;
								} else if (annee_cell.equals("2022")) {
									annee_mont_dec_column = _cp_mont_dec_2022;
								} else {
									continue;
								}
								
								Cell cmontant = row.getCell(TkUtils.getExcelColumnNumber(annee_mont_dec_column));
								if (cmontant != null) {
									logger.finest("Cell montant "+c5.toString()+" value:" + cmontant.toString());
									
									try {
										list_cplines.add(new CP_input(c1.toString()+c2.toString()+c3.toString()+c4.toString()+TkDataRequest.strSeparator+annee_cell,Double.parseDouble(cmontant.toString())));
									} catch (Exception e) {
										System.out.println(e.toString());
										logger.severe(e.toString());
									}
								}
							}
						}
						
						//Group by : code de l’opération constitué des colonnes 
						// U (Millésime Programme), 
						// V (Code Programme),
						// X (Millésime Opération) 
						// Y (Code Opération)
						System.out.println("*****************************************");
						System.out.println("Proceed budget CP unit aggregation");
						System.out.println("*****************************************");
					    logger.info("*****************************************");
					    logger.info("Proceed budget CP unit aggregation");
					    logger.info("*****************************************");
					    
			            Map<String, Double> _inputByKey = new HashMap<>();
			            
			            System.out.println("\n");
			            System.out.println("=====>> Control : Number of Line to proceed : " + list_cplines.size());
			            logger.info("\r=====>> Control : Number of Line to proceed : " + list_cplines.size());
			            
			            _inputByKey =  list_cplines.stream().collect(
		                         Collectors.groupingBy(CP_input::getKey, Collectors.summingDouble(CP_input::getunits)));
			            
			            logger.finest("Lines grouped by key : " + _inputByKey);
			            
			            System.out.println("\n");
			            System.out.println("=====>> Control : Number of Line to proceed (after grouping by key) : " + _inputByKey.size());
			            logger.info("\r=====>> Control : Number of Line to proceed (after grouping by key) : " + _inputByKey.size());
			            
			            System.out.println("*****************************************");
						System.out.println("Proceed budget CP unit import");
						System.out.println("*****************************************");
					    logger.info("*****************************************");
					    logger.info("Proceed budget CP unit import");
					    logger.info("*****************************************");
					    
			            String _units = "";
			            String result = "";
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
			            	
			            	String _num_OB = entry.getKey().split(TkDataRequest.strSeparator)[0];
			            	annee_cell = entry.getKey().split(TkDataRequest.strSeparator)[1];
			                
			                //Check OB exists with code
			                result = TkUtils.getListItem(list_DIEOB, _num_OB);
			        		if (result.length() > 0) {
					        	logger.finest("OB : "+ _num_OB + " exists]");
					        	
					        	String dataobject_id = result.split(TkDataRequest.strSeparator)[1];
					        	String timephaseditem_id = "";
					        	
					        	//Contrôle existance du code de l'item Investissement dans le TPA DIE_Budget, version Décaissé
					        	result = TkUtils.getListItem(list_DIEOB_TPA_items, _num_OB);
				        		if (result.length() > 0) {
						        	logger.finest("OB (ligne investissement) : "+ _num_OB + " exists]");
						        	
						        	//On met à jour (plus bas)
						        	timephaseditem_id = result.split(TkDataRequest.strSeparator)[2];
		        				} else {
		        					//On créé
		        					logger.finest("OB (ligne investissement) : create");
		        					success = tk.AddTimephasedItem(dataobject_id, 
				        							TkConfigFile.parameters.get("file_cp_tpa_attr_id"), 
				        							TkConfigFile.parameters.get("file_cp_tpa_version"),
				        							TkConfigFile.parameters.get("file_cp_tpa_cost_type_attr_id"),
				        							TkConfigFile.parameters.get("file_cp_tpa_cost_type_val_id"));
		        					//Récupérer item id
		        					if (success) {
		        						timephaseditem_id = (String) tk.getResult();
		        						nbTpaItemCreated += 1;
		        					}
		        				}
				        		
				        		result = TkUtils.getListItem(list_CALENDAR_Months, annee_cell); 
				        		
				        		//update
				        		logger.finest("OB (ligne investissement) : update");
				        		success = tk.SaveTimephasedData(dataobject_id,
				        										TkConfigFile.parameters.get("file_cp_tpa_attr_id"), 
							        							TkConfigFile.parameters.get("file_cp_tpa_version"),
							        							timephaseditem_id,
							        							result.split(TkDataRequest.strSeparator)[1],
																_units,
																TkConfigFile.parameters.get("file_cp_tpa_input_currency"));
	        					//Check update sucess
	        					if (success) {
	        						nbTpaItemUpd += 1;
	        					}
	        				} else {
	        					//Anomaly
	        					logger.finest("OB : "+ _num_OB+ " does not exists]");
	        					TkUtils.write(xlsFileKO, "OB : "+_num_OB+cvsSplitBy+TkMessageFile.messages.get("ob.notexists"));
	        					
	        					//Next record
	        					continue;
	        				}
			            }
			            
			            logger.info("\r\n");
		                logger.info("\r Number of procedeed : " + linecount);
		                logger.info("\r Number of CP TPA line created : " + nbTpaItemCreated);
		                logger.info("\r Number of CP TPA line updated : " + nbTpaItemUpd);
		                
					} else if (fileList[i].isDirectory()) {
						logger.finest("Directory " + fileList[i].getName());
					}
				  
					Path result = Files.move 
						        (Paths.get(xlsFile),  
						         Paths.get(xlsFileResult));
					  
				    if(result != null) 
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

   class CP_input{
		private String key;
		private Double units;
		
		public CP_input(String key, Double units) {
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
			final CP_input other = (CP_input) obj;
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
