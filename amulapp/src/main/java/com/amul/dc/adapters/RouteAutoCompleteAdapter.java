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
import com.amul.dc.fragment.HomeFragment;
import com.amul.dc.helper.Commons;
import com.amul.dc.helper.ShowAlertInformation;
import com.amul.dc.main.MainActivity;
import com.amul.dc.pojos.RoutesDto;

import org.json.JSONArray;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class RouteAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private Context mContext;
    private List<RoutesDto> resultList = new ArrayList<RoutesDto>();


    public RouteAutoCompleteAdapter(Context context) {
        mContext = context;


    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public RoutesDto getItem(int index) {
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
        label.setText(getItem(position).getRouteName());
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
                        postDataParams.put("route_name", constraint.toString());
                        postDataParams.put("city_id", HomeFragment.citiesDto.getCityId());
                        postDataParams.put("user_id", MainActivity.getMainScreenActivity().getUserID());
                        int responseCode = 0;
                        String response = "";
                        URL url = null;
                        HttpURLConnection conn = null;
                        try {
                            url = new URL(Commons.SEARCH_ROUTES);
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
                            ArrayList<RoutesDto> routesDtos = parseResponse(response);
                            filterResults.values = routesDtos;
                            filterResults.count = routesDtos.size();

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
                    resultList = (List<RoutesDto>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }



    private ArrayList<RoutesDto> parseResponse(String result) {
        ArrayList<RoutesDto> routesDtos = new ArrayList<>();
        try {
            JSONObject job = new JSONObject(result);
            String status = job.getString("status");
            if (status.equalsIgnoreCase("SUCCESS")) {
                JSONArray jsonArray = job.getJSONArray("routeData");
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        String city_id = jo.getString("city_id");
                        String route_id = jo.getString("route_id");
                        String route_name = jo.getString("route_name");
                        routesDtos.add(new RoutesDto(city_id, route_id,route_name));
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
        return routesDtos;
    }
}