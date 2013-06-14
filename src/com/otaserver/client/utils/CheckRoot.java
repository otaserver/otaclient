package com.otaserver.client.utils;



public class CheckRoot {

	public synchronized boolean getRootAhth() {
		try{
			Process  process = Runtime.getRuntime().exec("su");
		    process.waitFor();
		    int i = process.exitValue();
		    if(i>0){
		    	return true ;
		    }
			
		}catch(Exception e){
			e.printStackTrace() ;
		}
	
		return false ;

	}

}
