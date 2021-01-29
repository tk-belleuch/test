package eu.triskell.client.sicav;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import eu.triskell.client.sicav.TkConfigFile;

public class TkUtils {

	static Logger logger=Logger.getLogger(TkActions.class.getName());
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		TkUtils.logger = logger;
	}
	
	public static String TkJSONParser(String json, String attr) {
		
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		String str = "";
		try{
			obj = (JSONObject) parser.parse(json);
			str = obj.get(attr).toString();

	      }catch(Exception pe) {
	         logger.severe("Message: " + pe.getMessage());
	     }
		return str;
	  	}
	
	public static Map<String, String> getQueryMap(String query)  
	{  
	    String[] params = query.split("&");
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params)  
	    {  
	        String name = param.split("=")[0];  
	        String value = param.split(name+"=")[1];
	        map.put(name, value);  
	    }  
	    return map;  
	}
	
	public static String getTodaysDate(String format){
		try{
			DateFormat dateFormat = new SimpleDateFormat(format);
			Date today = Calendar.getInstance().getTime();
			String stoday = dateFormat.format(today);
			return stoday;
		}
		catch(Exception e){
			logger.severe(e.getMessage());
			return "";
		}
	}
	
    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }
	
	public static boolean getParameter(String parameter_, boolean paramChecked) {
		
		String param = "";
		
		try{
			param = TkConfigFile.parameters.get(parameter_).toString();
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

	public static String removeLastChar(String str) {
	    return str.substring(0, str.length() - 1);
	}
		
	public static String getListItem(ArrayList<String> tmp, String search) {
		String result = "";
		for(String item_ : tmp){
			//System.out.println(item_);
			if(item_.contains(search)){
				result = item_;
				break;
			} 
	    }
	    
	    return result;
	}
	
	public static void write (String filename, String string) throws IOException{
		  BufferedWriter outputWriter = null;
		  outputWriter = new BufferedWriter(new FileWriter(filename, true));
		  outputWriter.append(string+"\n");
		  outputWriter.flush();  
		  outputWriter.close();  
		}
	

    public static String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0)
            return true;
        return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
    }
    
	public static String getCsvFileColumnData(String line, String columnNumber, String splitBy, boolean battr){
		int csv_id = 0;
		String tk_id = "";
        String[] input = line.split(splitBy);
        String result = "";
        
		try{
			csv_id = Integer.parseInt(TkConfigFile.parameters.get(columnNumber).split(",")[0]);
			
			if (battr) {
				tk_id = TkConfigFile.parameters.get(columnNumber).split(",")[1];
				
				for(String item_ : TkImport_SICAV_OB.list_DIEOB_inherit_ca){
					if (item_.split(TkDataRequest.strSeparator)[1].equals(tk_id)) {
						TkXMLDocument.listAttributsExt.add(item_.split(TkDataRequest.strSeparator)[0]+TkDataRequest.strSeparator+"N"+TkDataRequest.strSeparator+input[csv_id].replace("\"","").trim());
						break;
					}
	    		}
			}
			
			result = input[csv_id].replace("\"","").trim();
			
        } catch(Exception e){
			logger.severe(e.getMessage());
			result = "";
        }
		return result;
	}
	
	public static String getEJFileColumnData(Row row, String columnNumber, boolean battr){
		String xls_col = "";
		String tk_id = "";
        String result = "";
        
		try{
			xls_col = TkConfigFile.parameters.get(columnNumber).split(",")[0];
			
			Cell c = row.getCell(getExcelColumnNumber(xls_col));
			if (c != null) {
				logger.finest("Cell "+columnNumber+" value:" + c.toString());
			}
			
			if (battr) {
				tk_id = TkConfigFile.parameters.get(columnNumber).split(",")[1];
				
				for(String item_ : TkImport_SICAV_Budget_AE.list_DIEEJ_inherit_ca){
					if (item_.split(TkDataRequest.strSeparator)[1].equals(tk_id)) {
						TkXMLDocument.listAttributsExt.add(item_.split(TkDataRequest.strSeparator)[0]+TkDataRequest.strSeparator+"N"+TkDataRequest.strSeparator+c.toString());
						break;
					}
	    		}
			}
			
			result = c.toString();
			
        } catch(Exception e){
			logger.severe(e.getMessage());
			result = "";
        }
		return result;
	}
	   
	public static boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

    public static int getExcelColumnNumber(String column) {
        int result = 0;
        for (int i = 0; i < column.length(); i++) {
            result *= 26;
            result += column.charAt(i) - 'A' + 1;
        }
        return result - 1;
    }

    public static String getExcelColumnName(int number) {
        final StringBuilder sb = new StringBuilder();

        int num = number - 1;
        while (num >=  0) {
            int numChar = (num % 26)  + 65;
            sb.append((char)numChar);
            num = (num  / 26) - 1;
        }
        return sb.reverse().toString();
    }
}


