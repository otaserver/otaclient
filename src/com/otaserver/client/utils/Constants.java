package com.otaserver.client.utils;

public class Constants {
	public static final String http_socket_timeout= "http.socket.timeout" ;
	public static final String http_connection_timeout= "http.connection.timeout" ;
	public static final String http_connection_manager_timeout= "http.connection-manager.timeout" ;
	public static final String Parser_xml_NoNode ="No this node" ;
	public static final String Parser_xml_NoChild = "no child node" ;
	public static final String Parser_xml_Error = "parse error" ;

	//XML Elements name
	public static final String XML_firmwareupdate = "firmwareupdate" ;
	public static final String XML_firmware = "firmware" ;
	public static final String XML_num = "num" ;
	public static final String XML_name = "name" ;
	public static final String XML_object_to_name = "object_to_name" ;
	public static final String XML_desc_ = "desc_" ;
	public static final String XML_md5 = "md5" ;
	public static final String XML_size = "size" ;
	public static final String XML_level = "level" ;
	public static final String XML_needbackup = "needbackup" ;
	public static final String XML_downloadurl = "downloadurl" ;
	public static final String XML_Error = "Error" ;
	public static final String XML_Error_Code = "Code" ;
	public static final String XML_Error_Message = "Message" ;
	
	//OnlineFileUpdate path
//	public static final String FilePath = "/data/data/com.otaserver.client/lenovootadata/" ;
	public static final String FilePath = "/sdcard/lenovoota/" ;
	public static final String UpdateZipPackageFileName = "update.zip" ;
	public static final String UpdateInfoFileName = "updinfo.xml" ;
	
	
	//SDcardFileUpdate path
	public static final String SDFilePath = "/sdcard/" ;
	
	//SharePreferences-UpdateFileInfo
	public static final String SP_Name = "otaclient" ;
	public static final String SP_Tag_UpInfoIsNew = "upInfoIsNew" ;
	public static final String SP_MD5 = "md5" ;
	public static final String FileSize = "filesize" ;
	public static final String TargetVer = "targetver" ;
	public static final String UpdateInfo = "UpdateInfo" ;
	public static final String DownloadUrl = "downloadurl" ;
	
	//SharePreferences-AppSetting
	public static final String SP_Settings = "appsettings" ;
	public static final String SP_Settings_initSettings = "resetsettings" ;
	public static final String SP_Settings_WifiOnly = "wifionly" ;
	public static final String SP_Settings_PushOnOff = "pushswitch" ;
	public static final String SP_Settings_ReminderOnOff = "reminderswitch" ;
	
	
	/*
	 * 注册Push时需要的信息
	 */
	
	//请将此属性值改为自己的SID 
	public static final String Push_SID = "rfus001" ;
	//请不要改变下面常量的值:
	public static final String PUSH_REGISTER = "com.lenovo.leos.push.intent.REGISTER";
	public static final String PT_REVEIVER_ACTION = "PTReceiver";
	
	//Push开关状态
	public static final int PUSHSWITCH_ON = 1;
	public static final int PUSHSWITCH_OFF = 0;
	
	//APK自身升级
	public static final String DOWNLOAD_APK_URL = "";
	public static final String DOWNLOAD_APK_CONFIGFILE_URL = "http://fus.dev.surepush.cn/otaclient/otaclient.json";
	public static final String DOWNLOAD_APK_CONFIGFILE_SAVE_PATH = "/sdcard/lenovoota/apkupdater/";
	public static final String DOWNLOAD_APK_CONFIGFILE_NAME = "otaclient.json";
	public static final String DOWNLOAD_APK_SAVE_PATH = "/sdcard/lenovoota/apkupdater/";
	public static final String DOWNLOAD_APK_NAME = "otaclient.apk";
	public static final String JSON_PACKAGENAME = "PackageName";
	public static final String JSON_VERSIONCODE = "VersionCode";
	public static final String JSON_VERSIONNAME = "VersionName";
	public static final String JSON_DOWNLOADURL = "DownloadUrl";
	public static final String JSON_APKSIZE = "ApkSize";
	
	
	//properties file
	public static final String PropertiesFilePath = "/sdcard/" ;
	public static final String PropertiesFileName = "OTAServer.properties" ;
	public static final String PropertiesKey_serverAddress = "com.lenovo.ota.address" ;
	public static final String PropertiesKey_IMEI = "com.lenovo.ota.imei" ;
	public static final String PropertiesKey_DeviceModel  = "com.lenovo.ota.devicemodel" ;
	public static final String PropertiesKey_FirmwareType  = "com.lenovo.ota.firmwaretype" ;
	public static final String PropertiesKey_LocaleLanguage  = "com.lenovo.ota.localelanguage" ;
	
	//Reminder
	public static final String RemindAction = "android.net.conn.CONNECTIVITY_CHANGE" ;
	public static final String TimeRecordFileName = "timerecord" ;
	public static final String TimeRecordFilePath = "/sdcard/lenovoota/timer/" ;
	public static final String TimeForDays = "reminderfordays" ;
	
	
	//DownloadReceiver
	public static final String DOWNLOAD_START = "com.otaserver.client.download.start" ;
	public static final String DOWNLOAD_PAUSE = "com.otaserver.client.download.pause" ;
	public static final String DOWNLOAD_STOP = "com.otaserver.client.download.stop" ;
	
	//Clicking Notification starts to activity by Intent-action
	public static final String Intent_Bundle_Key_Notification = "com.otaserver.client.intent.bundle.key.notification" ;
	public static final String Intent_Bundle_Value_Notification = "com.otaserver.client.intent.bundle.value.notification" ;
	
	//Start download intent Bundle key
	public static final String Download_Start_Bundle_Name= "download_start_bundle" ;
	public static final String Download_Start_Bundle_key_url = "downloadurl" ;
	public static final String Download_Start_Bundle_key_md5 = "md5" ;
	public static final String Download_Start_Bundle_key_pkgSize = "pkgSize" ;

	//Kill otaActivity
	public static final String MainActivityIsKilled = "appiskilled" ;
	public static final String DownloadStatus_Killing = "downloadstatus_killing" ;


}
