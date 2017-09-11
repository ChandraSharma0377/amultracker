package com.amul.dc.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.amul.dc.R;

public class ShowAlertInformation {




	public static void showNetworkDialog(Context context) {
		showDialog(context, context.getString(R.string.networkerror), context.getString(R.string.offline));
	}
	public static void showDialog(Context context ,String title, String message) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle(title);
		alertDialog.setCancelable(false);
		alertDialog.setMessage(message);
		alertDialog.setIcon(R.drawable.launcher);
		alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});

		alertDialog.show();
	}

	public static void showDialog(Context context ,String Title, String Message, OnClickListener positiveListner) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle(Title);
		alertDialog.setMessage(Message);
		alertDialog.setCancelable(false);
		alertDialog.setIcon(R.drawable.launcher);
		alertDialog.setNegativeButton("OK", positiveListner);
		alertDialog.show();
	}

}
