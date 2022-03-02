package rs.publish.models;


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
     
   //presalesinta
     /*
     public static final String IMAGESRelation = "hasimages";
     public static final String DOCUMENTRelation = "hasdocuments";
     public static final String CHILDOFRelation = "childof";
     public static final String CROSSSELLRelation = "crosssell";
	 public static final String locale= "en-US";
	 public static final  String tenantId="presalesinta";
	 public static final  String userId="integrationadmin@riversand.com";
	 public static final String auth_client_id="UP01Bb5v7BlRUfW1Yl3MdH5P5KlgjkSD";
	 public static final String auth_client_secret="YVGnnNmA6sSTha88zHYEglz2XPx75iXbbR5VeW2I0M6_VQ-sXQtHxEegSuwOB7bJ";
	 public static final String attributes="_ALL";
	 public static final String relationships="_ALL";
	 public static final Integer maxRecords=1000;
	 public static final String types="product,sku";
	 public static final Boolean imageurl=true;
	 public static final Boolean readFromContext=true;
	 public static final String  contextName="RS Print";
	 public static final String contextType="country";
	 public static final String WebURL="https://presalesinta.riversand.com";
	 public static final String WebPORT="";
	 public static final String assetsPORT="";
	 */
    //presalesintb
     /*
      public static final Boolean RELATIONSHIPALL=false;
	 public static final String IMAGESRelation = "hasimages";
	 public static final String DOCUMENTRelation = "hasdocuments";
	 public static final String CHILDOFRelation = "ischildof";
	 public static  String LOCALES= "en-US";
	 public static final String TENANTID="presalesintb";
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
	 public static  String  CONTEXTNAME="RS Print";
	 public static final String CONTEXTTYPE="country";
	 public static final String WEBURL="https://presalesintb.riversand.com";
	 public static final String WEBPORT="";
	 public static final String ASSETSPORT="";
	 public static final Boolean ATTRIBUTEFROMREFERENCE=false;
	 public static final String ATTRIBUTEREFERENCETYPE="printattributeslist";
	 public static final String REFERENCEATTRIBUTE="pvalue";
	 public static final String PRINTIMAGETYPE= "printimagetype";
*/
     
     //smuckersds2

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
	 
	 public static void setQueryLocaleContext(String querylocale, String querycontext) {
		 if(querycontext==null || querycontext.isEmpty()) {
			 CONTEXTNAME ="United States";//"United States";
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
	//Weber
	/*
	     public static final String IMAGESRelation = "hasimages";
	     public static final String DOCUMENTRelation = "hasdocuments";
	     public static final String CHILDOFRelation = "ischildof";
	     public static final String CROSSSELLRelation = "crosssell";
		 public static final String locale= "en-US";
		 public static final  String tenantId="weberds";
		 public static final  String userId="weber.admin@riversand.com";
		 public static final String auth_client_id="epIyVsxR0Nm3tYOC4W478T6QbjvAiFSK";
		 public static final String auth_client_secret="-CDrsJ59Cep5bD52in-qzAIlvM8R6RTSrA_MdWOpUHYyly-2mLNOvO9LeK9JQbk2";
		 public static final String attributes="_ALL";
		 public static final String relationships="_ALL";
		 public static final Integer maxRecords=1000;
		 public static final String types="grillmodel,grillvariant";
		 public static final Boolean imageurl=true;
		 public static final Boolean readFromContext=false;
		 public static final String  contextName="RS Print";
		 public static final String contextType="country";
		 public static final String WebURL="http://manage.rdpdsna07.riversand-dataplatform.com";
		 public static final String WebPORT="8085";
		 public static final String assetsPORT="9095";
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
}
