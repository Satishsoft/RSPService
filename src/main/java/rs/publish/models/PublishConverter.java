package rs.publish.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.internal.util.Strings;
@Service
public class PublishConverter {
	private static String queryType="";
	private static String querySearch="";
	private static String queryLocale="";
	private static String queryContext="";
	Map<String,String> attributeExternalList=new HashMap();
	
	public LoginResponse loginInfo(String userName, String password) {
		LoginResponse loginResponse = new LoginResponse();
		try {
			String name = null;
			JsonObject user_jsonObject = RSRestConnector.getUserDetails(userName, password);
			if (user_jsonObject != null) {
					name = user_jsonObject.get("name").getAsString();
				}
			if (name != null && !name.isEmpty() && name.equalsIgnoreCase(userName)) {
				UserProfile profile = new UserProfile();
				String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
				loginResponse.setSessionId(sessionId);
				loginResponse.setServerVersion("3.0.3.622");
				loginResponse.setUserProfile(profile);
				loginResponse.setLoginSuccess(true);
			} else {
				loginResponse.setLoginSuccess(false);
				loginResponse.setLoginFaultMessage("Invalid username or password");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return loginResponse;
	}

	public FinalResult getEntities(String query) throws IOException {
		FinalResult finalResult = new FinalResult();
		queryType="";
		querySearch="";
		queryLocale="";
		queryContext="";
		if(!query.isEmpty())getQueries(query);
		 Constants.setQueryLocaleContext(queryLocale,queryContext);
		if(attributeExternalList.size()<=0) {
			if(queryType.isEmpty()) {
				 attributeExternalList=getAttributeExternalName(Constants.TYPES.split(","));
		     }
		    else {
		    	if(queryType.contains(",")) {
		    		attributeExternalList=getAttributeExternalName(queryType.split(","));
		    	}
		    	else {
		    		String [] typeList=new String[1];
		    		typeList[0]=queryType;
		    	attributeExternalList=getAttributeExternalName(typeList);
		    	}
		    }
		}
		if(attributeExternalList!=null && attributeExternalList.size()>0) {
			finalResult=getEntitiesDetails(queryType,querySearch,attributeExternalList);
		}
		return finalResult;
	}
	
	private Map<String,String> getAttributeExternalName(String[]types) throws IOException {
		Map<String,String> attributeExternameList= new HashMap<String,String>();
		if(types.length>0) {	
			for (String key : types)
            {
				JsonObject json_EntityModel=RSRestConnector.getEntityManageObject(key);
				if(json_EntityModel!=null) {
					attributeExternameList.putAll(JsonObjectHelper.getAttributeExternalName(json_EntityModel, attributeExternameList));
				}
            }
		}
		return attributeExternameList;
	}
	
	private FinalResult getEntitiesDetails(String query,String querySearch,Map<String,String> attributeExternalList) {
		FinalResult finalResult = new FinalResult();
		try {
			List<Map<String, String>> totalAttribute_List = new ArrayList<Map<String, String>>();
			List<Map<String, String>> attributeList = null;
			JsonArray entitiesArray = null;
			List<JsonArray> response = RSRestConnector.getEntities(query,querySearch);
			if (response != null && !response.isEmpty() && response.size() > 0) {
				int recordcount = 0;
				String id="";
				String type="";
				finalResult.setMaxResultHits(100000);
				finalResult.setFirstResult(0);
				for (int r = 0; r < response.size(); r++) {
					entitiesArray = (JsonArray) response.get(r);
					if (entitiesArray != null && entitiesArray.isJsonArray()) {
						Map<String, String> attributes = null;
						recordcount = recordcount + entitiesArray.size();
						for (int i = 0; i < entitiesArray.size(); i++) {
							attributeList = new ArrayList<Map<String, String>>();
							JsonObject entity_Object = entitiesArray.get(i).getAsJsonObject();
							JsonObject attributes_Object;
							if(Constants.READFROMCONTEXT){
								attributes_Object = JsonObjectHelper.getContextAttributesJsonObjectFromEntityObject(entity_Object,Constants.CONTEXTTYPE,Constants.CONTEXTNAME);
								if (attributes_Object==null || attributes_Object.isJsonNull()) {
									attributes_Object = JsonObjectHelper.getAttributesJsonObjectFromEntityObject(entity_Object);
								}
							}
							else
							{
								attributes_Object = JsonObjectHelper.getAttributesJsonObjectFromEntityObject(entity_Object);
							}
							if(attributes_Object != null) {
								attributes = new Hashtable<String, String>();
								type=entity_Object.getAsJsonPrimitive("type").getAsString();
								id=entity_Object.getAsJsonPrimitive(Constants.id).getAsString();
								attributes.put(Constants.type,  type);
								if(entity_Object.get("name")!=null)attributes.put(Constants.name,  entity_Object.getAsJsonPrimitive("name").getAsString());
								attributes.put(Constants.id, id);
								Map<String, String> attribute_Values = getAttributes(attributes_Object,attributeExternalList);
								if (attribute_Values != null && attribute_Values.size() > 0){
										attributes.putAll(attribute_Values);
								}
							}
							JsonObject relationships;
							if(Constants.READFROMCONTEXT){
								relationships = JsonObjectHelper.getContextAttributesJsonObjectFromEntityObject(entity_Object,Constants.CONTEXTTYPE,Constants.CONTEXTNAME);
								if(relationships==null || relationships.isJsonNull()) {
									relationships = JsonObjectHelper.getRelationshipsJsonObjectFromEntityObject(entity_Object);
								}
							}
							else
							{
								relationships = JsonObjectHelper.getRelationshipsJsonObjectFromEntityObject(entity_Object);
							}
							if (relationships != null) {
								Map<String, String> relationship_Values=getRelatiionshipDetails(id,type);
								//Map<String, String> relationship_Values=getChildRelatiionshipDetails(relationships);
								if(relationship_Values!=null && relationship_Values.size()>0) {
									attributes.putAll(relationship_Values);
								}
							}
							if(attributes!=null && attributes.size()>0) {
								Map<String,String> attributesSortedList= new TreeMap<String,String>(attributes);
								attributeList.add(attributesSortedList);
								totalAttribute_List.addAll(attributeList);
							}
						}
					}
				}
				finalResult.setTotalHits(recordcount);
				finalResult.setHits(totalAttribute_List);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return finalResult;
	}
		
	private Map<String, String> getAttributes(JsonObject json_Attributes,Map<String,String> attributeExternameList){
        Map<String,List<JsonElement>> attributesdetails=JsonObjectHelper.getAttributeValuesFromAttributes(json_Attributes);
        if(!attributesdetails.isEmpty()){
        	return fillAttributedetails(attributesdetails,attributeExternameList);
        }
        return null;
    }
		
	@SuppressWarnings({ "rawtypes", "unchecked"})
	private Map<String, String> fillAttributedetails (Map<String,List<JsonElement>> attributesdetails,Map<String,String> attributeExternameList){       
		Map<String, String> attributes_values=new HashMap<String, String>();
		if(attributesdetails.size()>0) {
        	for ( Map.Entry entityjson : attributesdetails.entrySet()) {
        		if(entityjson!=null) {
        			String attributeName=(String)entityjson.getKey();
        			String att_Externalname=  attributeExternameList.get(attributeName);
        			List<JsonElement> JsonValueList=(List<JsonElement> )entityjson.getValue();
        			if(JsonValueList!=null && JsonValueList.size()>0 && JsonValueList.size()==1){
        				JsonElement valueObject=   JsonValueList.get(0);
        				if (valueObject!=null) {
        					if(!valueObject.isJsonArray()) {
        						if(att_Externalname!=null) {
        							attributes_values.put(att_Externalname, valueObject.getAsJsonObject().getAsJsonPrimitive(Constants.VALUE).getAsString());
        						}
        						else{
        							attributes_values.put(attributeName, valueObject.getAsJsonObject().getAsJsonPrimitive(Constants.VALUE).getAsString());
        						}
        					}
        					else{
        						String groupObject=  getGroupAttributeDetails(valueObject.getAsJsonArray(),attributeExternameList);
        						if(att_Externalname!=null) {
                					attributes_values.put(att_Externalname, groupObject);
                				}
                				else{
                					attributes_values.put(attributeName,  groupObject);
                				}
        						
        					}
        				}
        			}
        			else if (JsonValueList!=null && JsonValueList.size()>1){
        				String value="";
        				for (JsonElement valueObject:JsonValueList) {
        					if (valueObject != null) {
        						if (value.isEmpty()) {
        							value = valueObject.getAsJsonObject().getAsJsonPrimitive(Constants.VALUE).getAsString();
        						}
        						else {
        							value = value + "\r" + valueObject.getAsJsonObject().getAsJsonPrimitive(Constants.VALUE).getAsString();
        						}
        					}
        				}
        				if(att_Externalname!=null) {
        					attributes_values.put(att_Externalname, value);
        				}
        				else{
        					attributes_values.put(attributeName,  value);
        				}
                    }
                }
            }
		}
        return attributes_values;
    }
	
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	private String getGroupAttributeDetails(JsonArray attribues_Group_Array,Map<String,String> attributeExternameList) {
		List<Map<String,String>> groupList= new ArrayList<Map<String,String>>();
		Map<String,String> colvalue;
		List<String> heading=new ArrayList();
		if(attribues_Group_Array!=null && attribues_Group_Array.size()>0){
			Iterator var3 = attribues_Group_Array.iterator();
            while (var3.hasNext()) {
                JsonObject jsonattribute=  (JsonObject) var3.next();
                if(jsonattribute!=null) {
                	colvalue=new HashMap<>();
                	for (Iterator<?> iterator = jsonattribute.keySet().iterator(); iterator.hasNext(); ) {
                		String child_attribues_name = (String) iterator.next();
                		JsonElement checkJsonObject=jsonattribute.get(child_attribues_name);
                		if(checkJsonObject.isJsonObject()){
                			if(!heading.contains(child_attribues_name))heading.add(child_attribues_name);
                			JsonObject group_Values =  jsonattribute.get(child_attribues_name).getAsJsonObject();
                			JsonArray group_Values_Array =  group_Values.get("values").getAsJsonArray();
                			if (group_Values_Array != null && group_Values_Array.size() > 0) {
                				colvalue=FillValues(colvalue,child_attribues_name,group_Values_Array);
                			}
                		}
                	}
                	Map<String,String> map = new TreeMap<String,String>(colvalue);
                	groupList.add(map);
                }
            }
		}
	List<String> headingSortedList= heading.stream().sorted().collect(Collectors.toList());
	return getTableFormat(headingSortedList,groupList,attributeExternameList);
	}
	 
	private Map<String, String> FillValues(Map<String, String> List, String attName, JsonArray attribues_Values_Array1)
	{
		try
		{
			if (attribues_Values_Array1 != null && attribues_Values_Array1.size() > 0) {
				String value = "";
				for (int att = 0; att < attribues_Values_Array1.size(); att++) {
					JsonObject value_list =  attribues_Values_Array1.get(att).getAsJsonObject();
					if(value_list!=null){
						if (value == "") {
							if( value_list.get("value") != null)
								value = value_list.get("value").getAsString();
						}
						else {
							if(value_list.get("value") !=null)
								value = value + "\r" + value_list.get("value").getAsString();
						}
					}
				}
				if (!List.containsKey(attName)) {
					List.put(attName, value);
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
			return List;
	}
	
	private String getTableFormat(List<String> headingSortedList,List<Map<String,String>> groupList,Map<String,String> attributeExternameList) {
		String tablevalue = "";
		String tdkey = "";
		String tdvalue = "";
		String trvalue="";
		String att_Externalname;
        try
        {
        	for (String key : headingSortedList)
            {
        		att_Externalname=attributeExternameList.get(key);
        		if(att_Externalname==null)
        			tdkey += "<th>" + key + "</th>";
        		else
                tdkey += "<th>" + attributeExternameList.get(key) + "</th>";
            }
        	for(int j=0;j<groupList.size();j++) {
        		for(int i=0;i<headingSortedList.size();i++) {
        			if(!groupList.get(j).containsKey(headingSortedList.get(i))){
        				tdvalue+="<td></td>";
        			}
        			else{
        				tdvalue+="<td>"+groupList.get(j).get(headingSortedList.get(i)).toString()+"</td>";
        			}
        		}
        		trvalue+="<tr>"+tdvalue+"</tr>";
        		tdvalue="";
        	}
        	tablevalue = "<table><tr>" + tdkey + "</tr>" + trvalue + "</table>";
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
        return tablevalue;
	}
	
	private Map<String, String> getRelatiionshipDetails(String id,String type) throws IOException {
		Map<String, String> relatiionshipList=new HashMap<String, String>();
		JsonObject relationships_Object=RSRestConnector.getImageObject(id, type);
		JsonObject relationshipInfo = JsonObjectHelper.getRelationshipsJsonObjectFromEntityObject(relationships_Object);
		if(relationshipInfo!=null) {		
			if(Constants.RELATIONSHIPALL){
            Map<String,JsonArray> relationshipList=getRelationshipList(relationshipInfo);
            if(relationshipList.size()>0) {
                for (Map.Entry<String, JsonArray> entry : relationshipList.entrySet()) {
                    if (entry.getKey().equals(Constants.IMAGESRelation) || entry.getKey().equals(Constants.DOCUMENTRelation)) {
                    	relatiionshipList.putAll(getImageRelationship(entry.getValue(), entry.getKey()));
                    }
                   else {
                	   relatiionshipList.putAll(getSKURelationship(entry.getValue(), entry.getKey()));
                    }
                }
            }
        }
        else {
        	if(relationshipInfo.getAsJsonArray(Constants.IMAGESRelation)!=null && relationshipInfo.getAsJsonArray(Constants.IMAGESRelation).size()>0) {
	            relatiionshipList.putAll(getImageRelationship(relationshipInfo.getAsJsonArray(Constants.IMAGESRelation), Constants.IMAGESRelation));
	        }
	        if(relationshipInfo.getAsJsonArray(Constants.DOCUMENTRelation)!=null && relationshipInfo.getAsJsonArray(Constants.DOCUMENTRelation).size()>0) {
	            relatiionshipList.putAll(getImageRelationship(relationshipInfo.getAsJsonArray(Constants.DOCUMENTRelation), Constants.DOCUMENTRelation));
	        }
	        if(relationshipInfo.getAsJsonArray(Constants.CHILDOFRelation)!=null && relationshipInfo.getAsJsonArray(Constants.CHILDOFRelation).size()>0) {
	            relatiionshipList.putAll(getSKURelationship(relationshipInfo.getAsJsonArray(Constants.CHILDOFRelation), Constants.CHILDOFRelation));
	        }
		}
	}
		
        return relatiionshipList;
   }
	
	private Map<String,JsonArray> getRelationshipList( JsonObject relationshipObject){
        Map<String,JsonArray> relationship_List = new HashMap<>();
            if(relationshipObject==null) return null;
            for (Iterator<?> iterator = relationshipObject.keySet().iterator(); iterator.hasNext();) {
                String relationship_name = (String) iterator.next();
                JsonArray relationshipArray = (JsonArray) relationshipObject.get(relationship_name);
                if (relationshipArray != null && relationshipArray.size() > 0) {
                    if (!relationship_List.containsKey(relationship_name)) relationship_List.put(relationship_name,relationshipArray);
                }
            }
            return relationship_List;
    }
	
	private Map<String, String> getChildRelatiionshipDetails(JsonObject relationshipInfo) throws IOException {
		Map<String, String> relatiionshipList=new HashMap<String, String>();
		if(relationshipInfo!=null) {		
	        if(relationshipInfo.getAsJsonArray(Constants.CHILDOFRelation)!=null && relationshipInfo.getAsJsonArray(Constants.CHILDOFRelation).size()>0) {
	            relatiionshipList.putAll(getSKURelationship(relationshipInfo.getAsJsonArray(Constants.CHILDOFRelation), Constants.CHILDOFRelation));
	        }
		}
        return relatiionshipList;
   }
	
	@SuppressWarnings("rawtypes")
	private Map<String, String> getImageRelationship(JsonArray relationshipInfoArray,String relationshipName) throws IOException {
		Map<String, String> relatiionshipList=new HashMap<String, String>();
		String name="";
		Integer i=1;
	       if (relationshipInfoArray != null && relationshipInfoArray.size() > 0) {
	           Iterator var3 = relationshipInfoArray.iterator();
	           while(var3.hasNext()) {
	               JsonObject relationshipInfo = (JsonObject)var3.next();
	               if(relationshipInfo!=null){	                 
	                   String id=JsonObjectHelper.getRelationshipId(relationshipInfo);
	                   JsonObject imageNameObj=null;
	                   if(relationshipName.equals(Constants.IMAGESRelation))
	                   {
	                    imageNameObj= RSRestConnector.getImageNameObject(id,"image");
	                   }
	                   else
	                   {
	                	 imageNameObj= RSRestConnector.getImageNameObject(id,"document");
	                   }
	                   if(imageNameObj!=null)
	                   {
	                	   name=JsonObjectHelper.getAttributeValueFromEntityObject(imageNameObj,"property_originalfilename");
	                   }
	                   String isPrimary=JsonObjectHelper.getAssetsAttributeValue(relationshipInfo,Constants.ISPRIMARY);
	                   String printImageType=JsonObjectHelper.getAssetsAttributeValue(relationshipInfo,Constants.PRINTIMAGETYPE);
	                   if(name!=null) {
	                	   String downloadurl= JsonObjectHelper.getRelationshipDownloadURL(relationshipInfo);
	                	   if(printImageType!=null && downloadurl!=null) {
		                		  if(!relatiionshipList.containsKey(printImageType+"_Url")) relatiionshipList.put(printImageType+"_Url", downloadurl);
		                		  if(!relatiionshipList.containsKey(printImageType+"_Name")) relatiionshipList.put(printImageType+"_Name", name);
		                	  }
		                   else
		                   {
		                	if(isPrimary!=null && isPrimary.toLowerCase()=="true") {
		                	  if(!relatiionshipList.containsKey(relationshipName+"IsPrimary")) relatiionshipList.put(relationshipName+"isPrimary", name);
		                	  if(Constants.IMAGEURL) {
			                	   
			                	   if(!relatiionshipList.containsKey(relationshipName+"IsPrimaryURL") && downloadurl!=null) relatiionshipList.put(relationshipName+"isPrimaryURL", downloadurl);
			                   }
		                	}
		                   else {
		                	   if(!relatiionshipList.containsKey(relationshipName+"IsSecondary"+i.toString()))relatiionshipList.put(relationshipName+"isSecondary"+i.toString(), name);
		                	   if(Constants.IMAGEURL) {
			                	   if(!relatiionshipList.containsKey(relationshipName+"IsSecondaryURL"+i.toString()) && downloadurl!=null) relatiionshipList.put(relationshipName+"isSecondaryURL"+i.toString(), downloadurl);
			                   }
		                	   i++;
		                   }
	                   }
	                   }
	                }
	           }
	       }   
	       return relatiionshipList;
	   }
	
	@SuppressWarnings("rawtypes")
	private Map<String, String> getSKURelationship(JsonArray relationshipInfoArray,String relationshipName){
		Map<String, String> relatiionshipList=new HashMap<String, String>();
        if (relationshipInfoArray != null && relationshipInfoArray.size() > 0) {
            Iterator var3 = relationshipInfoArray.iterator();
            while (var3.hasNext()) {
                JsonObject relationshipInfo = (JsonObject) var3.next();
                if (relationshipInfo != null) {
                    String relToId=JsonObjectHelper.getRelationshipId(relationshipInfo);
                    String type= JsonObjectHelper.getRelationshipType(relationshipInfo);
                    if(relToId!=null &&type!=null) {
                    if(!relatiionshipList.containsKey(relationshipName+Constants.id))relatiionshipList.put(relationshipName+Constants.id, relToId);
                    if(!relatiionshipList.containsKey(relationshipName+Constants.type))relatiionshipList.put(relationshipName+Constants.type, type);
                    }
                }
            }
        }
        return relatiionshipList;
    }

	private static void getQueries(String query){
		if(query.contains(";"))
		{
			String[] queryArray = query.split(";");
			if(queryArray.length>0)
			{
			for (int i = 0; i < queryArray.length; i++) {
				getQueryvalues(queryArray[i].trim());
			}
			}
		}
		else
		{
			getQueryvalues(query);
		}
	}
	
	/*
	 * GetQueryvalues-> Get queries values.
	 */
	private static void getQueryvalues(String queryArray){
		if(!queryArray.isEmpty() &&queryArray!=null)
		{
			if(queryArray.contains(":"))
			{
				String[] _query= queryArray.split(":");
				if(_query[0].toLowerCase().equals("type"))
				queryType=(_query[1].trim());
				if(_query[0].toLowerCase().equals("search"))
					querySearch=(_query[1].trim());
				if(_query[0].toLowerCase().equals("locale"))
					queryLocale=(_query[1].trim());
				if(_query[0].toLowerCase().equals("context"))
					queryContext=(_query[1].trim());
			}
		}
	}
	
}
