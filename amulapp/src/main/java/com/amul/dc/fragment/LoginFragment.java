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
import com.amul.dc.helper.ShowAlertInformation;
import com.amul.dc.main.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginFragment extends Fragment {

    private Button btn_login;
    private EditText edt_user_id, edt_pwd;
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
        edt_user_id = (EditText) view.findViewById(R.id.edt_email);
        edt_pwd = (EditText) view.findViewById(R.id.edt_pwd);
        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = edt_user_id.getText().toString().trim();
                String pwd = edt_pwd.getText().toString().trim();
                edt_user_id.setError(null);
                edt_pwd.setError(null);
                if (email.equals("")) {

                    edt_user_id.setError("Please enter user id");
                } else if (pwd.equals("")) {

                    edt_pwd.setError("Please enter password");
                } else {
                    if (MainActivity.getNetworkHelper().isOnline()) {
                        HashMap<String, String> postDataParams = new HashMap<String, String>();
                        postDataParams.put("email", edt_user_id.getText().toString().trim());
                        postDataParams.put("password", edt_pwd.getText().toString().trim());
                        // new LoginTask(postDataParams).execute(Commons.LOGIN_URL);
                        MainActivity.getMainScreenActivity().setSharPreferancename("", "",
                                edt_user_id.getText().toString().trim(), true);
                        MainActivity.getMainScreenActivity().onClick(MainActivity.getMainScreenActivity().tv_home);
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

            if (201 == responseCode) {
                try {
                    JSONObject jo = new JSONObject(result);
                    String status = jo.getString("status");
                    String message = jo.getString("message");
                    if (status.equals("1")) {
                        String User_ID = jo.getString("user_id");
                        String act_user_id = jo.getString("act_user_id");
                        MainActivity.getMainScreenActivity().setSharPreferancename(act_user_id, User_ID,
                                edt_user_id.getText().toString().trim(), true);
                        MainActivity.getMainScreenActivity().onClick(MainActivity.getMainScreenActivity().tv_home);

                    } else {
                        progressDialog.dismiss();
                        new ShowAlertInformation(getActivity()).showDialog("Error", message);
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
