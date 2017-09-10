package com.amul.dc.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.amul.dc.R;
import com.amul.dc.asynctask.AsyncProcess;
import com.amul.dc.helper.Commons;
import com.amul.dc.helper.ShowAlertInformation;
import com.amul.dc.main.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginFragment extends Fragment {

    private Button btn_login;
    private EditText edt_email, edt_pwd;
    private LoginTask lat;
    private ProgressDialog progressDialog;

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.lay_login, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        edt_email = (EditText) view.findViewById(R.id.edt_email);
        edt_pwd = (EditText) view.findViewById(R.id.edt_pwd);
        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = edt_email.getText().toString().trim();
                String pwd = edt_pwd.getText().toString().trim();
                edt_email.setError(null);
                edt_pwd.setError(null);
                if (email.equals("")) {
                    edt_email.setError("Please enter email id");
                } else if (pwd.equals("")) {
                    edt_pwd.setError("Please enter password");
                }else if (!email.equals("") && !isValidEmail(email)) {
                    edt_email.setError("Please enter valid email id");
                } else {
                    if (MainActivity.getNetworkHelper().isOnline()) {
                        HashMap<String, String> postDataParams = new HashMap<String, String>();
                        postDataParams.put("email", edt_email.getText().toString().trim());
                        postDataParams.put("password", edt_pwd.getText().toString().trim());
                        new LoginTask(postDataParams).execute(Commons.LOGIN_URL);
                    } else {
                        new ShowAlertInformation(getActivity()).showDialog("Network error", getString(R.string.offline));
                    }
                }

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getMainScreenActivity().actionBarTitle.setText("Login");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    private class LoginTask extends AsyncProcess {

        public LoginTask(HashMap<String, String> postDataParams) {
            super(postDataParams);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "", "login please wait...");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(cancelListener);
        }

        @Override
        protected String doInBackground(String... params) {
            return super.doInBackground(params);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (200 == responseCode) {
                try {
                    JSONObject job = new JSONObject(result);
                    String status = job.getString("status");
                    if (status.equalsIgnoreCase("SUCCESS")) {
//                        {
//                            "status": "SUCCESS",
//                                "userData": {
//                            "id": 20,
//                                    "email": "rajsharma0377@gmail.com",
//                                    "first_name": "Chandra",
//                                    "last_name": "Sharma",
//                                    "role": "App",
//                                    "phone": 9766146936,
//                                    "permissions": [],
//                            "activated": true,
//                                    "activated_at": null,
//                                    "last_login": "2017-09-07 11:45:22",
//                                    "created_by": 1,
//                                    "updated_by": 1,
//                                    "updated_at": "2017-09-07 11:45:22",
//                                    "created_at": "2017-09-07 11:38:08"
//                        }
//                        }
                        JSONObject jo = job.getJSONObject("userData");
                        String User_ID = jo.getString("id");
                        String first_name = jo.getString("first_name");
                        String last_name = jo.getString("last_name");
                        MainActivity.getMainScreenActivity().setSharPreferancename(first_name+" "+last_name, User_ID,
                                edt_email.getText().toString().trim(), true);
                        MainActivity.getMainScreenActivity().selectTabs(true);
                    } else {
                        progressDialog.dismiss();
                        new ShowAlertInformation(getActivity()).showDialog("Error", "Login error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new ShowAlertInformation(getActivity()).showDialog("Error", "No data found");
                    progressDialog.dismiss();
                }
                System.out.println("LoginTask result is : " + (result == null ? "" : result));
                progressDialog.dismiss();
            } else {
                Log.i("LoginTask response", result == null ? "" : result);
                new ShowAlertInformation(getActivity()).showDialog("Error", "Error");
                progressDialog.dismiss();
            }

        }

        OnCancelListener cancelListener = new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                if (null != lat) {
                    lat.cancel(true);
                    System.out.println("refe" + lat.isCancelled());
                    lat = null;
                    // activity.getSupportFragmentManager().popBackStack();
                }
            }
        };
    }

}
