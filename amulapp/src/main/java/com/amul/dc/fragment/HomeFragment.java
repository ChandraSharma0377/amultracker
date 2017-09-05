
package com.amul.dc.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amul.dc.R;
import com.amul.dc.adapters.SpinAdapter;
import com.amul.dc.adapters.TransactionAdapter;
import com.amul.dc.db.DataHelperClass;
import com.amul.dc.main.MainActivity;
import com.amul.dc.pojos.TransactionBeans;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements View.OnClickListener {
	private final String TAG = HomeFragment.class.getSimpleName();
	private Spinner sp_cities, sp_route;
	private ArrayList<String> routeList = new ArrayList<String>();
	private ArrayList<String> citiesList = new ArrayList<String>();
	private ListView list_view;
	private ImageView iv_add_dc;
	private TransactionAdapter transactionAdapter;
	private ArrayList<TransactionBeans> arraylist = new ArrayList<TransactionBeans>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

		View view = inflater.inflate(R.layout.lay_landing, container, false);
		list_view = (ListView) view.findViewById(R.id.list_view);
		sp_cities = (Spinner) view.findViewById(R.id.sp_cities);
		sp_route = (Spinner) view.findViewById(R.id.sp_route);
		iv_add_dc = (ImageView) view.findViewById(R.id.iv_add_dc);
		sp_route.setEnabled(false);
		iv_add_dc.setOnClickListener(this);
		initSpinners();
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

				}else{
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
		DataHelperClass DHC = new DataHelperClass(getActivity());
		if (!DHC.isRecordExist()) {
			Toast.makeText(getActivity(), "No data to display", Toast.LENGTH_LONG).show();
			arraylist.clear();
		} else {
			arraylist = DHC.getDcDetailsData();
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

	}
	@Override
	public void onResume() {

		super.onResume();
		MainActivity.getMainScreenActivity().actionBarTitle.setText("AMUL TRACKER");
        if(MainActivity.getMainScreenActivity().checkLocationPermissionAllowed())
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
			MainActivity.getMainScreenActivity().replaceFragmentWithBackStack(getActivity(),
					new DcDetailsFragment(), TAG, MainActivity.tabHome);
			break;
		default:
			break;
		}

	}


}
