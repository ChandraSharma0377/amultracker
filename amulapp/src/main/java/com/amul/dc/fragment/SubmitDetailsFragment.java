package com.amul.dc.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.amul.dc.R;
import com.amul.dc.adapters.SubmitDetailsAdapter;
import com.amul.dc.asynctask.UploadMultipartAsync;
import com.amul.dc.db.DataHelperClass;
import com.amul.dc.helper.Commons;
import com.amul.dc.helper.ShowAlertInformation;
import com.amul.dc.main.MainActivity;
import com.amul.dc.pojos.ResponseDto;
import com.amul.dc.pojos.TransactionBeans;

import org.json.JSONObject;

import java.util.ArrayList;

public class SubmitDetailsFragment extends Fragment implements View.OnClickListener {
    private ListView listview;
    private SubmitDetailsAdapter adapter;
    private Button btn_submit, btn_delete;
    private ArrayList<TransactionBeans> dummy = new ArrayList<TransactionBeans>();
    private ProgressDialog progressDialog;
    private CheckBox cb;
    private final String TAG = SubmitDetailsFragment.class.getSimpleName();

    //private ViewGroup  footer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

        View view = inflater.inflate(R.layout.frag_submit_dc_details, container, false);
        listview = (ListView) view.findViewById(R.id.listview);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        btn_delete = (Button) view.findViewById(R.id.btn_delete);
        cb = (CheckBox) view.findViewById(R.id.cb_submit);
        //footer = (ViewGroup) inflater.inflate(R.layout.empty_list, listview, false);
        cb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    for (int i = 0; i < dummy.size(); i++) {
                        dummy.get(i).setIsselect(true);
                    }
                } else {
                    for (int i = 0; i < dummy.size(); i++) {
                        dummy.get(i).setIsselect(false);
                    }
                }
                adapter = new SubmitDetailsAdapter(getActivity(), dummy);
                listview.setAdapter(adapter);

            }
        });

        btn_submit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        setListData();
        MainActivity.getMainScreenActivity().actionBarTitle.setText("Submit DC's Details");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_submit:
                if (dummy.size() > 0) {
                    uploadData();
                } else {
                    ShowAlertInformation.showDialog(getActivity(), "Error", "No data to upload.");

                }

                break;
            case R.id.btn_delete:
                if (dummy.size() > 0) {
                    deleteData();
                } else {
                    ShowAlertInformation.showDialog(getActivity(), "Error", "No data to delete.");
                }
                break;

            default:
                break;
        }

    }

    private void uploadData() {
        ArrayList<TransactionBeans> temp = new ArrayList<>();

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getData().get(i).isselect()) {
                temp.add(adapter.getData().get(i));
            }
        }
        if (temp.size() == 0) {
            ShowAlertInformation.showDialog(getActivity(), "Error", "Please select data to upload.");
        } else {
            if (MainActivity.getNetworkHelper().isOnline()) {
                new UploadTask(temp).execute(Commons.ADD_DC_DETAILS);
                Toast.makeText(getActivity(), "Uploading inProgress...", Toast.LENGTH_LONG).show();
            } else {
                ShowAlertInformation.showNetworkDialog(getActivity());
            }
        }

    }

    private void deleteData() {
        final ArrayList<TransactionBeans> temp = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getData().get(i).isselect()) {
                temp.add(adapter.getData().get(i));
            }
        }
        if (temp.size() == 0) {
            ShowAlertInformation.showDialog(getActivity(), "Error", "Please select data to delete.");
        } else {

            new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.delete_title)).setMessage(getString(R.string.delete_msg))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < temp.size(); i++) {
                                DataHelperClass DHC = new DataHelperClass(getActivity());
                                DHC.deleteRecord(temp.get(i).getUniqueId());
                            }
                            setListData();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", null).show();
        }
    }

    private class UploadTask extends UploadMultipartAsync {

        public UploadTask(ArrayList<TransactionBeans> postDataParams) {
            super(postDataParams);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "", "uploading please wait...");
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(cancelListener);
        }

        @Override
        protected ArrayList<ResponseDto> doInBackground(String... params) {
            return super.doInBackground(params);
        }

        @Override
        protected void onPostExecute(ArrayList<ResponseDto> result) {
            super.onPostExecute(result);
            String output = "";
            int successcount = 0;
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i++) {
                    try {
                        ResponseDto rdo = result.get(i);
                        JSONObject jo = new JSONObject(rdo.getStatus());
                        String status = jo.getString("status");
                        String message = jo.getString("msg");
                        if (status.equals("SUCCESS")) {
                            ++successcount;
                            DataHelperClass DHC = new DataHelperClass(getActivity());
                            DHC.deleteRecord(rdo.getUniqueId());
                        } else {

                        }
                        output += "\n" + message;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            String msg = "";
            if (successcount == result.size())
                msg = "All Dc's submitted!";
            if (successcount < result.size())
                msg = successcount + " Dc's out of " + result.size() + " submitted!";
            ShowAlertInformation.showDialog(getActivity(), "Submit Dc's", msg);
            setListData();
            cb.setChecked(false);
            progressDialog.dismiss();
        }

        OnCancelListener cancelListener = new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                // if (null != lat) {
                // lat.cancel(true);
                // System.out.println("refe" + lat.isCancelled());
                // lat = null;
                // // activity.getSupportFragmentManager().popBackStack();
                // }
            }
        };
    }

    private void setListData() {
        DataHelperClass DHC = new DataHelperClass(getActivity());
        if (!DHC.isRecordExist()) {
            Toast.makeText(getActivity(), "No data to display", Toast.LENGTH_LONG).show();
            dummy.clear();
        } else {
            dummy = DHC.getDcDetailsData();
        }
        adapter = new SubmitDetailsAdapter(getActivity(), dummy);
        listview.setAdapter(adapter);
//		if (dummy.size() == 0 && listview.getFooterViewsCount() == 0) {
//			//footer.setVisibility(View.VISIBLE);
//			listview.addFooterView(footer, null, false);
//		} else if (dummy.size() > 0 ) {
//			listview.removeFooterView(footer);
//		}
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MainActivity.getMainScreenActivity().replaceFragmentWithBackStack(getActivity(),
                        new DcDetailsFragment(dummy.get(position)), TAG, MainActivity.tabUpload);
            }
        });
    }
}
