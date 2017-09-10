package com.amul.dc.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.amul.dc.main.MainActivity;
import com.amul.dc.pojos.ResponseDto;
import com.amul.dc.pojos.TransactionBeans;

import java.util.ArrayList;

public class UploadMultipartAsync extends AsyncTask<String, Void, ArrayList<ResponseDto>> {

	protected int responseCode = 0;
	private ArrayList<TransactionBeans> postDataList;

	protected ArrayList<ResponseDto> responselist = new ArrayList<ResponseDto>();

	public UploadMultipartAsync() {
	}

	public UploadMultipartAsync(ArrayList<TransactionBeans> postDataList) {
		this.postDataList = postDataList;
	}

	@Override
	protected ArrayList<ResponseDto> doInBackground(String... params) {
		try {

			String charset = "UTF-8";
			String requestURL = params[0].toString();
			for (int i = 0; i < postDataList.size(); i++) {
				TransactionBeans scan = postDataList.get(i);


				//poi_name, poi_details, poi_latitude, poi_longitude, poi_image, route_id, city_id, user_id
				MultipartUtility multipart = new MultipartUtility(requestURL, charset);
				//multipart.addHeaderField("x-api-key", "422ECF82D48A9D5A9EA9A573544674C0");
				multipart.addHeaderField("Accept", "application/json");
				multipart.addFormField("user_id", MainActivity.getMainScreenActivity().getUserID());
				multipart.addFormField("city_id", scan.getCityId());
				multipart.addFormField("route_id", scan.getRouteId());
				multipart.addFormField("poi_name", scan.getStoreName());
				multipart.addFormField("poi_details", scan.getStoreLocation());
				multipart.addFormField("poi_address", scan.getStoreLocation());
				multipart.addFormField("poi_latitude", scan.getLatitude());
				multipart.addFormField("poi_longitude", scan.getLongitude());
				if (null != scan.getImageOne())
					multipart.addFilePart("poi_image", scan.getImageOne());
//				if (null != scan.getImageTwo())
//					multipart.addFilePart("image[]", scan.getImageTwo());

				String response = multipart.finish();
				ResponseDto rdo = new ResponseDto();
				rdo.setUniqueId(scan.getUniqueId());
				rdo.setStatus(response);
				responselist.add(rdo);
				Log.v("rht", "Line : " + response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responselist;
	}

}
