package com.amul.dc.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.amul.dc.R;
import com.amul.dc.helper.Commons;
import com.amul.dc.main.MainActivity;
import com.amul.dc.pojos.CitiesDto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class CityAuotCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<CitiesDto> resultList = new ArrayList<CitiesDto>();


    public CityAuotCompleteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public CitiesDto getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_item, null);
        }
        TextView label = (TextView) convertView.findViewById(R.id.spinnerTarget);
        label.setText(getItem(position).getCityName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {
                  //  if (MainActivity.getNetworkHelper().isOnline()) {

                        HashMap<String, String> postDataParams = new HashMap<String, String>();
                        postDataParams.put("city_name", constraint.toString());
                        postDataParams.put("user_id", MainActivity.getMainScreenActivity().getUserID());
                        int responseCode = 0;


                        String response = "";
                        URL url = null;
                        HttpURLConnection conn = null;
                        try {
                            url = new URL(Commons.SEARCH_CITIES);
                        } catch (MalformedURLException e2) {
                            e2.printStackTrace();
                        }
                        String TAG = "async";
                        try {

                            conn = (HttpURLConnection) url.openConnection();
                            conn.setReadTimeout(30000);
                            conn.setConnectTimeout(30000);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            conn.setRequestProperty("x-api-key", "422ECF82D48A9D5A9EA9A573544674C0");
                            conn.setRequestProperty("Accept", "application/json");

                            conn.setDoInput(true);
                            conn.setDoOutput(true);
                            OutputStream os = conn.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            writer.write(Commons.getPostDataString(postDataParams));

                            writer.flush();
                            writer.close();
                            os.close();
                            responseCode = conn.getResponseCode();

                            if (responseCode == HttpsURLConnection.HTTP_OK) {
                                String line;
                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                while ((line = br.readLine()) != null) {
                                    response += line;
                                }
                            } else if (responseCode == HttpsURLConnection.HTTP_CREATED) {
                                String line;
                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                while ((line = br.readLine()) != null) {
                                    response += line;
                                }
                            } else {
                                response = "";

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if (200 == responseCode) {
                            // Assign the data to the FilterResults
                            ArrayList<CitiesDto> citiesDtos = parseResponse(response);
                            filterResults.values = citiesDtos;
                            filterResults.count = citiesDtos.size();

                        } else {
                            // Log.i("LoginTask response", result == null ? "" : result);
                            // new ShowAlertInformation(getActivity()).showDialog("Error", "Error");

                        }


//                    } else {
//                        new ShowAlertInformation(mContext).showDialog("Network error", mContext.getString(R.string.offline));
//                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<CitiesDto>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }


    private ArrayList<CitiesDto> parseResponse(String result) {
        ArrayList<CitiesDto> citiesDtos = new ArrayList<>();
        try {
            JSONObject job = new JSONObject(result);
            String status = job.getString("status");
            if (status.equalsIgnoreCase("SUCCESS")) {
                JSONArray jsonArray = job.getJSONArray("cityData");
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        String city_id = jo.getString("city_id");
                        String city_name = jo.getString("city_name");
                        citiesDtos.add(new CitiesDto(city_id,city_name));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {

                // new ShowAlertInformation(getActivity()).showDialog("Error", "Data error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return citiesDtos;
    }
}