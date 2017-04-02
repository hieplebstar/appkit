package com.bstar.powerdata.views.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bstar.powerdata.R;
import com.bstar.powerdata.models.CountryCallingCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountrySpinnerAdapter extends ArrayAdapter<CountryCallingCode> {

    private List<CountryCallingCode> mItems;
    private Context context;

    public CountrySpinnerAdapter(Context context, int resourceId,
                                 List<CountryCallingCode> items) {
        super(context, resourceId, items);
        this.mItems = items;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        CountryCallingCode item = mItems.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.country_spinner_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(row);
        viewHolder.mNameTextView.setText(item.getName());
        viewHolder.mCallingCodeTextView.setText(item.getCallingCode());
        try {
            InputStream inputStream = context.getAssets().open("flags/" + item.getIcon());
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            viewHolder.mFlagImageView.setImageDrawable(drawable);
        } catch (IOException ex) {
        }

        return row;
    }

    static class ViewHolder {
        @BindView(R.id.imageview_country_list_item_flag)
        ImageView mFlagImageView;
        @BindView(R.id.textview_country_list_item_calling_code)
        TextView mCallingCodeTextView;
        @BindView(R.id.textview_country_list_item_name)
        TextView mNameTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
