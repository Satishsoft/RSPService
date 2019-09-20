package rs.publish.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rs.publish.controller.Services;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.annotation.PostConstruct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;


@Service
public class PIMConnectors {
	private final Logger log = LoggerFactory.getLogger(Services.class);
	private JSONObject headerDetails;
	@Autowired
	private Configuration configuration;

	@PostConstruct
	public void init() {
		headerDetails = configuration.ReadFileAsJson(configuration.GetRSConfigValues("RSHeader.json"));
	}

	/*
	 * GetUserDetails-> Get user Json data.
	 */
	public Response GetUserDetails(String userName, String password) {
		Response response = null;
		try {
			String bodyContent = "{\"params\":{\"query\":{\"name\":\"" + userName.trim()
					+ "\",\"filters\":{\"typesCriterion\":[\"user\"]}}}}";
			JSONObject url = (JSONObject) headerDetails.get("url");
			String urlValue = (String) url.get("value") + "/api/entitymodelservice/get";
			JSONObject headers = (JSONObject) headerDetails.get("headers");
			response = SendRequest(bodyContent, urlValue, headers);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("------------- PIM connector GetEntities Exception----------- " + ex.getMessage());
		}
		return response;
	}

	/*
	 * GetEntities-> Get List of Entities Array data in Json format.
	 */
	public ArrayList<JSONArray> GetEntities(String query) {
		ArrayList<JSONArray> entities_response = new ArrayList<JSONArray>();
		try {
			JSONObject entity_Body = configuration.ReadFileAsJson(configuration.GetRSConfigValues("RSEntityBody.json"));
			JSONObject url = (JSONObject) headerDetails.get("url");
			String urlValue = (String) url.get("value") + "/api/entityservice/get";
			JSONObject headers = (JSONObject) headerDetails.get("headers");
			JSONObject record_Count = (JSONObject) headerDetails.get("recordcount");
			JSONObject options = (JSONObject) ((JSONObject) entity_Body.get("params")).get("options");
			long recordSize = (long) record_Count.get("value");
			long maxSize = (long) options.get("maxRecords");
			if (recordSize > maxSize) {
				long r_Count = (recordSize % maxSize == 0) ? recordSize / maxSize : ((recordSize / maxSize + 1));
				for (int i = 0; i <= r_Count; i++) {
					options.put("maxRecords", maxSize);
					options.put("from", i * maxSize);
					JSONArray entities_Details = GetEntityResponse(query, entity_Body, urlValue, headers);
					if (entities_Details != null) {
						entities_response.add(entities_Details);
					} else {
						break;
					}
				}
			} else {
				JSONArray entities_Details = GetEntityResponse(query, entity_Body, urlValue, headers);
				if (entities_Details != null) {
					entities_response.add(entities_Details);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("-------------PIM connector GetEntities Exception-----------" + ex.getMessage());
		}
		return entities_response;
	}

	/*
	 * GetEntitiesArray-> Get Entities Array data in Json format.
	 */
	public JSONArray GetEntitiesArray(String Arraytype, Response response) {
		JSONArray entities_Array = null;
		try {
			JSONParser parser = new JSONParser();
			String jsonData_response = response.body().string();
			JSONObject Jobject = (JSONObject) parser.parse(jsonData_response);
			JSONObject ojObject_response = (JSONObject) Jobject.get("response");
			entities_Array = (JSONArray) ojObject_response.get(Arraytype);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return entities_Array;
	}

	/*
	 * GetAssetsFileName-> Get Assets file details in Json format.
	 */
	public Response GetAssetsFileName(String assets_id, String assets_type) {
		Response response = null;
		try {
			JSONObject url = (JSONObject) headerDetails.get("url");
			String urlValue = (String) url.get("value") + "/api/entityservice/get";
			JSONObject headers = (JSONObject) headerDetails.get("headers");
			String bodyContent = "{\r\n  \"params\": {\r\n    \"query\": {\r\n      \"id\" : \"" + assets_id.toString()
					+ "\",\r\n      \"filters\": {\r\n      \t\r\n        \"typesCriterion\": [\r\n          \""
					+ assets_type.toString()
					+ "\"\r\n        ]\r\n      }\r\n    },\r\n    \"fields\": {\r\n      \"attributes\": [\r\n        \"property_objectkey\",\"property_originalfilename\"\r\n      ]\r\n    },\r\n    \"sort\": {\r\n      \"properties\": [\r\n        {\r\n          \"createdDate\": \"_ASC\",\r\n          \"sortType\": \"_STRING\"\r\n        }\r\n      ]\r\n    },\r\n    \"options\": {\r\n      \"maxRecords\": 500,\r\n      \"from\": 0\r\n    }\r\n  }\r\n}";
			response = SendRequest(bodyContent, urlValue, headers);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}

	/*
	 * GetAssetsURL-> Get Assets URL details in Json format.
	 */
	public Response GetAssetsURL(String objectKey, String originalfilename) {
		Response response = null;
		try {
			JSONObject url = (JSONObject) headerDetails.get("url");
			String urlValue = (String) url.get("value") + "/api/binarystreamobjectservice/prepareUpload";
			JSONObject headers = (JSONObject) headerDetails.get("headers");
			String bodyContent = "{\r\n\"binaryStreamObject\": {\r\n\"id\": \"4573682d-a629-4116-81d1-e2a3024846e5\",\r\n\"type\": \"BinaryStreamObject\",\r\n\"properties\": {\r\n\"objectKey\": \""
					+ objectKey + "\",\r\n\"originalFileName\": \"" + originalfilename
					+ "\"\r\n}\r\n},\r\n\"returnRequest\": false,\r\n\"params\": {}\r\n}";
			response = SendRequest(bodyContent, urlValue, headers);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("------------- PIM connector GetAssetsURL Exception----------- " + ex.getMessage());
		}
		return response;
	}

	/*
	 * SendRequest-> Get Response from PIM in Json format.
	 */
	private Response SendRequest(String bodyContent, String urlValue, JSONObject headers) {
		Response response = null;
		try {
			String authFlag="";
			OkHttpClient client = new OkHttpClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, bodyContent);
			JSONObject jAuthFlag=(JSONObject) headerDetails.get("AuthFlag");
			 if (jAuthFlag!=null)authFlag= (String)jAuthFlag.get("value");
			if(authFlag==null || authFlag=="" || !authFlag.equalsIgnoreCase("yes"))
			{
			Request request = new Request.Builder().url(urlValue).post(body)
					.addHeader("x-rdp-version", (String) headers.get("x-rdp-version"))
					.addHeader("x-rdp-clientid", (String) headers.get("x-rdp-clientid"))
					.addHeader("x-rdp-tenantid", (String) headers.get("x-rdp-tenantid"))
					.addHeader("x-rdp-ownershipdata", (String) headers.get("x-rdp-ownershipdata"))
					.addHeader("x-rdp-userid", (String) headers.get("x-rdp-userid"))
					.addHeader("x-rdp-username", (String) headers.get("x-rdp-username"))
					.addHeader("x-rdp-firstname", (String) headers.get("x-rdp-firstname"))
					.addHeader("x-rdp-lastname", (String) headers.get("x-rdp-lastname"))
					.addHeader("x-rdp-useremail", (String) headers.get("x-rdp-useremail"))
					.addHeader("x-rdp-userroles", "[\"" + (String) headers.get("x-rdp-userroles") + "\"]")
					.addHeader("content-type", (String) headers.get("content-type"))
					.addHeader("cache-control", (String) headers.get("cache-control"))
					.addHeader("postman-token", (String) headers.get("postman-token")).build();
			response = client.newCall(request).execute();
			}
			else
			{
				Request request = new Request.Builder().url(urlValue).post(body)
						.addHeader("x-rdp-version", (String) headers.get("x-rdp-version"))
						.addHeader("x-rdp-clientid", (String) headers.get("x-rdp-clientid"))
						.addHeader("x-rdp-tenantid", (String) headers.get("x-rdp-tenantid"))
						.addHeader("x-rdp-ownershipdata", (String) headers.get("x-rdp-ownershipdata"))
						.addHeader("x-rdp-userid", (String) headers.get("x-rdp-userid"))
						.addHeader("x-rdp-username", (String) headers.get("x-rdp-username"))
						.addHeader("x-rdp-firstname", (String) headers.get("x-rdp-firstname"))
						.addHeader("x-rdp-lastname", (String) headers.get("x-rdp-lastname"))
						.addHeader("x-rdp-useremail", (String) headers.get("x-rdp-useremail"))
						.addHeader("x-rdp-userroles", "[\"" + (String) headers.get("x-rdp-userroles") + "\"]")
						.addHeader("content-type", (String) headers.get("content-type"))
						.addHeader("cache-control", (String) headers.get("cache-control"))
						//.addHeader("Connection", (String) headers.get("Connection"))
						//.addHeader("Content-Length", (String) headers.get("Content-Length"))
						//.addHeader("Accept-Encoding", (String) headers.get("Accept-Encoding"))
						//.addHeader("Host", (String) headers.get("Host"))
						//.addHeader("Accept", (String) headers.get("Accept"))
						//.addHeader("User-Agent", (String) headers.get("User-Agent"))
						.addHeader("Authorization", (String) headers.get("Authorization"))
						.addHeader("postman-token", (String) headers.get("postman-token")).build();
				response = client.newCall(request).execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * GetEntityFilterBodyData-> Get request body content as string.
	 */
	private String GetEntityFilterBodyData(String query, JSONObject entityBody) {
		String bodyContent = null;
		if (!query.isEmpty()) {
			JSONObject filter = (JSONObject) ((JSONObject) ((JSONObject) entityBody.get("params")).get("query"))
					.get("filters");
			JSONArray types_array = (JSONArray) filter.get("typesCriterion");
			types_array.clear();
			if (query.contains(",")) {
				String[] queryArray = query.split(",");
				for (int i = 0; i < queryArray.length; i++) {
					types_array.add(queryArray[i]);
				}
			} else {
				types_array.add(query);
			}
			bodyContent = (String) entityBody.toString();
		} else {
			bodyContent = entityBody.toString();
		}
		return bodyContent;
	}

	/*
	 * GetEntityResponse-> Get entity array from response in Json format.
	 */
	private JSONArray GetEntityResponse(String query, JSONObject entityBody, String urlValue, JSONObject headers) {
		JSONArray entities_Details = null;
		Response response = null;
		String bodyContent = null;
		bodyContent = GetEntityFilterBodyData(query, entityBody);
		response = SendRequest(bodyContent, urlValue, headers);
		entities_Details = GetEntitiesArray("entities", response);
		return entities_Details;
	}
	private Hashtable<String, String> GetHeaderList(JSONObject headers)
	{
	Hashtable<String, String> headers_List = new Hashtable<String, String>();
	try
	{
        Iterator keys = headers.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            headers_List.put(key, (String)headers.get(key));
        }
	}
	catch (Exception e) {
		e.printStackTrace();
	}
	return headers_List;
	}
}
