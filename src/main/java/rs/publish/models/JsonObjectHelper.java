package rs.publish.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import com.google.gson.*;

public class JsonObjectHelper {

	public static JsonObject getAttributesJsonObjectFromEntityObject(JsonObject entityJsonObject){
	
	    if(entityJsonObject ==null){
	        return null;
	    }
	    if(entityJsonObject.get(Constants.DATA)==null) {
	        return null;
	    }
	    if(entityJsonObject.getAsJsonObject(Constants.DATA).get(Constants.ATTRIBUTES)==null) {
	        return null;
	    }
	        return entityJsonObject.getAsJsonObject(Constants.DATA).getAsJsonObject(Constants.ATTRIBUTES);
	}
	
	public static JsonObject getRelationshipsJsonObjectFromEntityObject(JsonObject entityJsonObject){
	    if(entityJsonObject ==null){
	        return null;
	    }
	    if(entityJsonObject.get(Constants.DATA)==null)
	    {
	        return null;
	    }
	    if(entityJsonObject.getAsJsonObject(Constants.DATA).get(Constants.RELATIONSHIPS)==null){
	        return null;
	    }
	        return entityJsonObject.getAsJsonObject(Constants.DATA).getAsJsonObject(Constants.RELATIONSHIPS);
	}
	
	@SuppressWarnings("rawtypes")
	public static Map<String,List<JsonElement>> getAttributeValuesFromAttributes(JsonObject attributes) {
	     Map<String, List<JsonElement>> attribute_List = new HashMap<>();
	    if(attributes==null) return null;
	    List<JsonElement> valueObject;
	    for (Iterator<?> iterator = attributes.keySet().iterator(); iterator.hasNext();) {
	        valueObject=new ArrayList<>();
	        String attribues_name = (String) iterator.next();
	        JsonObject attribute = (JsonObject) attributes.get(attribues_name);
	        JsonArray valuesArray = attribute.getAsJsonArray(Constants.VALUES);
	        if (valuesArray != null && valuesArray.size() > 0) {
	            Iterator var3 = valuesArray.iterator();
	            while (var3.hasNext()) {
	                valueObject.add((JsonElement) var3.next());
	            }
	            attribute_List.put(attribues_name,valueObject);
	        }
	        else if(attribute.get(Constants.GROUP)!=null)
	        {
	            JsonElement groupArray = attribute.get(Constants.GROUP);
	            valueObject.add(groupArray);
	            attribute_List.put(attribues_name,valueObject);
	        }
	    }
	    return attribute_List;
	}

	 @SuppressWarnings("rawtypes")
	public static Map<String,String> getAttributeExternalName(JsonObject entityManangeObject,Map<String,String> attributeList) throws IOException {
	      if(entityManangeObject==null)
	          return  null;
	      JsonObject attributeInfo=getAttributesJsonObjectFromEntityObject(entityManangeObject);
	      if(attributeInfo==null)
	          return null;
	        List<String> attriList=new ArrayList<>();
	        for (Iterator<?> iterator = attributeInfo.keySet().iterator(); iterator.hasNext();) {
	            String attribues_name = (String) iterator.next();
	            JsonObject attribute = (JsonObject) attributeInfo.get(attribues_name);
	            JsonArray groupArray = attribute.getAsJsonArray(Constants.GROUP);
	            if (groupArray != null && groupArray.size() > 0) {
	               JsonObject nestedAttribute= groupArray.get(0).getAsJsonObject();
	                attriList=getNestedAtrribute(nestedAttribute,attriList);
	            }
	            if(attribute.get(Constants.PROPERTIES)!=null) {
	                JsonElement externaAtt = attribute.getAsJsonObject(Constants.PROPERTIES).get("externalName");
	            if (externaAtt != null) {
	                if (!attributeList.containsKey(attribues_name))
	                    attributeList.put(attribues_name, externaAtt.getAsString());
	            }
	            else if (!attriList.contains(attribues_name)) attriList.add(attribues_name);
	        }
	    }
	    if(!attriList.isEmpty()){
	      JsonArray attributeArray=  RSRestConnector.getAttributeManageObject(attriList);
	      if(attributeArray.size()>0){
	          String value;
	          Iterator var3 = attributeArray.iterator();
	          while (var3.hasNext()) {
	              JsonObject attributesObject= (JsonObject) var3.next();
	              String attr_Name=attributesObject.get("name").getAsString();
	              JsonObject attributeObject=getAttributesJsonObjectFromEntityObject(attributesObject);
	              if(attributeObject!=null) {
	              JsonArray values= attributeObject.getAsJsonObject("externalName").getAsJsonArray(Constants.VALUES);
	              if(values!=null) {
	                  value = values.get(0).getAsJsonObject().get("value").getAsString();
	                      if(value!=null) {
	                          if (!attributeList.containsKey(attr_Name)) attributeList.put(attr_Name, value);
	                      }
	                  }
	              }
	          }
	          }
	        }
	        return attributeList;
	    }
	
	 private static List<String> getNestedAtrribute(JsonObject nestedAttributes,List<String> attriList){
	        for (Iterator<?> iterator = nestedAttributes.keySet().iterator(); iterator.hasNext();) {
	            String attribues_name = (String) iterator.next();
	          if(!attriList.contains(attribues_name)) attriList.add(attribues_name);
	        }
	        return attriList;
	    }
	 
	 public static String getRelationshipId(JsonObject relationshipJsonObject){
	        if(relationshipJsonObject == null)
	            return null;
	        try{
	            return relationshipJsonObject.get(Constants.RELTO).getAsJsonObject().get(Constants.id).getAsString();
	        } catch (Exception e){
	        }
	        return null;
	    }
	 
