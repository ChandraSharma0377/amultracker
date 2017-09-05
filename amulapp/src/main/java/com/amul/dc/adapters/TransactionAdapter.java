package com.amul.dc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amul.dc.R;
import com.amul.dc.pojos.TransactionBeans;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;


public class TransactionAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TransactionBeans> arraylist;

    public TransactionAdapter(Context context, ArrayList<TransactionBeans> items) {
        this.context = context;
        arraylist =items;

    }

    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_dcs, null);
            holder = new ViewHolder();
            holder.tvheader = (TextView) convertView.findViewById(R.id.tvheader);
            holder.tvsubheader = (TextView) convertView.findViewById(R.id.tvsubheader);
           // holder.tvamount = (TextView) convertView.findViewById(R.id.tvamount);
            holder.ivthumb = (ImageView) convertView.findViewById(R.id.ivthumb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvheader.setText(arraylist.get(position).getStoreName());
        holder.tvsubheader.setText(arraylist.get(position).getStoreLocation());
        if(null !=arraylist.get(position).getImageOne()) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(arraylist.get(position).getImageOne());
            Bitmap bitmap_one = BitmapFactory.decodeStream(imageStream);
            holder.ivthumb.setImageBitmap(bitmap_one);
        }
        else
            holder.ivthumb.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.store));
        return convertView;
    }

    public static class ViewHolder {
        TextView tvheader;
        TextView tvsubheader;
        TextView tvamount;
        ImageView ivthumb;
    }

}
