package com.superfiretruck.sketchabit;

import java.io.*;
import java.net.*;

import android.graphics.Bitmap;
import android.util.Log;

public class Util {

	public static boolean uploadBitmap(Bitmap bitmap, String parentId) throws Exception {
		final String BOUNDARY = "====CATSCATSCATS==";
		//URL url = new URL("http://www.postbin.org/q7oqzc");
		URL url = new URL("http://magicaluploadingurl.com/dostuff.php?parent="+parentId);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+BOUNDARY);
		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		outStream.writeBytes("--"+BOUNDARY+"\r\n");
		outStream.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\"upload.png\"\r\n");
		outStream.writeBytes("Content-Type: image/png\r\n");
		outStream.writeBytes("\r\n");
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
		outStream.writeBytes("\r\n");
		outStream.writeBytes("--"+BOUNDARY+"--\r\n");
		Log.d("panda", "upload response: "+conn.getResponseCode());
		if(conn.getResponseCode() == 200) {
			return true;
		} else {
			return false;
		}
	}
	
}
