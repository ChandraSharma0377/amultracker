package com.amul.dc.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amul.dc.R;
import com.amul.dc.db.DBOpenHelperClass;
import com.amul.dc.fragment.DcDetailsFragment;
import com.amul.dc.fragment.HomeFragment;
import com.amul.dc.fragment.LoginFragment;
import com.amul.dc.fragment.SubmitDetailsFragment;
import com.amul.dc.helper.GPSTracker;
import com.amul.dc.helper.NetworkHelper;
import com.amul.dc.pojos.CitiesDto;
import com.amul.dc.pojos.RoutesDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class MainActivity extends ActionBarActivity  {
    private Boolean exit = false;
    private static MainActivity mainActivity;
    private static NetworkHelper networkHelper;
    public static final String MyPREFERENCES = "AppPref";
    private static SharedPreferences sharedpreferences;
    public static String tabHome = "Home";
    public static String tabUpload = "Upload";
    // Tabs associated with list of fragments
    public Map<String, List<Fragment>> fragmentsStack = new HashMap<String, List<Fragment>>();
    protected Fragment mFrag;
    protected Fragment cFrag, rootFragment;
    private HashMap<String, Stack<Fragment>> mFragmentsStack;
    public TextView actionBarTitle;
    public ImageView iv_logout;

    private final String IS_LOGIN = "IsLoggedIn";
    private final String KEY_EMAIL = "email";
    private final String KEY_USER_NAME = "userName";
    private final String KEY_USER_ID = "userID";


    private final String KEY_CITY_ID = "cityID";
    private final String KEY_CITY_NAME = "cityName";
    private final String KEY_ROUTE_ID = "routeID";
    private final String KEY_ROUTE_NAME = "routeName";

    protected static final String CONTENT_TAG = "contenFragments";
    public DBOpenHelperClass dbHandler;
    private int GPS_REQUEST = 55;
    public GPSTracker gps;
    private long lastPress;
    private Toast backPressToast;
    private String currentSelectedTabTag = "";
    private BottomNavigationView bottomNavigationView;

    public static MainActivity getMainScreenActivity() {
        return mainActivity;
    }

    public static NetworkHelper getNetworkHelper() {
        return networkHelper;
    }

    public String getCurrentSelectedTabTag() {
        return currentSelectedTabTag;
    }

    // Used in TabListener to keep currentSelectedTabTag actual.
    public void setCurrentSelectedTabTag(String currentSelectedTabTag) {
        this.currentSelectedTabTag = currentSelectedTabTag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        networkHelper = new NetworkHelper(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mFragmentsStack = new HashMap<String, Stack<Fragment>>();
        mFragmentsStack.put(CONTENT_TAG, new Stack<Fragment>());

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        ActionBarSetup();
        dbHandler = DBOpenHelperClass.getSharedObject(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                setCurrentSelectedTabTag(tabHome);
                                if (null == fragmentsStack.get(tabHome)) {
                                    fragment = new HomeFragment();
                                    createStackForTab(tabHome);
                                    addFragmentToStack(fragment);
                                    replaceFragmentWithoutBackStack(R.id.main_fragment, fragment);
                                } else {
                                    replaceFragmentWithoutBackStack(R.id.main_fragment, getLastFragment());
                                }
                                break;
                            case R.id.action_upload:
                                setCurrentSelectedTabTag(tabUpload);
                                if (null == fragmentsStack.get(tabUpload)) {
                                    fragment = new SubmitDetailsFragment();
                                    createStackForTab(tabUpload);
                                    addFragmentToStack(fragment);
                                    replaceFragmentWithoutBackStack(R.id.main_fragment, fragment);
                                } else {
                                    replaceFragmentWithoutBackStack(R.id.main_fragment, getLastFragment());
                                }
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
        if (isLoggedIn()) {
            //changeNavigationContentFragment(new HomeFragment(), false);
            selectTabs(true);
        } else
            changeNavigationContentFragment(new LoginFragment(), false);
        onNewIntent(getIntent());

    }


    public void showHideBottomLay(boolean isVisible) {
        if (isVisible) {
           // lay_bottom.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            iv_logout.setVisibility(View.VISIBLE);
        } else {
           // lay_bottom.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
            iv_logout.setVisibility(View.GONE);
        }
    }

    public void selectTabs(boolean isHomeSelected) {
//        if (isHomeSelected)
//            bottomNavigationView.setSelectedItemId(R.id.action_home);
//        else
//            bottomNavigationView.setSelectedItemId(R.id.action_upload);
        View view = bottomNavigationView.findViewById(R.id.action_home);
        view.performClick();
    }
    private void ActionBarSetup() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_view);
        getSupportActionBar().setElevation(0);
        View v = getSupportActionBar().getCustomView();

        actionBarTitle = (TextView) v.findViewById(R.id.title);
        iv_logout = (ImageView) v.findViewById(R.id.iv_logout);
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mainActivity).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.getSharePreferance().edit().clear().commit();
                                MainActivity.getMainScreenActivity().restartActivity();
                            }
                        }).setNegativeButton("No", null).show();
            }
        });
        Toolbar parent = (Toolbar) v.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        // actionBarTitle.setText(getString(R.string.app_name));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Fragment f = getLastFragment();
        if (f instanceof DcDetailsFragment) {
            f.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void changeNavigationContentFragment(Fragment frgm, boolean shouldAdd) {

        if (shouldAdd) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (null != fm.findFragmentById(R.id.main_fragment))
                ft.hide(fm.findFragmentById(R.id.main_fragment));
            ft.add(R.id.main_fragment, frgm, frgm.getClass().getSimpleName());
            ft.addToBackStack(null);
            // ft.commitAllowingStateLoss();
            ft.commit();
            mFragmentsStack.get(CONTENT_TAG).push(frgm);
        } else {
            mFragmentsStack.get(CONTENT_TAG).clear();
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            manager.beginTransaction().replace(R.id.main_fragment, frgm).commitAllowingStateLoss();
            rootFragment = frgm;
        }

        cFrag = frgm;

    }


    public static SharedPreferences getSharePreferance() {
        return sharedpreferences;
    }

    public String getActiveUserID() {
        return sharedpreferences.getString(KEY_USER_NAME, "");
    }

    public CitiesDto getPrefCityDto(){
        CitiesDto citiesDto = null;
        String cityID = sharedpreferences.getString(KEY_CITY_ID, "");
        if(!cityID.equals("")){
            citiesDto = new CitiesDto();
            citiesDto.setCityId(cityID);
            citiesDto.setCityName(sharedpreferences.getString(KEY_CITY_NAME, ""));
        }
        return citiesDto;
    }
    public RoutesDto getPrefRouteDto(){
        RoutesDto routesDto = null;
        String routeID = sharedpreferences.getString(KEY_ROUTE_ID, "");
        if(!routeID.equals("")){
            routesDto = new RoutesDto();
            routesDto.setRouteId(routeID);
            routesDto.setRouteName(sharedpreferences.getString(KEY_ROUTE_NAME, ""));
        }
        return routesDto;
    }

    public void setPrefRouteDto(RoutesDto routesDto){
        Editor editor = sharedpreferences.edit();
        if(null != routesDto) {
            editor.putString(KEY_ROUTE_ID, routesDto.getRouteId());
            editor.putString(KEY_ROUTE_NAME, routesDto.getRouteName());
        }else{
            editor.putString(KEY_ROUTE_ID, "");
            editor.putString(KEY_ROUTE_NAME, "");
        }
        editor.commit();
    }

    public void setPrefCityDto(CitiesDto cityDto){
        Editor editor = sharedpreferences.edit();
        editor.putString(KEY_CITY_ID, cityDto.getCityId());
        editor.putString(KEY_CITY_NAME, cityDto.getCityName());
        editor.commit();
    }

    public String getUserID() {
        return sharedpreferences.getString(KEY_USER_ID, "");
    }

    public boolean isLoggedIn() {
        return sharedpreferences.getBoolean(IS_LOGIN, false);
    }

    public void setSharPreferancename(String user_name, String userID, String mobileNo, boolean isLogin) {
        Editor editor = sharedpreferences.edit();
        editor.putString(KEY_USER_NAME, user_name);
        editor.putString(KEY_USER_ID, userID);
        editor.putString(KEY_EMAIL, mobileNo);
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.commit();
    }


    public void replaceFragmentWithBackStack(FragmentActivity activity,  Fragment fragment, String tag, String selectedTab) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.main_fragment, fragment, tag).addToBackStack(tag).commitAllowingStateLoss();
        // Added to maintaining the stack
        setCurrentSelectedTabTag(selectedTab);
        addFragmentToStack(fragment);

    }

    public void restartActivity() {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getLocation() {
        try {
            // create class object
            gps = new GPSTracker(MainActivity.this);

            // check if GPS enabled
            if (gps.canGetLocation()) {

//				latitude = gps.getLatitude();
//				longitude = gps.getLongitude();

                // \n is for new line
                // Toast.makeText(getApplicationContext(),
                // "Your Location is - \nLat: " + latitude + "\nLong: " +
                // longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_REQUEST);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST) {
            try {
                if (null != gps) {
                    gps.getLocation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void replaceFragmentWithoutBackStack(int container, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(container, fragment).commit();
    }

    /**
     * Method for adding list of fragment for tab to our Back Stack
     *
     * @param tabTag The identifier tag for the tab
     */
    public void createStackForTab(String tabTag) {
        List<Fragment> tabFragments = new ArrayList<Fragment>();
        fragmentsStack.put(tabTag, tabFragments);
    }

    /**
     * @param fragment The fragment that will be added to the Back Stack
     */
    public void addFragmentToStack(Fragment fragment) {
        fragmentsStack.get(currentSelectedTabTag).add(fragment);
    }

    /**
     * Used in TabListener for showing last opened screen from selected tab
     *
     * @return The last added fragment of actual tab will be returned
     */
    public Fragment getLastFragment() {
        List<Fragment> fragments = fragmentsStack.get(currentSelectedTabTag);
        return fragments.get(fragments.size() - 1);
    }

    /**
     * Override default behavior of hardware Back button
     * for navigation thru fragments on tab hierarchy
     */
    @Override
    public void onBackPressed() {
        List<Fragment> currentTabFragments = fragmentsStack.get(currentSelectedTabTag);

        if (null != currentTabFragments && currentTabFragments.size() > 1) {

            int size = currentTabFragments.size();

            // if it is not first screen then
            // current screen is closed and removed from Back Stack and shown the previous one
            Fragment fragment = currentTabFragments.get(size - 2);
            currentTabFragments.remove(size - 1);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment, fragment);
            fragmentTransaction.commit();
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 3000) {
                backPressToast = Toast.makeText(mainActivity, getString(R.string.exit_app), Toast.LENGTH_LONG);
                backPressToast.show();
                lastPress = currentTime;
            } else {
                if (backPressToast != null) {
                    backPressToast.cancel();
                }
               // super.onBackPressed();
                finish();
            }
        }
    }

    public boolean checkLocationPermissionAllowed() {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                   123 );
            return  false;
        }
        return  true;
    }
}
