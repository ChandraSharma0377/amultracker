package com.amul.dc.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amul.dc.R;
import com.amul.dc.asynctask.AsyncProcess;
import com.amul.dc.asynctask.UploadMultipartAsync;
import com.amul.dc.db.DataHelperClass;
import com.amul.dc.helper.Commons;
import com.amul.dc.helper.ShowAlertInformation;
import com.amul.dc.main.MainActivity;
import com.amul.dc.pojos.ResponseDto;
import com.amul.dc.pojos.TransactionBeans;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class DcDetailsFragment extends Fragment {

    private TextView tv_store_location, tv_scan_datetime,
            tv_gps_coordinate;
    private View view;
    private Button btn_add, btn_save;
    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private final int CAMERA_AND_STORAGE_PERMISSION_CODE = 102;
    private TransactionBeans transactionBeansB;
    private Bitmap bitmap_one, bitmap_two;
    private ImageView iv_one, iv_two;
    private EditText edt_store_name;

    private final String[] CAMERA_AND_STORAGE_PERMISSION_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public DcDetailsFragment() {

    }

    public DcDetailsFragment(TransactionBeans transactionBeans) {
        this.transactionBeansB = transactionBeans;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_dc_details, container, false);
        //btn_retake = (Button) view.findViewById(R.id.btn_retake);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_save = (Button) view.findViewById(R.id.btn_save);

        tv_store_location = (TextView) view.findViewById(R.id.tv_store_location);
        tv_scan_datetime = (TextView) view.findViewById(R.id.tv_scan_datetime);
        tv_gps_coordinate = (TextView) view.findViewById(R.id.tv_gps_coordinate);
        edt_store_name = (EditText) view.findViewById(R.id.edt_store_name);
        iv_one = (ImageView) view.findViewById(R.id.iv_one);
        //iv_two = (ImageView) view.findViewById(R.id.iv_two);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (null == bitmap_one) {
                bitmap_one = photo;
                iv_one.setImageBitmap(photo);

            } else {
//				bitmap_two = photo;
//				iv_two.setImageBitmap(photo);
            }

        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity(), "User cancelled image capture", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.getMainScreenActivity().actionBarTitle.setText("DC's Details");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        if (null != transactionBeansB) {
            tv_store_location.setText(transactionBeansB.getStoreLocation());
            tv_scan_datetime.setText(transactionBeansB.getScandatetime());
            tv_gps_coordinate.setText(transactionBeansB.getGpscoordinate());
            edt_store_name.setText(transactionBeansB.getStoreName());
            edt_store_name.setEnabled(false);
            if (transactionBeansB.getImageOne() != null) {
                ByteArrayInputStream imageStream = new ByteArrayInputStream(transactionBeansB.getImageOne());
                bitmap_one = BitmapFactory.decodeStream(imageStream);
                iv_one.setImageBitmap(bitmap_one);
            }
            if (transactionBeansB.getImageTwo() != null) {
                ByteArrayInputStream imageStream1 = new ByteArrayInputStream(transactionBeansB.getImageTwo());
                bitmap_two = BitmapFactory.decodeStream(imageStream1);
                iv_two.setImageBitmap(bitmap_two);
            }
            if (transactionBeansB.getImageOne() == null &&
                    !transactionBeansB.getImageUrl().equals("")
                    ) {
                Picasso.with(getActivity()).load(transactionBeansB.getImageUrl()).placeholder(R.drawable.placeholder).
                        into(iv_one);
            }
            btn_add.setText("Edit Details");
            btn_save.setText("Delete Details");
            //btn_retake.setText("Back");
        } else {
            iv_one.setImageResource(R.drawable.camera);
            //	iv_two.setImageResource(R.drawable.camera);
            iv_one.setOnClickListener(myListener);
            //iv_two.setOnClickListener(myListener);
            setData("");
        }
        //btn_retake.setOnClickListener(myListener);
        btn_add.setOnClickListener(myListener);
        btn_save.setOnClickListener(myListener);
    }

    View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_add:
                    if (btn_add.getText().toString().equalsIgnoreCase("Edit Details")) {
                        btn_add.setText("Add Photo");
                        btn_save.setText("Save");
                        iv_one.setOnClickListener(myListener);
                        //iv_two.setOnClickListener(myListener);
                        edt_store_name.setEnabled(true);

                    } else {
                        if (null != bitmap_one & null != bitmap_two)
                            ShowAlertInformation.showDialog(getActivity(),"Error", "Please delete some photo to add new photo.");
                        else {

                            if (isCameraAndStoragePermissionAllowed()) {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                            } else {
                                requestCameraAndStoragePermission();
                            }

                        }
                    }

                    break;
                case R.id.btn_save:

                    if (btn_save.getText().toString().equalsIgnoreCase("Delete Details")) {
                        showDialogDelete();
                    } else {
                        if (edt_store_name.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "Please enter store name", Toast.LENGTH_LONG).show();
                        } else {

                            TransactionBeans transactionBeans = new TransactionBeans();
                            transactionBeans.setStoreName(edt_store_name.getText().toString().trim());
                            transactionBeans.setStoreLocation(tv_store_location.getText().toString().trim());
                            transactionBeans.setScandatetime(tv_scan_datetime.getText().toString().trim());
                            transactionBeans.setGpscoordinate(tv_gps_coordinate.getText().toString().trim());
                            transactionBeans.setCityId(HomeFragment.citiesDto.getCityId());
                            transactionBeans.setRouteId(HomeFragment.routesDto.getRouteId());
                            transactionBeans.setLatitude("" + MainActivity.getMainScreenActivity().gps.getLatitude());
                            transactionBeans.setLongitude("" + MainActivity.getMainScreenActivity().gps.getLongitude());
                            transactionBeans.setImageOne(getByte(bitmap_one));
                            //scanItemDto.setImageTwo(getByte(bitmap_two));
                            if (null != transactionBeansB)
                                transactionBeans.setUniqueId(transactionBeansB.getUniqueId());

                            new DataHelperClass(getActivity()).addDcDetails(transactionBeans);
                            ArrayList<TransactionBeans> temp = new ArrayList<>();
                            temp.add(transactionBeans);

                            if (MainActivity.getNetworkHelper().isOnline()) {
                                new UploadTask(temp).execute(Commons.ADD_DC_DETAILS);
                            } else {
                                ShowAlertInformation.showNetworkDialog(getActivity());
                                     }
//						DialogInterface.OnClickListener doc = new OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								MainActivity.getMainScreenActivity().onBackPressed();
//								dialog.dismiss();
//							}
//						};

//						if (null != transactionBeansB) {
//							new ShowAlertInformation(getActivity()).showDialog(
//									getActivity().getString(R.string.scan_update_title),
//									getActivity().getString(R.string.scan_update_msg), doc);
//
//						} else {
//							new ShowAlertInformation(getActivity()).showDialog(
//									getActivity().getString(R.string.scan_save_title),
//									getActivity().getString(R.string.scan_save_msg), doc);
//						}
                        }
                    }
                    break;
                case R.id.iv_one:
                    if (null != bitmap_one)
                        showDialog(iv_one);
                    break;
