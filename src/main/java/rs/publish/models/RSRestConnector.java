package rs.publish.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.springframework.stereotype.Service;
import com.google.gson.*;
import okhttp3.*;

@Service
public class RSRestConnector {
	
	/*
	 * GetUserDetails-> Get user Json data.
	 */
	public static JsonObject getUserDetails(String userName, String password) {
		try {
			JsonObject requestBody = getUserRequestBody(userName);
			Response response = sendRequest(requestBody,Constants.WEBURL,Constants.WEBPORT, "/api/entitymodelservice/get");
			if (response != null && response.code() == 200) {
				JsonArray valuesArray = GetEntitiesArray("entityModels",response);
                    if (valuesArray != null) {
                        return valuesArray.get(0).getAsJsonObject();
                    }
            }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/*
	 * GetEntities-> Get List of Entities Array data in Json format.
	 */
	public static List<JsonArray> getEntities(String query,String querySearch) {
		List<JsonArray> entities_response = new ArrayList<JsonArray>();
		try {
			List<String>attributesList=null;
			if(Constants.ATTRIBUTEFROMREFERENCE) {
					attributesList=getAttributeList();
			}
			if (Constants.TOTALRECORD > Constants.MAXRECORD &&! querySearch.isEmpty()) {
				long r_Count = (Constants.TOTALRECORD % Constants.MAXRECORD == 0) ? Constants.TOTALRECORD / Constants.MAXRECORD : ((Constants.TOTALRECORD / Constants.MAXRECORD + 1));
				for (int i = 0; i <= r_Count; i++) {
					JsonObject requestBody=getRSEntityRequestBody(query, querySearch,i,attributesList);
					Response response = sendRequest(requestBody, Constants.WEBURL,Constants.WEBPORT,"/api/entityappservice/get");
					if(response!=null && response.code()==200){
	                    JsonArray valuesArray = GetEntitiesArray("entities",response);
	                    if (valuesArray != null) {
							entities_response.add(valuesArray);
						}
	                    else {
							break;
						}
					}					
				}
			}
			else {
				JsonObject requestBody=getRSEntityRequestBody(query,querySearch,0,attributesList);
				Response response = sendRequest(requestBody,Constants.WEBURL,Constants.WEBPORT, "/api/entityappservice/get");
				if(response!=null && response.code()==200){
                    JsonArray valuesArray = GetEntitiesArray("entities",response);
                    if (valuesArray != null) {
						entities_response.add(valuesArray);
					} 
				}
		} 
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return entities_response;
	}
	
	@SuppressWarnings({ "unused"})
	
	public static JsonObject getEntityManageObject(String type) throws IOException {
        JsonObject requestBody=getEntityManageRSjsonRequest(type);
        if(requestBody!=null) {
            Response response = sendRequest(requestBody,Constants.WEBURL,Constants.WEBPORT, "/api/entitymodelservice/get");
            if (response != null && response.code() == 200) {
            	JsonArray valuesArray = GetEntitiesArray("entityModels",response);
                if (valuesArray != null) {
                    return valuesArray.get(0).getAsJsonObject();
                }
            }
       }
        return null;
    }

    public static JsonArray getAttributeManageObject(List<String> attribute) throws IOException {
        JsonObject requestBody=getAttributeRSjsonRequest(attribute);
        if(requestBody!=null) {
            Response response = sendRequest(requestBody,Constants.WEBURL,Constants.WEBPORT, "/api/entitymodelservice/get");
            if (response != null && response.code() == 200) {
            	return  GetEntitiesArray("entityModels",response);
            }
        }
        return null;
    }
  
    public static JsonObject getImageNameObject( String entityid, String type) throws IOException {
        JsonObject requestBody= getImageNameRSjsonRequest(entityid,type);
        if(requestBody!=null) {
            Response response = sendRequest(requestBody,Constants.WEBURL,Constants.WEBPORT, "/api/entityappservice/get");
            if (response != null && response.code() == 200) {
                JsonParser parser = new JsonParser();
                String jsonData_response = response.body().string();
                if (jsonData_response != null) {
                    JsonObject jsonImgObject = (JsonObject) parser.parse(jsonData_response);
                    JsonArray valuesArray = jsonImgObject.getAsJsonObject("response").getAsJsonArray("entities");
                    if (valuesArray != null) {
                        return valuesArray.get(0).getAsJsonObject();
                    }
                }
            }
        }
        return null;
    }

    public static JsonObject getImageObject(String id, String type) throws IOException {
        JsonObject requestBody=getImageRSjsonRequest(id,type);
        if(requestBody!=null) {
            Response response = sendRequest(requestBody,Constants.WEBURL,Constants.ASSETSPORT, "/api/rsAssetService/getlinkedasseturl");
            if (response != null && response.code() == 200) {
                JsonParser parser = new JsonParser();
                String jsonData_response = response.body().string();
                if (jsonData_response != null) {
                    JsonObject jsonImgObject = (JsonObject) parser.parse(jsonData_response);
                    if(jsonImgObject.getAsJsonObject("response")!=null) {
                    JsonArray valuesArray = jsonImgObject.getAsJsonObject("response").getAsJsonArray("entities");
                    if (valuesArray != null) {
                        return valuesArray.get(0).getAsJsonObject();
                    }
                    }
                }
            }
        }
        return null;
    }

	private static JsonArray GetEntitiesArray(String Arraytype, Response response) {
		JsonArray entities_Array = null;
		try {
			JsonParser parser = new JsonParser();
			String jsonData_response = response.body().string();
			if(jsonData_response!=null) {
				JsonObject jsonImgObject = (JsonObject) parser.parse(jsonData_response);
				entities_Array = jsonImgObject.getAsJsonObject("response").getAsJsonArray(Arraytype);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return entities_Array;
	}
	
	private static Response sendRequest(JsonObject requestBody, String WEBURL, String webPort, String api) throws IOException{
        String URL="";
		if (webPort!="") {
			URL=WEBURL+":"+webPort;
        }else {
        	URL=WEBURL;
        }
		
		OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
                .url(URL+api)
                .method("POST", body)
                .addHeader("x-rdp-version", "8.1")
                .addHeader("x-rdp-clientId", "rdpclient")
                .addHeader("x-rdp-tenantId", Constants.TENANTID)
                .addHeader("x-rdp-userId", Constants.USERID)
                .addHeader("x-rdp-userRoles", "[\"admin\"]")
                .addHeader("auth-client-id", Constants.AUTH_CLIENT_ID)
                .addHeader("auth-client-secret", Constants.AUTH_CLIENT_SECRECT)
                .addHeader("Content-Type", "application/json")
                .build();
        return client.newCall(request).execute();
    }

	private static JsonArray GetentityjsonArray( List<String> attributeList){

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String currentLinksValues = gson.toJson(attributeList);
        JsonParser parser = new JsonParser();
        return parser.parse(currentLinksValues).getAsJsonArray();
    }

	private static JsonObject getRSEntityRequestBody(String querytype,String querySearch,Integer i,List<String>attributesList) {
		JsonObject jsonObject=new JsonObject();
		String[] typeList;
	    JsonObject params=new JsonObject();
	    JsonArray types=new JsonArray();
	    if(querytype.isEmpty()) {
	    	typeList=Constants.TYPES.split(",");
	    	types = GetentityjsonArray(Arrays.asList(typeList));
	     }
	    else {
	    	if(querytype.contains(",")) {
	    		typeList=querytype.split(",");
	    		types = GetentityjsonArray(Arrays.asList(typeList));
	    	}
	    	else {
	    		typeList=new String[1];
	    		typeList[0]=querytype;
	    		types = GetentityjsonArray(Arrays.asList(typeList));
	    	}
	    }
	    JsonObject query=new JsonObject();
	    JsonObject jsonLocale=new JsonObject();
	    jsonLocale.addProperty("source","internal");
	    jsonLocale.addProperty("locale",Constants.LOCALES);
	    JsonArray valueContexts=new JsonArray();
	    valueContexts.add(jsonLocale);
	    query.add("valueContexts",valueContexts);
	    JsonObject filters=new JsonObject();
	    filters.add("typesCriterion",types);
	    JsonObject options=new JsonObject();
	    if(querySearch.isEmpty()) {
	    	options.addProperty("maxRecords", 1);
		    options.addProperty("from", 0);
	    }
	    else {
	    	JsonObject keywordsCriterion=new JsonObject();
		    keywordsCriterion.addProperty("keywords", querySearch);
		    keywordsCriterion.addProperty("operator", "_AND");
	    	options.addProperty("maxRecords", Constants.MAXRECORD);
		    options.addProperty("from", i*Constants.MAXRECORD);
		    filters.add("keywordsCriterion", keywordsCriterion);
	    }
	    query.add("filters",filters);
	    JsonObject fields=new JsonObject();
	    JsonArray relationships=new JsonArray();
	    relationships.add("_ALL");
	    fields.add("relationships",relationships);
	    if(attributesList!=null && attributesList.size()>0){
            JsonArray attributeArrayList = GetentityjsonArray(attributesList);
            fields.add("attributes",attributeArrayList);
        }
	    else {
        JsonArray attributes=new JsonArray();
	    attributes.add("_ALL");
	    fields.add("attributes",attributes); 
	    } 
	    params.add("query",query);    
	    query.add("valueContexts",valueContexts);
	    if(Constants.READFROMCONTEXT)
        {
            JsonArray contexts=new JsonArray();
            JsonObject context=new JsonObject();
            context.addProperty(Constants.CONTEXTTYPE,Constants.CONTEXTNAME);
            contexts.add(context);
            query.add("contexts",contexts);
        }
	    params.add("fields",fields);
	    JsonObject sortTypeobj=new JsonObject();
	    sortTypeobj.addProperty("createdDate","_ASC");
	    sortTypeobj.addProperty("sortType","_STRING");
	    JsonArray properties=new JsonArray();
	    properties.add(sortTypeobj);
	    JsonObject sort=new JsonObject();
	    params.add("sort", sort);
	    params.add("options", options);
	    jsonObject.add("params",params);
	    return jsonObject;
	}
		
	private static JsonObject getUserRequestBody(String username) {
		JsonObject jsonObject=new JsonObject();
	    JsonObject params=new JsonObject();
	    JsonObject query=new JsonObject();
	    JsonArray typesCriterion=new JsonArray();
        typesCriterion.add("user");
	    JsonObject filters=new JsonObject();
	    filters.add("typesCriterion",typesCriterion);
	    query.add("filters",filters);
	    query.addProperty("name", username);
	    params.add("query", query);
	    jsonObject.add("params",params);
	    return jsonObject;
	}
	
	private static JsonObject getAttributeRSjsonRequest(List<String> attribute) {
	    JsonObject jsonObject=new JsonObject();
	    JsonObject params=new JsonObject();
	    JsonArray typesCriterion=new JsonArray();
	    JsonArray attributeArrayList = GetentityjsonArray(attribute);
	    typesCriterion.add("attributeModel");
	    JsonObject query=new JsonObject();
	    if(Constants.READFROMCONTEXT)
        {
            JsonArray contexts=new JsonArray();
            JsonObject context=new JsonObject();
            context.addProperty(Constants.CONTEXTTYPE,Constants.CONTEXTNAME);
            contexts.add(context);
            query.add("contexts",contexts);
        }
	    query.add("names",attributeArrayList);
	    JsonObject filters=new JsonObject();
	    filters.add("typesCriterion",typesCriterion);
	    query.add("filters",filters);
	    JsonObject jsonLocale=new JsonObject();
	    jsonLocale.addProperty("source","internal");
	    jsonLocale.addProperty("locale",Constants.LOCALES);
	    JsonArray valueContexts=new JsonArray();
	    valueContexts.add(jsonLocale);
	    query.add("valueContexts",valueContexts);
	    JsonObject fields=new JsonObject();
	    JsonArray attributes=new JsonArray();
	    attributes.add("externalName");
	    fields.add("attributes",attributes);
	    params.add("query",query);
	    params.add("fields",fields);
	    jsonObject.add("params",params);
	    return jsonObject;
	}

	private static JsonObject getEntityManageRSjsonRequest(String type) {
	    JsonObject jsonObject=new JsonObject();
	    JsonObject params=new JsonObject();
	    JsonArray typesCriterion=new JsonArray();
	    typesCriterion.add("entityManageModel");
	    JsonObject query=new JsonObject();
	    if(Constants.READFROMCONTEXT)
        {
            JsonArray contexts=new JsonArray();
            JsonObject context=new JsonObject();
            context.addProperty(Constants.CONTEXTTYPE,Constants.CONTEXTNAME);
            contexts.add(context);
            query.add("contexts",contexts);
        }
	    JsonObject filters=new JsonObject();
	    filters.add("typesCriterion",typesCriterion);
	    query.add("filters",filters);
	   // JsonObject jsonLocale=new JsonObject();
	   //.addProperty("source","internal");
	   // jsonLocale.addProperty("locale",Constants.locale);
	    //JsonArray valueContexts=new JsonArray();
	    //valueContexts.add(jsonLocale);
	   // query.add("valueContexts",valueContexts);
	    query.addProperty("id",type+"_entityManageModel");
	    JsonObject fields=new JsonObject();
	    JsonArray attributes=new JsonArray();
	    attributes.add("_ALL");
	    fields.add("attributes",attributes);
	    JsonArray relationships=new JsonArray();
	    relationships.add("_ALL");
	    fields.add("relationships",relationships);
	    params.add("query",query);
	    params.add("fields",fields);
	    jsonObject.add("params",params);
	    return jsonObject;
	}

	private static JsonObject getImageNameRSjsonRequest(String id, String type) {
        JsonObject jsonObject=new JsonObject();
        JsonObject params=new JsonObject();
        JsonArray typesCriterion=new JsonArray();
        typesCriterion.add(type);
        JsonObject query=new JsonObject();
        query.addProperty("id",id);
        JsonObject filters=new JsonObject();
        filters.add("typesCriterion",typesCriterion);
        query.add("filters",filters);
        JsonObject fields=new JsonObject();
        JsonArray attributes=new JsonArray();
        attributes.add("property_originalfilename");
        fields.add("attributes",attributes);
        params.add("query",query);
        params.add("fields",fields);
        jsonObject.add("params",params);
        return jsonObject;
    }

	private static JsonObject getImageRSjsonRequest(String id,String type) {
        JsonObject jsonObject=new JsonObject();
        JsonObject params=new JsonObject();
        JsonArray typesCriterion=new JsonArray();
        typesCriterion.add(type);
        JsonObject query=new JsonObject();
        query.addProperty("id",id);
        JsonObject filters=new JsonObject();
        filters.add("typesCriterion",typesCriterion);
        query.add("filters",filters);
        if(Constants.READFROMCONTEXT)
        {
            JsonArray contexts=new JsonArray();
            JsonObject context=new JsonObject();
            context.addProperty(Constants.CONTEXTTYPE,Constants.CONTEXTNAME);
            contexts.add(context);
            query.add("contexts",contexts);
        }
        JsonObject fields=new JsonObject();
        JsonArray relationships=new JsonArray();
        relationships.add("_ALL");
        fields.add("relationships",relationships);
        params.add("query",query);
        params.add("fields",fields);
        jsonObject.add("params",params);
        return jsonObject;
    }
	
	@SuppressWarnings({ "rawtypes", "null" })
	private static List<String> getAttributeList(){
		List<String> attributes=new ArrayList();
		try {
			JsonArray attributeentityJsonArray = getLookUPObject(Constants.ATTRIBUTEREFERENCETYPE);
			if(attributeentityJsonArray!=null && attributeentityJsonArray.size()>0){
				Iterator var3 = attributeentityJsonArray.iterator();
	            while (var3.hasNext()) {
	                JsonObject attributeentityJsonObject=  (JsonObject) var3.next();
	                if(attributeentityJsonObject!=null) {
	                	String attributesJsonObject=JsonObjectHelper.getAttributeValueFromEntityObject(attributeentityJsonObject,Constants.REFERENCEATTRIBUTE);
	                	if(attributesJsonObject!=null) {
	                		attributes.add(attributesJsonObject);
	                	}
	                }
	            }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return attributes;
	}
	
	  private static JsonArray getLookUPObject(String lookupType) throws IOException {
	        if(lookupType!=null) {
	            JsonObject requestBody = getLookupRSjsonRequest(lookupType);
	            if (requestBody != null) {
	                Response response = sendRequest(requestBody,Constants.WEBURL,Constants.WEBPORT, "/api/entityappservice/get");
	                if (response != null && response.code() == 200) {
	                    JsonParser parser = new JsonParser();
	                    String jsonData_response = response.body().string();
	                    if (jsonData_response != null) {
	                        JsonObject jsonImgObject = (JsonObject) parser.parse(jsonData_response);
	                        return jsonImgObject.getAsJsonObject("response").getAsJsonArray("entities");
	                    }
	                }
	            }
	        }
	        return null;
	    }
	  
	  private static JsonObject getLookupRSjsonRequest(  String lookupName) {
	        JsonObject jsonObject=new JsonObject();
	        JsonObject params=new JsonObject();
	        JsonArray typesCriterion=new JsonArray();
	        typesCriterion.add(lookupName);
	        JsonObject query=new JsonObject();
	       // query.addProperty("id",lookupRowId);
	        JsonObject filters=new JsonObject();
	        filters.add("typesCriterion",typesCriterion);
	        query.add("filters",filters);
	        JsonObject fields=new JsonObject();
	        JsonArray attributes=new JsonArray();
	        attributes.add("_ALL");
	        fields.add("attributes",attributes);
	        params.add("query",query);
	        params.add("fields",fields);
	        jsonObject.add("params",params);
	        return jsonObject;
	    }
}