	 public static String getAttributeValueByAttributeName(JsonObject attributes, String attributeName){
	        if(attributes == null){
	            return  null;
	        }
	
	        if(attributes.has(attributeName)){
	            return getSingleValueFromAttribute(attributes.getAsJsonObject(attributeName));
	        }
	
	        return null;
	    }
	 
	 public static String getSingleValueFromAttribute(JsonObject attribute) {
	
	        if(attribute == null) { return  null; }
	
	        JsonArray valuesArray = attribute.getAsJsonArray(Constants.VALUES);
	        if (valuesArray != null) {
	            return valuesArray.get(0).getAsJsonObject().getAsJsonPrimitive(Constants.VALUE).getAsString();
	        }
	
	        return null;
	  }
	 
	 public static String getRelationshipType(JsonObject relationshipJsonObject){
	        if(relationshipJsonObject == null)
	            return null;
	        try{
	            return relationshipJsonObject.get(Constants.RELTO).getAsJsonObject().get(Constants.TYPE).getAsString();
	        } catch (Exception e){
	        }
	        return null;
	    }
	
	 public static String getRelationshipDownloadURL(JsonObject relationshipJsonObject){
	    if(relationshipJsonObject == null)
	        return null;
	    try{
	        JsonObject rel_Attributes;
	        if(relationshipJsonObject!=null) {
	            rel_Attributes = getAttributesJsonObjectFromEntityObject(relationshipJsonObject.get(Constants.RELTO).getAsJsonObject());
	            return getAttributeValueByAttributeName(rel_Attributes,Constants.DOWNLOADURL);
	        }
	    } catch (Exception e){
	    }
	    return null;
	}
	
	 public static String getAssetsAttributeValue(JsonObject relationshipJsonObject, String attributeName){
	    if(relationshipJsonObject == null)
	        return null;
	    try{
	        JsonObject attributesObject = getAssetsAttributes(relationshipJsonObject);//relationshipJsonObject.getAsJsonObject(Constants.ATTRIBUTES).getAsJsonObject(Constants.ISPRIMARY);
	        return getAttributeValueByAttributeName(attributesObject,attributeName);
	    } catch (Exception e){
	    }
	    return null;
	}

	 public static String getAttributeValueFromEntityObject(JsonObject entityJsonObject, String attributeName){
	        JsonObject attributesInfo = getAttributesJsonObjectFromEntityObject(entityJsonObject);

	        if (attributesInfo == null || attributeName == null || attributeName.isEmpty()) {
	            return null;
	        }

	        JsonObject attributeObject = attributesInfo.getAsJsonObject(attributeName);

	        return getSingleValueFromAttribute(attributeObject);
	    }
	 
	 public static JsonObject getContextAttributesJsonObjectFromEntityObject(JsonObject entityJsonObject, String contextType, String contextName){
	        if(entityJsonObject ==null){
	            return null;
	        }
	        JsonObject contextJsonObject = getContextObjectFromEntity(entityJsonObject, contextType, contextName);
	        if (contextJsonObject != null) {;
	            if (contextJsonObject.has(Constants.ATTRIBUTES)) {
	                JsonObject attributesInfo = contextJsonObject.getAsJsonObject(Constants.ATTRIBUTES);
	                return attributesInfo;
	            }
	        }
	        return null;
	    }

	 public static JsonObject getContextRelationshipsJsonObjectFromEntityObject(JsonObject entityJsonObject, String contextType, String contextName){
	        if(entityJsonObject ==null){
	            return null;
	        }
	        JsonObject contextJsonObject = getContextObjectFromEntity(entityJsonObject, contextType, contextName);
	        if (contextJsonObject != null) {;
	            if (contextJsonObject.has(Constants.RELATIONSHIPS)) {
	                JsonObject attributesInfo = contextJsonObject.getAsJsonObject(Constants.RELATIONSHIPS);
	                return attributesInfo;
	            }
	        }
	        return null;
	    }

	    @SuppressWarnings("rawtypes")
	 public static JsonObject getContextObjectFromEntity(JsonObject entityJsonObject, String contextType, String contextName) {
	        if(entityJsonObject ==null){
	            return null;
	        }

	        JsonElement contextInfo = entityJsonObject.get(Constants.DATA).getAsJsonObject().get(Constants.CONTEXTS);

	        if (contextInfo != null && contextInfo.isJsonArray()) {
	            Iterator contextInfoIterator = contextInfo.getAsJsonArray().iterator();

	            while(contextInfoIterator.hasNext()) {

	                JsonElement contextInfoItem = (JsonElement)contextInfoIterator.next();
	                JsonObject contextInfoObject = contextInfoItem.getAsJsonObject();
	                JsonObject context = contextInfoObject.getAsJsonObject(Constants.CONTEXT);

	                JsonElement contextNameJsonElement =  context.get(contextType);

	                if(contextNameJsonElement == null){
	                    continue;
	                }

	                if(!contextNameJsonElement.getAsString().equals(contextName)){
	                    continue;
	                }

	                return contextInfoObject;
	            }
	        }

	        return null;
	    }
	    
	 public static JsonObject getAssetsAttributes(JsonObject relationshipJsonObject){
	        if(relationshipJsonObject == null)
	            return null;
	        if( relationshipJsonObject.getAsJsonObject(Constants.ATTRIBUTES)==null)
	            return null;
	        return relationshipJsonObject.getAsJsonObject(Constants.ATTRIBUTES);
	    }

}
