package com.amul.dc.asynctask;

import android.os.AsyncTask;

import com.amul.dc.helper.Commons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AsyncProcess extends AsyncTask<String, Void, String> {

	protected int responseCode = 0;
	private HashMap<String, String> postDataParams;

	public AsyncProcess() {
	}

	public AsyncProcess(HashMap<String, String> postDataParams) {
		this.postDataParams = postDataParams;
	}

	@Override
	protected String doInBackground(String... params) {
		String response = "";
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(params[0].toString());
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}
		String TAG = "async";
		try {

			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(30000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("x-api-key", "422ECF82D48A9D5A9EA9A573544674C0");
			conn.setRequestProperty("Accept","application/json");
			
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));
			writer.write(Commons.getPostDataString(postDataParams));

			writer.flush();
			writer.close();
			os.close();
			responseCode=conn.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {
				String line;
				BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line=br.readLine()) != null) {
					response+=line;
				}
			}else if(responseCode==HttpsURLConnection.HTTP_CREATED){
				String line;
				BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line=br.readLine()) != null) {
					response+=line;
				}
			}
			else {
				response="";

			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		return response.toString();
	}

}
