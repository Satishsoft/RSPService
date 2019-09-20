package rs.publish.models;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.util.IOUtils;

@Service
@Component
public class Configuration {
	@Value("${file.path}")
    private String filepath;
	public String GetRSConfigValues(String fileName) {	
		String values = "";
		try {
		//values = filepath.toString()+"/" + fileName;
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
		values=IOUtils.toString(inputStream,StandardCharsets.UTF_8);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return values;
	}

	public JSONObject ReadFileAsJson(String fileName) {
		JSONObject fileDataJson = null;
		try {
			JSONParser parser = new JSONParser();
			fileDataJson = (JSONObject) parser.parse(fileName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fileDataJson;
	}

}
