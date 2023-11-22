package rs.publish.models;

import java.util.Properties;

public class Constants {
	 public static final String type="Type";
	 public static final String id="id";
	 public static final String name="Name";
	 public static final Integer TOTALRECORD=10000;
	 public static final String LOCALE = "locale";
     public static final String RELTO = "relTo";
     public static final String CATALOG ="</catalog>";
     public static final String DATA="data";
     public static final String CONTEXTS="contexts";
     public static final String CONTEXT="context";
     public static final String ATTRIBUTES="attributes";
     public static final String RELATIONSHIPS="relationships";
     public static final String VALUES="values";
     public static final String VALUE="value";
     public static final String NAME="name";
     public static final String TYPE="type";
     public static final String PARAMS="params";
     public static final String QUERY="query";
     public static final String FILTERS="filters";
     public static final String PROPERTIES="properties";
     public static final String DOWNLOADURL = "downloadURL";
     public static final String ISPRIMARY = "isprimary";
     public static final String GROUP = "group";
     
	 //RS Config details  
    //presalesintb
    public static Boolean RELATIONSHIPALL;
	 public static String IMAGESRelation;
	 public static String DOCUMENTRelation;
	 public static String CHILDOFRelation;
	 public static String LOCALES;
	 public static String TENANTID;
	 public static String USERID;
	 public static String AUTH_CLIENT_ID;
	 public static String AUTH_CLIENT_SECRECT;
	 public static String ATTRIBUTE;
	 public static String RELATISHIONSHIP;
	 public static Integer MAXRECORD;
	 public static String TYPES;
	 public static Boolean IMAGEURL;
	 public static Boolean IMAGEURLList;
	 public static Boolean READFROMCONTEXT;
	 public static String  CONTEXTNAME;
	 public static String CONTEXTTYPE;
	 public static String WEBURL;
	 public static String WEBPORT;
	 public static String ASSETSPORT;
	 public static Boolean ATTRIBUTEFROMREFERENCE;
	 public static String ATTRIBUTEREFERENCETYPE;
	 public static String REFERENCEATTRIBUTE;
	 public static String PRINTIMAGETYPE;
	 
	   public static void getTenantConfigDetails(){
		   Config config=new Config();
	        Properties propertiesObj= config.readConfigfile();
	        if(propertiesObj!=null && propertiesObj.size()>0){
	        	Constants.RELATIONSHIPALL=Boolean.getBoolean(propertiesObj.get("RELATIONSHIPALL").toString().trim());
	        	Constants.IMAGESRelation=propertiesObj.get("IMAGESRelation").toString().trim();
	        	Constants.DOCUMENTRelation=propertiesObj.get("DOCUMENTRelation").toString().trim();
	        	Constants.CHILDOFRelation=propertiesObj.get("CHILDOFRelation").toString().trim();
	        	Constants.LOCALES=propertiesObj.get("LOCALES").toString().trim();
	        	Constants.TENANTID=propertiesObj.get("TENANTID").toString().trim();
	        	Constants.USERID=propertiesObj.get("USERID").toString().trim();
	        	Constants.CONTEXTNAME=propertiesObj.get("CONTEXTNAME").toString().trim();
	        	Constants.READFROMCONTEXT=Boolean.getBoolean(propertiesObj.get("READFROMCONTEXT").toString().trim());
	        	Constants.CONTEXTTYPE=propertiesObj.get("CONTEXTTYPE").toString().trim();
	        	Constants.AUTH_CLIENT_ID=propertiesObj.get("AUTH_CLIENT_ID").toString().trim();
	        	Constants.AUTH_CLIENT_SECRECT=propertiesObj.get("AUTH_CLIENT_SECRECT").toString().trim();
	        	Constants.ATTRIBUTE=propertiesObj.get("ATTRIBUTE").toString().trim();
	        	Constants.RELATISHIONSHIP=propertiesObj.get("RELATISHIONSHIP").toString().trim();
	        	Constants.MAXRECORD=Integer.parseInt(propertiesObj.get("MAXRECORD").toString().trim());
	        	Constants.TYPES=propertiesObj.get("TYPES").toString().trim();
	        	Constants.IMAGEURL=Boolean.getBoolean(propertiesObj.get("IMAGEURL").toString().trim());
	        	Constants.IMAGEURLList=Boolean.getBoolean(propertiesObj.get("IMAGEURLList").toString().trim());
	        	Constants.WEBURL=propertiesObj.get("WEBURL").toString().trim();
	        	Constants.WEBPORT=propertiesObj.get("WEBPORT").toString().trim();
	        	Constants.ASSETSPORT=propertiesObj.get("ASSETSPORT").toString().trim();
	        	Constants.ATTRIBUTEFROMREFERENCE=Boolean.getBoolean(propertiesObj.get("ATTRIBUTEFROMREFERENCE").toString().trim());
	        	Constants.ATTRIBUTEREFERENCETYPE=propertiesObj.get("ATTRIBUTEREFERENCETYPE").toString().trim();
	        	Constants.REFERENCEATTRIBUTE=propertiesObj.get("REFERENCEATTRIBUTE").toString().trim();
	        	Constants.REFERENCEATTRIBUTE=propertiesObj.get("REFERENCEATTRIBUTE").toString().trim();
	        	Constants.PRINTIMAGETYPE=propertiesObj.get("PRINTIMAGETYPE").toString().trim();
	        	
	        }
	    }
	 
