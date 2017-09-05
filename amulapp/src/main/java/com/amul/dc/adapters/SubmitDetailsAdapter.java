package com.amul.dc.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amul.dc.R;
import com.amul.dc.pojos.TransactionBeans;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class SubmitDetailsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<TransactionBeans> transactionBeanses;

	public SubmitDetailsAdapter(Context context, ArrayList<TransactionBeans> helpListItemDtos) {
		this.context = context;
		this.transactionBeanses = helpListItemDtos;
	}

	@Override
	public int getCount() {
		return transactionBeanses.size();
	}

	@Override
	public Object getItem(int position) {
		return transactionBeanses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public ArrayList<TransactionBeans> getData() {
		return transactionBeanses;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_submit_dcs, parent, false);
			holder = new ViewHolder();
			holder.cb_submit = (CheckBox) convertView.findViewById(R.id.cb_submit);

			// holder.tv_profit_center = (TextView) convertView
			// .findViewById(R.id.tv_profit_center);
			holder.tvheader = (TextView) convertView.findViewById(R.id.tvheader);
			holder.tvsubheader = (TextView) convertView.findViewById(R.id.tvsubheader);
			holder.ivthumb = (ImageView) convertView.findViewById(R.id.ivthumb);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.cb_submit.setChecked(transactionBeanses.get(position).isselect());
		holder.tvheader.setText(transactionBeanses.get(position).getStoreName());
		holder.tvsubheader.setText(transactionBeanses.get(position).getStoreLocation());
		if(null !=transactionBeanses.get(position).getImageOne()) {
			ByteArrayInputStream imageStream = new ByteArrayInputStream(transactionBeanses.get(position).getImageOne());
			Bitmap bitmap_one = BitmapFactory.decodeStream(imageStream);
			holder.ivthumb.setImageBitmap(bitmap_one);
		}
		else
			holder.ivthumb.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.store));
		setCheckChangeListener(holder, position);

		return convertView;
	}

	static class ViewHolder {
		CheckBox cb_submit;
		ImageView ivthumb;
		TextView tvheader;
		TextView tvsubheader;

	}

	private void setCheckChangeListener(final ViewHolder holder, final int position) {
		holder.cb_submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					holder.cb_submit.setChecked(true);
					transactionBeanses.get(position).setIsselect(true);
				} else {
					holder.cb_submit.setChecked(false);
					transactionBeanses.get(position).setIsselect(false);
				}
			}
		});
	}
}
