package eu.triskell.client.sicav;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author vcailleaud
 
 */
public class TkXMLDocument {

	static Logger logger=Logger.getLogger(TkActions.class.getName());
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		TkUtils.logger = logger;
	}
	
	static ArrayList<String> listAttributs = new ArrayList<>();
	static ArrayList<String> listAttributsExt = new ArrayList<>();
	static ArrayList<String> listRelationships = new ArrayList<>();
	
	public String usersCreationXML (String code,
									String name,
									String email,
									String Matricule,
									String Function) {
		
        String itemsXMLStringValue = null;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        logger.finest("----------XML doc build starts-----------.\r\n");
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            // Create users root element 
            Element usersRootElement = doc.createElement("USERS");
            doc.appendChild(usersRootElement);
            							                            
            // Create child user Element
            Element users = doc.createElement("USER");
            usersRootElement.appendChild(users);
                        
            Element codeElement = doc.createElement("CODE");
            codeElement.appendChild(doc.createTextNode(code));
            users.appendChild(codeElement);
            
            Element nameElement = doc.createElement("NAME");
            nameElement.appendChild(doc.createTextNode(name));
            users.appendChild(nameElement);
            
            Element emailElement = doc.createElement("EMAIL");
            emailElement.appendChild(doc.createTextNode(email));
            users.appendChild(emailElement);
            
            Element passwordElement = doc.createElement("PASSWORD");
            passwordElement.appendChild(doc.createTextNode(""));
            users.appendChild(passwordElement);
            
            Element languageElement = doc.createElement("LANGUAGECODE");
            languageElement.appendChild(doc.createTextNode(TkConfigFile.parameters.get("tk_user_LANGUAGECODE")));
            users.appendChild(languageElement);
            
            Element calendarElement = doc.createElement("CALENDAR");
            calendarElement.appendChild(doc.createTextNode(TkConfigFile.parameters.get("tk_user_CALENDAR")));
            users.appendChild(calendarElement);
            
            Element activeElement = doc.createElement("ACTIVE");
            activeElement.appendChild(doc.createTextNode(TkConfigFile.parameters.get("tk_user_ACTIVE")));
            users.appendChild(activeElement);
            
            Element timesheetElement = doc.createElement("TIMESHEET");
            timesheetElement.appendChild(doc.createTextNode(TkConfigFile.parameters.get("tk_user_TIMESHEET")));
            users.appendChild(timesheetElement);
            
            Element configuratorElement = doc.createElement("CONFIGURATOR");
            configuratorElement.appendChild(doc.createTextNode(TkConfigFile.parameters.get("tk_user_CONFIGURATOR")));
            users.appendChild(configuratorElement);
            
            Element attributesListElement = doc.createElement("ATTRIBUTES");
            users.appendChild(attributesListElement);
            
            Element attribute1Element = doc.createElement("ATTRIBUTE");
            attributesListElement.appendChild(attribute1Element);
            
            Element attributeProfileElement = doc.createElement("NAME");
            attributeProfileElement.appendChild(doc.createTextNode("Matricule"));
            attribute1Element.appendChild(attributeProfileElement);
            
            Element attribute1ValueElement = doc.createElement("VALUE");
            attribute1ValueElement.appendChild(doc.createTextNode(Matricule));
            attribute1Element.appendChild(attribute1ValueElement);
            
            Element attribute2Element = doc.createElement("ATTRIBUTE");
            attributesListElement.appendChild(attribute2Element);
            
            Element attributeCostTypeElement = doc.createElement("NAME");
            attributeCostTypeElement.appendChild(doc.createTextNode("Fonction"));
            attribute2Element.appendChild(attributeCostTypeElement);
            
            Element attribute2ValueElement = doc.createElement("VALUE");
            attribute2ValueElement.appendChild(doc.createTextNode(Function));
            attribute2Element.appendChild(attribute2ValueElement);
                        
            // Transform Document to XML String
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            
            // Get the String value of final xml document
            itemsXMLStringValue = writer.getBuffer().toString();
            
            logger.finest("--------XML doc builds ends---------\r\n");
        } catch (ParserConfigurationException | TransformerException e) {
            logger.severe(e.getMessage());
        }

        return itemsXMLStringValue;
    }
	
	public String timesheetCreationXML (Map<String, Double> datas, String tsuser, String tspath)
	  {
        String itemsXMLStringValue = null;
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        logger.finest("----------XML doc build starts-----------.\r\n");
		DocumentBuilder domBuilder = null;
		
        String _user = "";
        String _path = "";
        String _startDate = "";
        String _units = "";
        
		try
		{
		  domBuilder = domFactory.newDocumentBuilder();

			Document newDoc = domBuilder.newDocument();
			
			Element rootElement = newDoc.createElement("p:timesheets");
			rootElement.setAttribute("xmlns:p", "https://ondemand.triskellsoftware.com/xsd/timesheet");
			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:schemaLocation","https://ondemand.triskellsoftware.com/xsd/timesheet timesheet-1.0.xsd ");
			
			newDoc.appendChild(rootElement);
			
			Element userElement = newDoc.createElement("user");
			rootElement.appendChild(userElement);
			
			Element typeElement = newDoc.createElement("type");
			typeElement.appendChild(newDoc.createTextNode(TkConfigFile.parameters.get("file_user_column_type")));
			userElement.appendChild(typeElement);
			
			Element valueElement = newDoc.createElement("value");
			valueElement.appendChild(newDoc.createTextNode(tsuser));
			userElement.appendChild(valueElement);
			
			Element objectElement = newDoc.createElement("object");
			userElement.appendChild(objectElement);
			
			Element pathElement = newDoc.createElement("path");
			pathElement.appendChild(newDoc.createTextNode(tspath));
			objectElement.appendChild(pathElement);
			
			Element timesheetElement = newDoc.createElement("timesheet");
			objectElement.appendChild(timesheetElement);
	        
			
			for (Map.Entry<String,Double> entry : datas.entrySet()) {
				
				Double d = entry.getValue();
            	_units = String.valueOf(d);
				String[] input = entry.getKey().split(TkConfigFile.parameters.get("file_separator"));
                
                _user  		= input[0].trim();
            	_path   	= input[1].trim();
            	_startDate  = input[2].trim();
            	
            	if (_user.equals(tsuser) && _path.equals(tspath)) {
				
					Element dataElement = newDoc.createElement("data");
					timesheetElement.appendChild(dataElement);
					
					// Creating a random UUID (Universally unique identifier).
			        UUID uuid = UUID.randomUUID();
			        String randomUUIDString = uuid.toString();
			        
					Element timesheetidElement = newDoc.createElement("timesheetid");
					timesheetidElement.appendChild(newDoc.createTextNode(randomUUIDString));
					dataElement.appendChild(timesheetidElement);
					/*  
					Element attributesElement = newDoc.createElement("attributes");
					dataElement.appendChild(attributesElement);
					  
					Element attributeElement = newDoc.createElement("attribute");
					attributesElement.appendChild(attributeElement);
					  
					Element nameElement = newDoc.createElement("name");
					nameElement.appendChild(newDoc.createTextNode("Input Type"));
					attributeElement.appendChild(nameElement);
					
					
					Element valueAttributeElement = newDoc.createElement("value");
					valueAttributeElement.appendChild(newDoc.createTextNode(tksBean.getInputType()));
					attributeElement.appendChild(valueAttributeElement);*/
					  
					Element valueTimesheetElement = newDoc.createElement("value");
					valueTimesheetElement.appendChild(newDoc.createTextNode(_startDate));
					dataElement.appendChild(valueTimesheetElement);
					  
					Element unitsElement = newDoc.createElement("units");
					unitsElement.appendChild(newDoc.createTextNode(_units));
					dataElement.appendChild(unitsElement);
					  
					Element etcElement = newDoc.createElement("etc");
					etcElement.appendChild(newDoc.createTextNode("0"));
					dataElement.appendChild(etcElement);
            	}
			}
			
	        // Transform Document to XML String
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        StringWriter writer = new StringWriter();
	        transformer.transform(new DOMSource(newDoc), new StreamResult(writer));
	        
	        // Get the String value of final xml document
	        itemsXMLStringValue = writer.getBuffer().toString();
	        
	        logger.finest("--------XML doc builds ends---------\r\n");
	  } catch (ParserConfigurationException | TransformerException e) {
          logger.severe(e.getMessage());
      }

      return itemsXMLStringValue;

	}

	public String dataobjectsOBCreationXML (
			String objectName,
			String parentId,
			String dataobjectName,
			String description,
			String stage,
			String pool,
			String administrators,
			String relations,
			String primaryKey,
			boolean bInheritances, 
			boolean bRelations, 
			boolean bRoles ) {
		
        String dataobjectsXMLStringValue = null;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        logger.finest(".XML doc starts.\r\n");
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            // Create dataobjects root element 
            Element dataobjectsRootElement = doc.createElement("dataobjects");
            doc.appendChild(dataobjectsRootElement);           
			
			String fieldSource = "";
			String fieldTarget = "";
			String name = "";
			String ids = "";
				
            // Create child dataobject Element
            Element dataobject = doc.createElement("dataobject");
            dataobjectsRootElement.appendChild(dataobject);
            
            // Create object Element
            Element objectElement = doc.createElement("object");
            objectElement.appendChild(doc.createTextNode(objectName));
            dataobject.appendChild(objectElement);
            logger.finest("objectName :"+objectName+ "\r\n");
            
            // Create name Element
            Element nameElement = doc.createElement("name");
            nameElement.appendChild(doc.createTextNode(dataobjectName));
            dataobject.appendChild(nameElement);
            logger.finest("dataobjectName :"+dataobjectName+ "\r\n");
            
            // Create description Element
            if (description.length() > 255) {
            	description = description.substring(0, 254);
            }
            Element descriptionElement = doc.createElement("description");
            descriptionElement.appendChild(doc.createTextNode(description));
            dataobject.appendChild(descriptionElement);
            logger.finest("description :"+description+ "\r\n");
            
            // Create stage Element
            Element stageElement = doc.createElement("stage");
            stageElement.appendChild(doc.createTextNode(stage));
            dataobject.appendChild(stageElement);
            logger.finest("stage :"+stage+ "\r\n");
            
            // Create pool Element
            Element poolElement = doc.createElement("pool");
            poolElement.appendChild(doc.createTextNode(pool));
            dataobject.appendChild(poolElement);
            logger.finest("pool :"+pool+ "\r\n");
            
            // Create parentid Element
            Element parentidElement = doc.createElement("parentid");
            parentidElement.appendChild(doc.createTextNode(parentId));
            dataobject.appendChild(parentidElement);
            logger.finest("parentid :"+parentId+ "\r\n");
            
            // Create currency Element
            Element currencyElement = doc.createElement("currency");
            currencyElement.appendChild(doc.createTextNode(TkConfigFile.parameters.get("file_ob_currency")));
            dataobject.appendChild(currencyElement);
            logger.finest("currency :"+TkConfigFile.parameters.get("file_ob_currency")+"\r\n");
            
            logger.finest("bInheritances/count :"+bInheritances+"/"+listAttributs.size()+"\r\n");
            
            // Create attributes Elements
            if (listAttributs.size() > 0 & bInheritances) {
            	
            	//Get dataObject details from parent
                //String dataObjectDetails = null;
            	TkActions.dataObjectDetails = TkActions.GetDataObjectDetails(parentId);
                logger.finest("dataObjectDetails :"+TkActions.dataObjectDetails + "\r\n");
	            
            	JSONParser parser = new JSONParser();
        		JSONObject obj = null;
        		
        		try {
        			obj = (JSONObject) parser.parse(TkActions.dataObjectDetails);
        		} catch(Exception e){
        			e.printStackTrace();
        		}
            	
        		JSONObject objData = null;	
    			try{
    				objData = (JSONObject) obj.get("data");	
    			} catch(Exception e){}
    			
    			if(objData != null){  					
					// Create attributes Element
		            Element attributesElement = doc.createElement("attributes");
		            dataobject.appendChild(attributesElement);
		            
					String value = "";
					String attrName = "";
											
					for(String attrExt : listAttributsExt){
						attrName = attrExt.split(TkDataRequest.strSeparator)[0];
	
						if(attrExt.split(TkDataRequest.strSeparator)[1].equals("I")) {
							try{
								value = objData.get(attrName.toLowerCase()).toString();
							} catch(Exception e){
								value = "";
							}
						} else if (attrExt.split(TkDataRequest.strSeparator)[1].equals("N")) {
							value = attrExt.split(TkDataRequest.strSeparator)[2];
						} else if (attrExt.split(TkDataRequest.strSeparator)[1].equals("E")) {
							try{
								fieldSource = attrExt.split(TkDataRequest.strSeparator)[0];
								logger.finest("fieldSource: " + fieldSource);
							} catch(Exception e){
								fieldSource = "";
							}
							
							try{
								fieldTarget = attrExt.split(TkDataRequest.strSeparator)[2];
								logger.finest("fieldTarget: " + fieldTarget);
							} catch(Exception e){
								fieldTarget = "";
							}
							
							attrName = fieldTarget;
							
							try{
								logger.finest("date: " + objData.toJSONString());
								value = objData.get(fieldSource.toLowerCase()).toString();
								logger.finest("value: " + value);
							} catch(Exception e){
								value = "";
							}
						}
						
						if(attrName.length() > 0 & value.length() > 0) {
							// Create attribute Element
							Element attributeElement = doc.createElement("attribute");
				            attributesElement.appendChild(attributeElement);
				            
				            // Create name Element
				            Element nameAttElement = doc.createElement("name");
				            nameAttElement.appendChild(doc.createTextNode(attrName));
				            attributeElement.appendChild(nameAttElement);
				            
				            if (attrName.toUpperCase().contains("DATE")) {
				            	Date d = new Date(Long.parseLong(value));
				            	String tmp = new SimpleDateFormat("yyyy.MM.dd").format(d);
				            	value = tmp;
				            }
				            
				            // Create value Element
				            Element valueElement = doc.createElement("value");
				            valueElement.appendChild(doc.createTextNode(value));
				            attributeElement.appendChild(valueElement);
						}
					}
    			}
            }
            
            logger.finest("bRelations :"+bRelations+"\r\n");
            
            if (bRelations) {
            	         	
                // IF relationships 
	            if (relations.length() > 0) {
	            	logger.finest("bRelations/count :"+relations.length()+"\r\n");
	            	
	            	// Create relationships Element
		            Element relationshipsElement = doc.createElement("relationships");
		            dataobject.appendChild(relationshipsElement);
		            String[] rels = relations.split(";");
                	for (String r: rels) {
		            	name = r.split(",")[0];
		            	ids = r.split(",")[1];
		            	List<String> values = Arrays.asList(ids.split("[\\s;]+"));

			            for (String t: values) {  
			            	//logger.finest("...3\r\n");
			            	// Create relationship Element
				            Element relationshipElement = doc.createElement("relationship");
				            relationshipsElement.appendChild(relationshipElement);
				            
				            // Create dataobjectid Element
				            Element dataobjectidElement = doc.createElement("dataobjectid");
				            dataobjectidElement.appendChild(doc.createTextNode(t));
				            relationshipElement.appendChild(dataobjectidElement);
				            
				            // Create type Element
				            Element typeElement = doc.createElement("type");
				            typeElement.appendChild(doc.createTextNode(name));
				            relationshipElement.appendChild(typeElement);
			            }
		            }
                }
            }
            //logger.finest("...4\r\n");
            logger.finest("bRoles :"+bRoles+"\r\n");
            
            if (bRoles) {
                // Create user_roles Elements
	            if (administrators.length() > 0) {
	            	
	            	// Create attributes Element
		            Element user_rolesElement = doc.createElement("user_roles");
		            dataobject.appendChild(user_rolesElement);
		            
		            String[] admins = administrators.split(";");
		            for (String s: admins) {           
			            if (s.length() > 0) {
			            	// Create user_role Element
				            Element user_roleElement = doc.createElement("user_role");
				            user_rolesElement.appendChild(user_roleElement);
				            
				            // Create user_code Element
				            Element user_codeElement = doc.createElement("user_code");
				            user_codeElement.appendChild(doc.createTextNode(s));
				            user_roleElement.appendChild(user_codeElement);
				            
				            // Create role Element
				            Element roleElement = doc.createElement("role");
				            roleElement.appendChild(doc.createTextNode(TkConfigFile.parameters.get("file_ob_administrator")));
				            user_roleElement.appendChild(roleElement);
			            }
		            }			            
	            }
            }
            
            // Transform Document to XML String
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            
            // Get the String value of final xml document
            dataobjectsXMLStringValue = writer.getBuffer().toString();
            
            logger.finest(".XML doc ends.\r\n");
        } catch (ParserConfigurationException | TransformerException e) {
        	logger.severe("Error :"+e.getMessage()+"\r\n");
        }
        logger.finest("dataobjectsXMLStringValue = " + dataobjectsXMLStringValue);
        return dataobjectsXMLStringValue;
    }

	public static String extractUserInfo (String userInfo, String param) {
		String result = "";
		String temp = "";
		Integer x = 0;
		
		String[] params = param.split("\\s* \\s*");
		for (String p : params) {
			x = userInfo.indexOf(p+" :") + p.length() + 2;
			temp = userInfo.substring(x);
			logger.finest("param(p) : "+p+"\r\n");
			logger.finest("temp  : "+temp+"\r\n");		
			
			try{
				if(temp.indexOf(",")>0) {
					if(result.length()>0) {
						result = result + " " + userInfo.substring(x, x+temp.indexOf(","));
					} else {
						result = userInfo.substring(x, x+temp.indexOf(","));
					}
				} else {
					if(result.length()>0) {
						result = result + " " + userInfo.substring(x, x+temp.length());
					} else {
						result = userInfo.substring(x, x+temp.length());
					}
				}
			} catch(Exception e){
				logger.severe(e.getMessage());
				result = "";
			}
		}
		
		return result;
	}
}