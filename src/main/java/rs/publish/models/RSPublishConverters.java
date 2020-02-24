package rs.publish.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import okhttp3.Response;
import rs.publish.controller.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;

@Service
public class RSPublishConverters {
	@Autowired
	private PIMConnectors pimConnector;
	@Autowired
	private Configuration configuration;
	private final Logger log = LoggerFactory.getLogger(Services.class);
	private JSONObject headerDetails;

	@PostConstruct
	public void init() {
		headerDetails = configuration.ReadFileAsJson(configuration.GetRSConfigValues("RSHeader.json"));
	}
	/*
	 * LoginDetails-> Get User details query by userName and password as parameters
	 */
	public LoginResponse LoginDetails(String userName, String password) {
		LoginResponse loginResponse = new LoginResponse();
		try {
			String name = null;
			Response response = pimConnector.GetUserDetails(userName, password);
			JSONArray user_EntityModels = pimConnector.GetEntitiesArray("entityModels", response);
			if (user_EntityModels != null && user_EntityModels.size() > 0) {
				for (int i = 0; i < user_EntityModels.size(); i++) {
					JSONObject binary_object = (JSONObject) user_EntityModels.get(i);
					name = (String) binary_object.get("name");
				}
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
			log.info("-------RSPublishConverter LoginDetails Exception ---- " + ex.getMessage());
		}
		return loginResponse;
	}

	/*
	 * EntitiesDetails-> Get All Entities details including attributes and
	 * relationships query by query.
	 */
	public FinalResult EntitiesDetails(String query) {
		FinalResult finalResult = new FinalResult();
		try {
			List<Dictionary<String, String>> totalAttribute_List = new ArrayList<Dictionary<String, String>>();
			List<Dictionary<String, String>> attributeList = null;
			JSONArray entitiesArray = null;
			ArrayList<JSONArray> response = pimConnector.GetEntities(query);
			if (response != null && !response.isEmpty() && response.size() > 0) {
				int recordcount = 0;
				finalResult.setMaxResultHits(100000);
				finalResult.setFirstResult(0);
				for (int r = 0; r < response.size(); r++) {
					entitiesArray = (JSONArray) response.get(r);
					if (entitiesArray != null && !entitiesArray.isEmpty()) {
						Hashtable<String, String> attributes = null;
						recordcount = recordcount + entitiesArray.size();
						for (int i = 0; i < entitiesArray.size(); i++) {
							attributeList = new ArrayList<Dictionary<String, String>>();
							JSONObject entity_object = (JSONObject) entitiesArray.get(i);
							JSONObject data_Object = (JSONObject) entity_object.get("data");
							if (data_Object != null && data_Object.size() > 0) {
								attributes = new Hashtable<String, String>();
								attributes.put(FieldNames.type, (String) entity_object.get("type"));
								if(entity_object.get("name")!=null)attributes.put(FieldNames.name, (String) entity_object.get("name"));
								attributes.put(FieldNames.id, (String) entity_object.get("id"));
								JSONObject attribute_List = (JSONObject) data_Object.get("attributes");
								if (attribute_List != null && !attribute_List.isEmpty()) {
									Hashtable<String, String> attribute_Values = GetAttributeValues(attribute_List);
									if (attribute_Values != null && attribute_Values.size() > 0)
									{
										attributes.putAll(attribute_Values);
									}
								}
								JSONObject relationships = (JSONObject) data_Object.get("relationships");
								if (relationships != null && !relationships.isEmpty()) {
									Hashtable<String, String> relationship_Values=fillRelationmships(relationships);
									if(relationship_Values!=null && relationship_Values.size()>0) {
										attributes.putAll(relationship_Values);
									}
								}
							}
							attributeList.add(attributes);
							totalAttribute_List.addAll(attributeList);
						}
					}
				}
				finalResult.setTotalHits(recordcount);
				finalResult.setHits(totalAttribute_List);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("------- RSPublishConverter EntitiesDetails ------ Exception ----- " + ex.toString());
		}
		return finalResult;
	}
	
	private Hashtable<String,String> fillRelationmships(JSONObject relationships){
		Hashtable<String,String> relationshipsList=new Hashtable<String,String>();
		if(relationships!=null&& !relationships.isEmpty()) {
		JSONArray childof = (JSONArray) relationships.get("childof");
		JSONArray kittingcontains = (JSONArray) relationships.get("kittingcontains");
		JSONArray hasImages = (JSONArray) relationships.get("hasimages");
		JSONArray hasDocuments = (JSONArray) relationships.get("hasdocuments");
		if (childof != null && childof.size() > 0) {
			Hashtable<String, String> childof_List = GetChildof(childof);
			if (childof_List != null && childof_List.size() > 0)
				relationshipsList.putAll(childof_List);
		}
		if (kittingcontains != null && kittingcontains.size() > 0) {
			Hashtable<String, String> kittingcontains_List = GetKitting(kittingcontains);
			if (kittingcontains_List != null && kittingcontains_List.size() > 0)
				relationshipsList.putAll(kittingcontains_List);
		}
		if (hasImages != null && hasImages.size() > 0) {
			Hashtable<String, String> image_List = GetAssetsDetails(hasImages);
			if (image_List != null && image_List.size() > 0)
				relationshipsList.putAll(image_List);
		}
		if (hasDocuments != null && hasDocuments.size() > 0) {
			Hashtable<String, String> document_List = GetAssetsDetails(hasDocuments);
			if (document_List != null && document_List.size() > 0)
				relationshipsList.putAll(document_List);
		}
		}
		return relationshipsList;
	}
	/*
	 * GetChildof-> Get All entities child/SKU relationship details.
	 */
	private Hashtable<String, String> GetChildof(JSONArray childof) {
		Hashtable<String, String> childof_List = new Hashtable<String, String>();
		if (childof != null && !childof.isEmpty()) {
			for (int ch = 0; ch < childof.size(); ch++) {
				JSONObject value_list = (JSONObject) childof.get(ch);
				JSONObject relTo = (JSONObject) value_list.get("relTo");
				if (relTo != null && !relTo.isEmpty()) {
					if (!childof_List.containsKey(FieldNames.childof_parentId)) {
						childof_List.put(FieldNames.childof_parentId, (String) relTo.get("id"));
					}
					if (!childof_List.containsKey(FieldNames.childof_parentType)) {
						childof_List.put(FieldNames.childof_parentType, (String) relTo.get("type"));
					}
				}
			}
		}
		return childof_List;
	}
	/*
	 * GetKitting-> Get All entities child/kitting relationship details.
	 */
	private Hashtable<String, String> GetKitting(JSONArray kittingcontains) {
		Hashtable<String, String> kitting_List = new Hashtable<String, String>();
		if (kittingcontains != null && !kittingcontains.isEmpty()) {
			for (int ch = 0; ch < kittingcontains.size(); ch++) {
				JSONObject value_list = (JSONObject) kittingcontains.get(ch);
				JSONObject relTo = (JSONObject) value_list.get("relTo");
				if (relTo != null && !relTo.isEmpty()) {
					if (!kitting_List.containsKey(FieldNames.kittingcontains_parentId)) {
						kitting_List.put(FieldNames.kittingcontains_parentId, (String) relTo.get("id"));
					}
					if (!kitting_List.containsKey(FieldNames.kittingcontains_parentType)) {
						kitting_List.put(FieldNames.kittingcontains_parentType, (String) relTo.get("type"));
					}
				}
			}
		}
		return kitting_List;
	}
	/*
	 * GetAssetsDetails-> Get All assets type details.
	 */
	private Hashtable<String, String> GetAssetsDetails(JSONArray AssetsList) {
		Hashtable<String, String> result_Assets_List = new Hashtable<String, String>();
		if (AssetsList != null && !AssetsList.isEmpty()) {
			int imageSequence = 1;
			String primary_Check = "";
			for (int asst = 0; asst < AssetsList.size(); asst++) {
				primary_Check="";
				JSONObject value_list = (JSONObject) AssetsList.get(asst);
				JSONObject relTo = (JSONObject) value_list.get("relTo");
				JSONObject assetsPrimary_check = (JSONObject) value_list.get("attributes");
				if (assetsPrimary_check!=null &&!assetsPrimary_check.isEmpty()) {
					JSONArray values = (JSONArray) ((JSONObject) assetsPrimary_check.get("isprimary")).get("values");
					if (values != null && !values.isEmpty() && values.size() > 0) {
						for (int i = 0; i < values.size(); i++) {
							JSONObject isPrimary = (JSONObject) values.get(i);
							primary_Check = String.valueOf(isPrimary.get("value"));
						}
					}
				}
				if (relTo != null && !relTo.isEmpty()) {
					String id = (String) relTo.get("id");
					String type = (String) relTo.get("type");
					Hashtable<String, String> assets_FileName_list = GetAssetsFileName(id, type);
					if (assets_FileName_list != null && !assets_FileName_list.isEmpty()
							&& assets_FileName_list.size() > 0) {
						String assets_URL = GetAssetsURL(assets_FileName_list.get("property_objectkey"),
								assets_FileName_list.get("property_originalfilename"));
						if (assets_URL != null && !assets_URL.isEmpty()) {

							if (primary_Check!="") {
								if (!result_Assets_List.containsKey("main_primary" + type + "URL"))
									result_Assets_List.put("main_primary" + type + "URL", assets_URL);
								if (!result_Assets_List.containsKey("main_primary" + type + "filename"))
									result_Assets_List.put("main_primary" + type + "filename",
											assets_FileName_list.get("property_originalfilename"));
							} else {
								if (!result_Assets_List.containsKey(type + imageSequence + "URL"))
									result_Assets_List.put(type + imageSequence + "URL", assets_URL);
								if (!result_Assets_List.containsKey(type + imageSequence + "filename"))
									result_Assets_List.put(type + imageSequence + "filename",
											assets_FileName_list.get("property_originalfilename"));
								imageSequence++;
							}

						} else {
							if (primary_Check.toLowerCase()=="true") {
								if (!result_Assets_List.containsKey("main_primary" + type + "filename"))
									result_Assets_List.put("main_primary" + type + "filename",
											assets_FileName_list.get("property_originalfilename"));
							} else {
								if (!result_Assets_List.containsKey(type + imageSequence + "filename"))
									result_Assets_List.put(type + imageSequence + "filename",
											assets_FileName_list.get("property_originalfilename"));
								imageSequence++;
							}
						}
					}
				}
			}
		}
		return result_Assets_List;
	}
	/*
	 * GetAssetsURL-> Get asset URL details.
	 */
	private String GetAssetsURL(String assetsKey, String originalFileName) {
		String assets_URL = null;
		try {
			JSONObject assetsdynamicurl_check = (JSONObject) headerDetails.get("assetsdynamicurl");
			String dynamicUrlcheck = (String) assetsdynamicurl_check.get("value");
			if (dynamicUrlcheck.toLowerCase().equals("yes")) {
				Response response = pimConnector.GetAssetsURL(assetsKey, originalFileName);
				JSONArray binaryStreamObjects = pimConnector.GetEntitiesArray("binaryStreamObjects", response);
				if (binaryStreamObjects != null && binaryStreamObjects.size() > 0) {
					for (int i = 0; i < binaryStreamObjects.size(); i++) {
						JSONObject binary_object = (JSONObject) binaryStreamObjects.get(i);
						JSONObject properties = (JSONObject) ((JSONObject) binary_object.get("data")).get("properties");
						assets_URL = (String) properties.get("downloadURL");
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return assets_URL;
	}
	/*
	 * GetAssetsFileName-> Get asset file name and id.
	 */
	private Hashtable<String, String> GetAssetsFileName(String assets_id, String assets_type) {
		Hashtable<String, String> fileName_List = new Hashtable<String, String>();
		Response response = pimConnector.GetAssetsFileName(assets_id, assets_type);
		JSONArray entities_Array = pimConnector.GetEntitiesArray("entities", response);
		if (entities_Array != null && !entities_Array.isEmpty()) {
			for (int i = 0; i < entities_Array.size(); i++) {
				JSONObject entity_object = (JSONObject) entities_Array.get(i);
				JSONObject assets_Attributes = (JSONObject) ((JSONObject) entity_object.get("data")).get("attributes");
				if (assets_Attributes != null && assets_Attributes.size() > 0) {
					fileName_List = GetAttributeValues(assets_Attributes);
				}
			}
		}
		return fileName_List;
	}
	/*
	 * GetAttributeValues-> Get attribute values.
	 */
	private Hashtable<String, String> GetAttributeValues(JSONObject attribute_List) 
	{
		Hashtable<String, String> attributesValues = new Hashtable<String, String>();
		try
		{
		if (attribute_List != null && !attribute_List.isEmpty())
		{
			for (Iterator<?> iterator = attribute_List.keySet().iterator(); iterator.hasNext();) 
			{
				String attribues_name = (String) iterator.next();
				JSONObject attribues_Values = (JSONObject) attribute_List.get(attribues_name);
				if(attribues_Values!=null)
				{
					JSONArray attribues_Values_Array = (JSONArray) attribues_Values.get("values");
					if (attribues_Values_Array != null && attribues_Values_Array.size() > 0) 
					{
						attributesValues=FillAttributeValues(attributesValues,attribues_name,attribues_Values_Array);
					}
					JSONArray attribues_Group_Array = (JSONArray) attribues_Values.get("group");
					if(attribues_Group_Array!=null && attribues_Group_Array.size()>0) {
						String complexValues=getTableValue(attribues_Group_Array);
						if (complexValues.length() > 0)
						{
							attributesValues.put(attribues_name, complexValues);
						}
					}
				}
			}
		}
	} catch (Exception ex) {
		ex.printStackTrace();
		log.info("------- RSPublishConverter GetAttributeValues ------ Exception ----- " + ex.toString());
	}
		return attributesValues;
	}	
	/*	
	 * GetAttributeValues-> Get attribute values list .
	 */	
	private Hashtable<String, String> FillAttributeValues( Hashtable<String, String> List, String attribues_name, JSONArray attribues_Values_Array1)
	{
		try
		{
			if (attribues_Values_Array1 != null && attribues_Values_Array1.size() > 0) 
			{
				String value = "";
			for (int att = 0; att < attribues_Values_Array1.size(); att++) {
				JSONObject value_list = (JSONObject) attribues_Values_Array1.get(att);
				if(value_list!=null)
				{
				if (value == "") {
					 if( value_list.get("value") != null)
						 value = value_list.get("value").toString();
					}
				else {
					if(value_list.get("value") !=null)
						value = value + "\r" + value_list.get("value").toString();
					}
				}
			}
			if (!List.containsKey(attribues_name)) {
				List.put(attribues_name, value);
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log.info("------- RSPublishConverter FillAttributeValues ------ Exception ----- " + ex.toString());
		}
			return List;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getTableValue(JSONArray attribues_Group_Array) {
		List<Map<String,String>> groupList= new ArrayList<Map<String,String>>();
		Map<String,String> colvalue;
		List<String> heading=new ArrayList();
		for (int i=0;i<attribues_Group_Array.size();i++)
		{
			//j++;
		 JSONObject group_object = (JSONObject) attribues_Group_Array.get(i);
		 if(group_object!=null)
		 {
			colvalue=new HashMap<>();
			for (Iterator iteratorg = group_object.keySet().iterator();iteratorg.hasNext();) 
			{
				String child_attribues_name = (String) iteratorg.next();
				if(!child_attribues_name.equals("locale") && !child_attribues_name.equals("id") && !child_attribues_name.equals("source") && !child_attribues_name.equals("value") && !child_attribues_name.equals("properties"))
				{
					if(!heading.contains(child_attribues_name))heading.add(child_attribues_name);
					JSONObject group_Values = (JSONObject) group_object.get(child_attribues_name);
					JSONArray group_Values_Array = (JSONArray) group_Values.get("values");
					if (group_Values_Array != null && group_Values_Array.size() > 0) 
					{
						colvalue=FillValues(colvalue,child_attribues_name,group_Values_Array);
					}
				}
			}
			Map<String,String> map = new TreeMap<String,String>(colvalue);
			groupList.add(map);
		 }
	}
	List<String> headingSortedList= heading.stream().sorted().collect(Collectors.toList());
	
	return getTableFormat(headingSortedList,groupList);
	}
	
	private Map<String, String> FillValues(Map<String, String> List, String attName, JSONArray attribues_Values_Array1)
	{
		try
		{
			if (attribues_Values_Array1 != null && attribues_Values_Array1.size() > 0) 
			{
				String value = "";
			for (int att = 0; att < attribues_Values_Array1.size(); att++) {
				JSONObject value_list = (JSONObject) attribues_Values_Array1.get(att);
				if(value_list!=null)
				{
				if (value == "") {
					 if( value_list.get("value") != null)
						 value = value_list.get("value").toString();
					}
				else {
					if(value_list.get("value") !=null)
						value = value + "\r" + value_list.get("value").toString();
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
			log.info("------- RSPublishConverter FillValues ------ Exception ----- " + ex.toString());
		}
			return List;
	}
	 
	private String getTableFormat(List<String> headingSortedList,List<Map<String,String>> groupList) {
		String tablevalue = "";
		String tdkey = "";
		String tdvalue = "";
		String trvalue="";
        try
        {
        	for (String key : headingSortedList)
            {
                tdkey += "<th>" + key + "</th>";
            }
        	for(int j=0;j<groupList.size();j++) {
        		for(int i=0;i<headingSortedList.size();i++) {
        			if(!groupList.get(j).containsKey(headingSortedList.get(i)))
        			{
        				tdvalue+="<td></td>";
        			}
        			else
        			{
        				tdvalue+="<td>"+groupList.get(j).get(headingSortedList.get(i))+"</td>";
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
    		log.info("Exception occur in method getTableFormat: " + ex.toString());
        }
        return tablevalue;
	}
}
