package com.otaserver.client.utils;

import java.io.File;
import java.io.IOException;

import android.util.Log;

public class updateFile extends File{
	private static final String Tag = "updateFile" ;
	private String Path = null ;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2979723338975861258L;

	public updateFile(String path) {
		super(path);
		Path = path ;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getCanonicalPath() throws IOException {
		// TODO Auto-generated method stub
		return getAbsolutePath(); 
	}
	
}