    /* public static final Boolean RELATIONSHIPALL=false;
	 public static final String IMAGESRelation = "hasimages";
	 public static final String DOCUMENTRelation = "hasdocuments";
	 public static final String CHILDOFRelation = "childof";
	 public static String LOCALES= "en-US";
	 public static final String TENANTID="presalesinta";
	 public static final String USERID="integrationadmin@riversand.com";
	 public static final String AUTH_CLIENT_ID="UP01Bb5v7BlRUfW1Yl3MdH5P5KlgjkSD";
	 public static final String AUTH_CLIENT_SECRECT="YVGnnNmA6sSTha88zHYEglz2XPx75iXbbR5VeW2I0M6_VQ-sXQtHxEegSuwOB7bJ";
	 public static final String ATTRIBUTE="_ALL";
	 public static final String RELATISHIONSHIP="_ALL";
	 public static final Integer MAXRECORD=1000;
	 public static final String TYPES="product,sku";
	 public static final Boolean IMAGEURL=true;
	 public static final Boolean IMAGEURLList=true;
	 public static final Boolean READFROMCONTEXT=true;
	 public static String  CONTEXTNAME="RS Print";
	 public static final String CONTEXTTYPE="country";
	 public static final String WEBURL="https://presalesinta.riversand.com";
	 public static final String WEBPORT="";
	 public static final String ASSETSPORT="";
	 public static final Boolean ATTRIBUTEFROMREFERENCE=false;
	 public static final String ATTRIBUTEREFERENCETYPE="printattributeslist";
	 public static final String REFERENCEATTRIBUTE="pvalue";
	 public static final String PRINTIMAGETYPE= "printimagetype";

     
     //smuckersds2
/*
     public static final Boolean RELATIONSHIPALL=false;
	 public static final String IMAGESRelation = "hasimages";
	 public static final String DOCUMENTRelation = "hasdocuments";
	 public static final String CHILDOFRelation = "ischildof";
	 public static  String LOCALES= "en-US";
	 public static final String TENANTID="smuckersds2";
	 public static final String USERID="system";
	 public static final String AUTH_CLIENT_ID="m1cAHepI0x9OT817qEcnY9onEzJEDnoJ";
	 public static final String AUTH_CLIENT_SECRECT="xJXK8Yid6Py_O9va4Xs5m975vt0AuVJ60Z3OelETEouA3Yx3ER5-AZOHQ0WRcyfe";
	 public static final String ATTRIBUTE="_ALL";
	 public static final String RELATISHIONSHIP="_ALL";
	 public static final Integer MAXRECORD=1000;
	 public static final String TYPES="itmworkinprogress,itmfinishedgoodvariant,itmintermediate,itmpackagingmaterial,itmfinishedgood,itmtradeitem,itmnontradeditem,itmsemifinishedgood,itmrawmaterialingredient";
	 public static final Boolean IMAGEURL=true;
	 public static final Boolean IMAGEURLList=true;
	 public static final Boolean READFROMCONTEXT=true;
	 public static  String  CONTEXTNAME="United States";
	 public static final String CONTEXTTYPE="country";
	 public static final String WEBURL="https://smuckersds2.riversand.com";
	 public static final String WEBPORT="";
	 public static final String ASSETSPORT="";
	 public static final Boolean ATTRIBUTEFROMREFERENCE=false;
	 public static final String ATTRIBUTEREFERENCETYPE="printattributeslist";
	 public static final String REFERENCEATTRIBUTE="pvalue";
	 public static final String PRINTIMAGETYPE= "printimagetype";
	 */
	 
