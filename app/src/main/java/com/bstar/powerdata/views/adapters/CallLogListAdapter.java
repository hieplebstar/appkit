package com.bstar.powerdata.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.models.CallLog;
import com.bstar.powerdata.utils.CalendarUtils;
import com.bstar.powerdata.utils.PhoneUtils;
import com.bstar.powerdata.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CallLogListAdapter extends RecyclerView.Adapter<CallLogListAdapter.ViewHolder> {

    public List<CallLog> mCallLogList;
    private Context mContext;

    public CallLogListAdapter(Context context) {
        mCallLogList = new ArrayList<>();
        mContext = context;
    }

    public void setItems(List<CallLog> list) {
        if(list == null) return;
        this.mCallLogList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_call_log_item_name)
        TextView mNameTextView;
        @BindView(R.id.textview_call_log_item_number)
        TextView mNumberTextView;
        @BindView(R.id.textview_call_log_item_avatar)
        TextView mAvatarTextView;
        @BindView(R.id.textview_call_log_item_time)
        TextView mTimeTextView;
        @BindView(R.id.textview_call_log_item_duration)
        TextView mDurationTextView;
        @BindView(R.id.textview_call_log_item_status)
        TextView mStatusTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CallLog callLog = mCallLogList.get(position);
        String contactName = PhoneUtils.getContactName(mContext, callLog.getContact());
        if(TextUtils.isEmpty(contactName)) {
            contactName = PowerDataApplication.getStringResource(R.string.no_name);
        }
        holder.mNameTextView.setText(contactName);
        holder.mNumberTextView.setText(callLog.getContact());
        holder.mAvatarTextView.setText(StringUtils.getAvatarAltFromUserName(contactName));
        holder.mTimeTextView.setText(CalendarUtils.changeDateStringFormat(callLog.getStartTime(), CalendarUtils.DATE_LOG_FORMAT, CalendarUtils.DATE_SHOW_LOG_FORMAT));
        holder.mDurationTextView.setText(StringUtils.getDurationTimeFormat(callLog.getDuration()));
        holder.mStatusTextView.setText(callLog.getStatus());
    }

    @Override
    public int getItemCount() {
        return mCallLogList.size();
    }
}