//			case R.id.iv_two:
//				if (null != bitmap_two)
//					showDialog(iv_two);
//				break;

                default:
                    break;
            }

        }
    };

    private byte[] getByte(Bitmap photo) {
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm a");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void showDialog(final ImageView view) {
        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete Image")
                .setCancelable(false).setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (view.getId() == R.id.iv_one) {
                            bitmap_one = null;
                        } else {
                            bitmap_two = null;
                        }
                        view.setImageResource(R.drawable.camera);
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", null).show();
    }

    public void showDialogDelete() {
        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setCancelable(false)
                .setTitle(getString(R.string.delete_title)).setMessage(getString(R.string.delete_msg))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataHelperClass DHC = new DataHelperClass(getActivity());
                        if (DHC.deleteRecord(transactionBeansB.getUniqueId())) {
                            MainActivity.getMainScreenActivity().onBackPressed();
                        } else {
                            if (MainActivity.getNetworkHelper().isOnline()) {
                                HashMap<String, String> postDataParams = new HashMap<String, String>();
                                postDataParams.put("poi_id", "" + transactionBeansB.getUniqueId());
                                postDataParams.put("user_id", MainActivity.getMainScreenActivity().getUserID());
                                new DeleteDcsDetailAsync(postDataParams).execute(Commons.DELETE_ROUTE_DC_DETAILS);
                            } else {
                                 ShowAlertInformation.showNetworkDialog(getActivity());
                            }
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", null).show();
    }

    private void setData(String data) {
        tv_scan_datetime.setText(getDateTime());
        if (null != MainActivity.getMainScreenActivity().gps) {
            tv_gps_coordinate.setText(MainActivity.getMainScreenActivity().gps.getLatitude() + ","
                    + MainActivity.getMainScreenActivity().gps.getLongitude());
            tv_store_location.setText(getCompleteAddressString(MainActivity.getMainScreenActivity().gps.getLatitude(),
                    MainActivity.getMainScreenActivity().gps.getLongitude()));
        } else {
            tv_gps_coordinate.setText("0.0,0.0");
            tv_store_location.setText("");
        }
    }

    private boolean isExternalStoragePermissionAllowed() {
        //Getting the permission status
        int result_read = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result_write = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result_read == PackageManager.PERMISSION_GRANTED && result_write == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    private boolean isCameraPermissionAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    private boolean isCameraAndStoragePermissionAllowed() {
        return (isCameraPermissionAllowed() && isExternalStoragePermissionAllowed());
    }

    private void requestCameraAndStoragePermission() {
        ActivityCompat.requestPermissions(
                getActivity(),
                CAMERA_AND_STORAGE_PERMISSION_PERMS,
                CAMERA_AND_STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_AND_STORAGE_PERMISSION_CODE) {
            //both remaining permission allowed
            if (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                //scanBarcode();
            } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//one remaining permission allowed
                //scanBarcode();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //No permission allowed
                //Do nothing
            }
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction add", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction add", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction add", "Canont get Address!");
        }
        return strAdd;
    }

    private class UploadTask extends UploadMultipartAsync {
        private ProgressDialog progressDialog;

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
            if (successcount == result.size()) {
                //	msg="Dc's submitted!";
                //if(successcount <result.size())
                //	msg="";
                //new ShowAlertInformation(getActivity()).showDialog("Submit Dc's", msg);
                DialogInterface.OnClickListener doc = new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.getMainScreenActivity().onBackPressed();
                        dialog.dismiss();
                    }
                };

                if (null != transactionBeansB) {
                    ShowAlertInformation.showDialog(getActivity(),
                            getActivity().getString(R.string.scan_update_title),
                            getActivity().getString(R.string.scan_update_msg), doc);

                } else {
                    ShowAlertInformation.showDialog(getActivity(),
                            getActivity().getString(R.string.scan_save_title),
                            getActivity().getString(R.string.scan_save_msg), doc);
                }
            }
            progressDialog.dismiss();
        }

        DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
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

    private class DeleteDcsDetailAsync extends AsyncProcess {
        private ProgressDialog progressDialog;


        public DeleteDcsDetailAsync(HashMap<String, String> postDataParams) {
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
                        MainActivity.getMainScreenActivity().onBackPressed();
                        ShowAlertInformation.showDialog(getActivity(),"Success", "DC's deleted successfully.");
                    } else {
                        ShowAlertInformation.showDialog(getActivity(),"Error", "Failed to update.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ShowAlertInformation.showDialog(getActivity(),"Error", "Failed to update.");
                }
                System.out.println("AddCityAndRouteAsync result is : " + (result == null ? "" : result));

            } else {
                Log.i("AddCityAndRouteAsync", result == null ? "" : result);
                ShowAlertInformation.showDialog(getActivity(),"Error", "Error");
            }
            progressDialog.dismiss();
        }
    }

}