	//Veeder
	/*
     public static final Boolean RELATIONSHIPALL=true;
	 public static final String IMAGESRelation = "hasimages";
	 public static final String DOCUMENTRelation = "hasdocuments";
	 public static final String CHILDOFRelation = "ischildof";
	 public static String LOCALES= "en-US";
	 public static final String TENANTID="veederds";
	 public static final String USERID="veeder.businessadmin@riversand.com";
	 public static final String AUTH_CLIENT_ID="LjqjYkBsnqOZ4te73LS04RQtw105sfu8";
	 public static final String AUTH_CLIENT_SECRECT="ddqWaXlR3UnSl7CWpdNi9ng5gz4RH9W3DqPVpOofgms9VNMu_PI57IsJmtUqG1l2";
	 public static final String ATTRIBUTE="_ALL";
	 public static final String RELATISHIONSHIP="_ALL";
	 public static final Integer MAXRECORD=1000;
	 public static final String TYPES="parentitem,item";
	 public static final Boolean IMAGEURL=true;
	 public static final Boolean READFROMCONTEXT=false;
	 public static String CONTEXTNAME="RS Print";
	 public static final String CONTEXTTYPE="country";
	 public static final String WEBURL="https://veederds.riversand.com";
	 public static final String WEBPORT="";
	 public static final String ASSETSPORT="";
	 public static final Boolean ATTRIBUTEFROMREFERENCE=false;
	 public static final String ATTRIBUTEREFERENCETYPE="printattributeslist";
	 public static final String REFERENCEATTRIBUTE="pvalue";
	 public static final String PRINTIMAGETYPE= "printimagetype";
	*/
//Asendbpim
     /*
     public static final String IMAGESRelation = "hasimages";
     public static final String DOCUMENTRelation = "hasdocuments";
     public static final String CHILDOFRelation = "ischildof";
     public static final String CROSSSELLRelation = "crosssell";
	 public static final String locale= "en-US";
	 public static final  String tenantId="ascendbpim";
	 public static final  String userId="ascend.badmin@riversand.com";
	 public static final String auth_client_id="KbpZ004f4VgDsm3ftMOd2ZlKsWEakzMc";
	 public static final String auth_client_secret="f5fgqpB4f6pKCdS7rlzjd_EzE-M1ILoQ57Air8bxlYiOqL50TfKW5li7dA2Jua58";
	 public static final String attributes="_ALL";
	 public static final String relationships="_ALL";
	 public static final Integer maxRecords=1000;
	 public static final String types="product,sku,item,parentitem";
	 public static final Boolean imageurl=true;
	 public static final Boolean readFromContext=false;
	 public static final String  contextName="RS Print";
	 public static final String contextType="country";
	 public static final String WebURL="https://ascendbpim.riversand.com";
	 public static final String WebPORT="";
	 public static final String assetsPORT="";
*/
     
     public static void setQueryLocaleContext(String querylocale, String querycontext) {
		 if(querycontext==null || querycontext.isEmpty()) {
			 CONTEXTNAME ="RS Print";//"United States";
		 }
		 else{
			 CONTEXTNAME =querycontext;
		 }
		 if(querylocale==null || querylocale.isEmpty()) {
			 LOCALES="en-US";
		 }
		 else{
			 LOCALES=querylocale;
		 }
		 
	 }
}
