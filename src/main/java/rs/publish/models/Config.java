package rs.publish.models;

import java.io.InputStream;
import java.util.Properties;
;


public class Config {
    public  Properties readConfigfile(){
        Properties properties=new Properties();
        try{
        	String fileName="static/config.properties";
        	  InputStream ioStream =this.getClass()
            .getClassLoader()
            .getResourceAsStream(fileName);
        		    if(ioStream == null) {
        		        throw new IllegalArgumentException(fileName + " is not found 1");
        		    }
                    properties.load(ioStream);
        }catch (Exception ex){
           ex.printStackTrace();
        }
        return properties;
    }
}

