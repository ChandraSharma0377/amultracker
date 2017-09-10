
package com.amul.dc.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amul.dc.R;
import com.amul.dc.adapters.AuotCompleteAdapter;
import com.amul.dc.adapters.RouteAutoCompleteAdapter;
import com.amul.dc.adapters.SpinAdapter;
import com.amul.dc.adapters.TransactionAdapter;
import com.amul.dc.asynctask.AsyncProcess;
import com.amul.dc.db.DataHelperClass;
import com.amul.dc.helper.Commons;
import com.amul.dc.helper.ShowAlertInformation;
import com.amul.dc.main.MainActivity;
import com.amul.dc.pojos.CitiesDto;
import com.amul.dc.pojos.RoutesDto;
import com.amul.dc.pojos.TransactionBeans;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private final String TAG = HomeFragment.class.getSimpleName();
    private Spinner sp_cities, sp_route;
    private ArrayList<String> routeList = new ArrayList<String>();
    private ArrayList<String> citiesList = new ArrayList<String>();
    private ListView list_view;
    private ImageView iv_add_dc, iv_add_city,iv_add_route;
    private TransactionAdapter transactionAdapter;
    private ArrayList<TransactionBeans> arraylist = new ArrayList<TransactionBeans>();
    private AutoCompleteTextView tv_autoCompleteCity,tv_autoCompleteRoute;
    private String error="";
    public static CitiesDto citiesDto = null;
    public static RoutesDto routesDto = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.lay_landing, container, false);
        list_view = (ListView) view.findViewById(R.id.list_view);
        sp_cities = (Spinner) view.findViewById(R.id.sp_cities);
        sp_route = (Spinner) view.findViewById(R.id.sp_route);
        iv_add_dc = (ImageView) view.findViewById(R.id.iv_add_dc);
        iv_add_city = (ImageView) view.findViewById(R.id.iv_add_city);
        iv_add_route = (ImageView) view.findViewById(R.id.iv_add_route);
        sp_route.setEnabled(false);
        iv_add_dc.setOnClickListener(this);
        iv_add_city.setOnClickListener(this);
        iv_add_route.setOnClickListener(this);
        initSpinners();

        tv_autoCompleteCity = (AutoCompleteTextView) view.findViewById(R.id.tv_autoCompleteCity);
        tv_autoCompleteCity.setThreshold(3);
        tv_autoCompleteCity.setAdapter(new AuotCompleteAdapter(getActivity()));
        tv_autoCompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                citiesDto = (CitiesDto) adapterView.getItemAtPosition(position);
                tv_autoCompleteCity.setText(citiesDto.getCityName());
                MainActivity.getMainScreenActivity().setPrefCityDto(citiesDto);
                setListData();
            }
        });

        tv_autoCompleteRoute = (AutoCompleteTextView) view.findViewById(R.id.tv_autoCompleteRoute);
        tv_autoCompleteRoute.setThreshold(3);
        tv_autoCompleteRoute.setAdapter(new RouteAutoCompleteAdapter(getActivity()));
        tv_autoCompleteRoute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                routesDto = (RoutesDto) adapterView.getItemAtPosition(position);
                tv_autoCompleteRoute.setText(routesDto.getRouteName());
                MainActivity.getMainScreenActivity().setPrefRouteDto(routesDto);
                setListData();
            }
        });
        citiesDto = MainActivity.getMainScreenActivity().getPrefCityDto();
        routesDto = MainActivity.getMainScreenActivity().getPrefRouteDto();
        if(null != citiesDto){
            tv_autoCompleteCity.setText(citiesDto.getCityName());
        }
        if(null != routesDto){
            tv_autoCompleteRoute.setText(routesDto.getRouteName());
        }
        setListData();


        sp_route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    //selectedText.setTextColor(Color.WHITE);
                }
                if (position > 0) {

                } else {
                    //Toast.makeText(getActivity(),"Please select city first",Toast.LENGTH_LONG).show();
                }

            }

        });
        sp_cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    //selectedText.setTextColor(Color.WHITE);
                }
                if (position > 0) {

                    sp_route.setEnabled(true);

                } else {
                    sp_route.setEnabled(false);


                }

            }

        });
        return view;
    }

    private void initSpinners() {
        citiesList.clear();
        routeList.clear();
        citiesList.add("Select City");
        citiesList.add("Ahemdabad");
        citiesList.add("Ahmednagar");
        citiesList.add("Gandhinagar");
        citiesList.add("Surat");

        routeList.add("Select Route");
        routeList.add("Route Ahemdabad");
        routeList.add("Route Ahmednagar");
        routeList.add("Route Gandhinagar");
        routeList.add("Route Surat");
        SpinAdapter cityAdapter = new SpinAdapter(getActivity(), citiesList);
        sp_cities.setAdapter(cityAdapter);
        SpinAdapter routeAdapter = new SpinAdapter(getActivity(), routeList);
        sp_route.setAdapter(routeAdapter);
    }


    private void setListData() {
        if(null != routesDto && null != citiesDto) {
            if (MainActivity.getNetworkHelper().isOnline()) {
                HashMap<String, String> postDataParams = new HashMap<String, String>();
                postDataParams.put("city_id", citiesDto.getCityId());
                postDataParams.put("route_id", routesDto.getRouteId());
                postDataParams.put("user_id", MainActivity.getMainScreenActivity().getUserID());
                new GetAllDCsAsync(postDataParams).execute(Commons.GET_ROUTE_DC_DETAILS);
            } else {
                new ShowAlertInformation(getActivity()).showDialog("Network error", getActivity().getString(R.string.offline));
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        MainActivity.getMainScreenActivity().actionBarTitle.setText("AMUL TRACKER");
        if (MainActivity.getMainScreenActivity().checkLocationPermissionAllowed())
            MainActivity.getMainScreenActivity().getLocation();
        MainActivity.getMainScreenActivity().showHideBottomLay(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.iv_add_dc:
                if (tv_autoCompleteCity.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please select city first", Toast.LENGTH_LONG).show();
                } else if (!tv_autoCompleteCity.getText().toString().equals(citiesDto.getCityName())) {
                    Toast.makeText(getActivity(), "Please select valid city", Toast.LENGTH_LONG).show();
                } else if (tv_autoCompleteRoute.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please select route first", Toast.LENGTH_LONG).show();
                } else if (!tv_autoCompleteRoute.getText().toString().equals(routesDto.getRouteName())) {
                    Toast.makeText(getActivity(), "Please select valid route", Toast.LENGTH_LONG).show();
                } else {
                    MainActivity.getMainScreenActivity().replaceFragmentWithBackStack(getActivity(),
                            new DcDetailsFragment(), TAG, MainActivity.tabHome);
                }

                break;
            case R.id.iv_add_city:
                createAddCityDia(getActivity(),true);
                break;
            case R.id.iv_add_route:
                if(tv_autoCompleteCity.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Please select city first",Toast.LENGTH_LONG).show();
                }else {
                    if(tv_autoCompleteCity.getText().toString().equals(citiesDto.getCityName())) {
                        createAddCityDia(getActivity(), false);
                    }else{
                        Toast.makeText(getActivity(),"Please select valid city",Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }

    }

    private void createAddCityDia(final Context mContext, final boolean isCityAdd) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View customDialogView = inflater.inflate(R.layout.dialog_product, null, false);
        final EditText edtName = (EditText) customDialogView.findViewById(R.id.edtName);
        Button btncancel = (Button) customDialogView.findViewById(R.id.btncancel);
        Button btnok = (Button) customDialogView.findViewById(R.id.btnok);
        if(isCityAdd) {
            builder.setTitle("Add City");
            error = "City Name cann't be blank.";
        }else{
            builder.setTitle("Add Route");
            edtName.setHint("Enter route name");
            error = "Route Name cann't be blank.";
            btnok.setText("ADD ROUTE");
        }
        builder.setView(customDialogView);
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCancelable(false);

        btnok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edtName.setError(null);
                if (edtName.getText().toString().equals("")) {
                    edtName.setError(error);
                } else {
                    if (MainActivity.getNetworkHelper().isOnline()) {
                        HashMap<String, String> postDataParams = new HashMap<String, String>();
                        if(isCityAdd) {
                            postDataParams.put("city_name", edtName.getText().toString());
                            postDataParams.put("user_id", MainActivity.getMainScreenActivity().getUserID());
                            new AddCityAndRouteAsync(postDataParams,isCityAdd).execute(Commons.ADD_CITY);
                        }else{
                            postDataParams.put("city_id", citiesDto.getCityId());
                            postDataParams.put("route_name", edtName.getText().toString());
                            postDataParams.put("user_id", MainActivity.getMainScreenActivity().getUserID());
                            new AddCityAndRouteAsync(postDataParams,isCityAdd).execute(Commons.ADD_ROUTE);
                        }
                        mAlertDialog.dismiss();
                    } else {
                        new ShowAlertInformation(mContext).showDialog("Network error", mContext.getString(R.string.offline));
                    }
                }
            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

            }
        });

        mAlertDialog.show();

    }

    private class AddCityAndRouteAsync extends AsyncProcess {
        private ProgressDialog progressDialog;
        private boolean isCityAdd;

        public AddCityAndRouteAsync(HashMap<String, String> postDataParams, boolean isCityAdd) {
            super(postDataParams);
            this.isCityAdd = isCityAdd;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "", "Updating please wait...");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);

        }
        @Override
        protected void onPostExecute(String result) {

            if (200 == responseCode) {
                try {
                    JSONObject job = new JSONObject(result);
                    String status = job.getString("status");
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        try {
                            if(isCityAdd) {
                                //                        {
//                            "cityData": {
//                            "city_name": "Saket Nagar",
//                                    "city_id": 1627
//                        },
//                            "status": "SUCCESS"
//                        }
                                JSONObject jo = job.getJSONObject("cityData");
                                String city_name = jo.getString("city_name");
                                Toast.makeText(getActivity(), "City Name: " + city_name + " is added successfully", Toast.LENGTH_LONG).show();
                            }else {
//                                {
//                                    "routeData":{
//                                    "route_name":"Sakinaka",
//                                            "city_id":"1630,",
//                                            "user_id":"20,",
//                                            "updated_at":"2017-09-09 22:09:43",
//                                            "created_at":"2017-09-09 22:09:43",
//                                            "route_id":6
//                                },
//                                    "status":"SUCCESS"
//                                }
                                JSONObject jo = job.getJSONObject("routeData");
                                String route_name = jo.getString("route_name");
                                Toast.makeText(getActivity(), "Route Name: " + route_name + " is added successfully", Toast.LENGTH_LONG).show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        new ShowAlertInformation(getActivity()).showDialog("Error", "Failed to update.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new ShowAlertInformation(getActivity()).showDialog("Error", "Failed to update.");
                }
                System.out.println("AddCityAndRouteAsync result is : " + (result == null ? "" : result));

            } else {
                Log.i("AddCityAndRouteAsync", result == null ? "" : result);
                new ShowAlertInformation(getActivity()).showDialog("Error", "Error");
            }
            progressDialog.dismiss();
        }
    }

    private class GetAllDCsAsync extends AsyncProcess {
        private ProgressDialog progressDialog;


        public GetAllDCsAsync(HashMap<String, String> postDataParams) {
            super(postDataParams);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "", "Updating please wait...");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);

        }
        @Override
        protected void onPostExecute(String result) {

            if (200 == responseCode) {
                try {
                    JSONObject job = new JSONObject(result);
                    String status = job.getString("status");
                    if (status.equalsIgnoreCase("SUCCESS")) {
                        try {
                            arraylist.clear();
////                                {
//                            "poi_id": 3,
//                                    "poi_name": "Gokul Dugdh",
//                                    "poi_address": null,
//                                    "poi_details": "19, Koparkhairane Village Road\nGaothan, Sector 19, Kopar Khairane\nNavi Mumbai, Maharashtra 400709",
//                                    "poi_latitude": "19.1056556",
//                                    "poi_longitude": "72.9986984",
//                                    "poi_image": "http://13.126.111.240/public/upload/poi/Gokul-dugdh-6.png",
//                                    "route_id": 6,
//                                    "city_id": 1630,
//                                    "user_id": 20,
//                                    "created_at": "2017-09-09 22:57:44",
//                                    "updated_at": "2017-09-09 22:57:44"
//                        }
                                JSONArray jo = job.getJSONArray("poiData");

                                for (int i = 0; i< jo.length();i++){
                                    JSONObject jobj = jo.getJSONObject(i);
                                    TransactionBeans transactionBeans = new TransactionBeans();
                                   // transactionBeans.setUniqueId(cursor.getInt(0));
                                    transactionBeans.setStoreName(jobj.getString("poi_name"));
                                    transactionBeans.setStoreLocation(jobj.getString("poi_details"));
                                    transactionBeans.setScandatetime(jobj.getString("created_at"));
                                    transactionBeans.setGpscoordinate(jobj.getString("poi_latitude")+","+jobj.getString("poi_longitude"));
                                    transactionBeans.setCityId(jobj.getString("city_id"));
                                    transactionBeans.setRouteId(jobj.getString("route_id"));
                                    transactionBeans.setLatitude(jobj.getString("poi_latitude"));
                                    transactionBeans.setLongitude(jobj.getString("poi_longitude"));
                                    transactionBeans.setImageUrl(jobj.getString("poi_image"));
                                    //transactionBeans.setImageTwo(cursor.getBlob(10));
                                   // transactionBeans.setStatus(cursor.getString(11));
                                    arraylist.add(transactionBeans);
                                }
                            transactionAdapter = new TransactionAdapter(getActivity(), arraylist);
                            list_view.setAdapter(transactionAdapter);
                            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                    MainActivity.getMainScreenActivity().replaceFragmentWithBackStack(getActivity(),
                                            new DcDetailsFragment(arraylist.get(position)), TAG, MainActivity.tabHome);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        new ShowAlertInformation(getActivity()).showDialog("Error", "Failed to update.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new ShowAlertInformation(getActivity()).showDialog("Error", "Failed to update.");
                }
                System.out.println("AddCityAndRouteAsync result is : " + (result == null ? "" : result));

            } else {
                Log.i("AddCityAndRouteAsync", result == null ? "" : result);
                new ShowAlertInformation(getActivity()).showDialog("Error", "Error");
            }
            progressDialog.dismiss();
        }
    }
}
