package rs.publish.controller;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import rs.publish.models.Config;
import rs.publish.models.Constants;
import rs.publish.models.FinalResult;
import rs.publish.models.LoginResponse;
import rs.publish.models.PublishConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
@RestController
public class Services {
 @Autowired
 private PublishConverter publishConverter;
 /*
  * search-> get all Entities details query by q
 */
	@RequestMapping("/services/search")
	public FinalResult search(@RequestParam("q") String q) {
		//List<String>list=test();
		FinalResult finalresult = new FinalResult();
		Constants.getTenantConfigDetails();
		try {
			if (q != null) {
				if (q.indexOf('(') != -1 && q.indexOf(')') != -1) {
					q = q.substring(1, q.length() - 1);
				}
				finalresult = publishConverter.getEntities(q);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return (finalresult);
	}
	/*
	  * login-> login validation by userNmae and password
	 */
	@RequestMapping("/services/login")
	public LoginResponse login(@RequestParam("returnProfile") Optional<Boolean> returnProfile,@RequestParam("username") String username, @RequestParam("password") String password) {
		LoginResponse loginResponse = new LoginResponse();
		try {
			Constants.getTenantConfigDetails();
			loginResponse = publishConverter.loginInfo(username,password);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return (loginResponse);
	}
	
	private List<String> test() {
		List<String>list=new ArrayList();
		try{
            String text = new String(Files.readAllBytes(Paths.get("c:\\test\\ts.json")), StandardCharsets.UTF_8);

            JSONObject obj = new JSONObject(text);
            JSONArray arr = obj.getJSONArray("entities");
            for(int i = 0; i < arr.length(); i++){
                String id = arr.getJSONObject(i).getString("id");
                String type = arr.getJSONObject(i).getString("type");
             if(type.equals("option")) {
            	 list.add(id);
             }
               
            }
            if(list.size()>0) {
            	String s=null;
            	for(int i = 0; i < list.size(); i++){
            	 s=s+","+list.get(i);
            	}
            	System.out.println(s);
            }
        }
        catch(Exception ex){
            System.out.println(ex.toString());
        }
		return list;
	}
}
