package com.bstar.powerdata.views.adapters;

import android.content.Context;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.models.CallLog;
import com.bstar.powerdata.models.CallLogGroup;
import com.bstar.powerdata.models.Contact;
import com.bstar.powerdata.utils.CalendarUtils;
import com.bstar.powerdata.utils.PhoneUtils;
import com.bstar.powerdata.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CallLogExpandableAdapter extends ExpandableRecyclerAdapter<CallLogGroup, CallLog, CallLogExpandableAdapter.CallLogGroupViewHolder, CallLogExpandableAdapter.CallLogChildViewHolder> {

    private Context mContext;
    private OnChildItemClickListener mListener;

    public interface OnChildItemClickListener {
        void onChildItemSelected(Contact contact);
    }

    public CallLogExpandableAdapter(Context context, List<CallLogGroup> groups, OnChildItemClickListener listener) {
        super(groups);
        mContext = context;
        mListener = listener;
    }

    public class CallLogGroupViewHolder extends ParentViewHolder{

        @BindView(R.id.textview_call_log_group_title)
        TextView mTextViewGroupTitle;

        public CallLogGroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(CallLogGroup group) {
            mTextViewGroupTitle.setText(group.getTitle());
        }
    }

    public class CallLogChildViewHolder extends ChildViewHolder {

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
        @BindView(R.id.imageview_call_log_item_indicator)
        ImageView mIndicatorImageView;
        @BindView(R.id.imageview_call_log_item_type)
        ImageView mTypeImageView;

        public CallLogChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(final CallLog callLog) {
            final String contactName = PhoneUtils.getContactName(mContext, callLog.getContact());
            mNameTextView.setText(contactName);
            mNumberTextView.setText(callLog.getContact());
            mAvatarTextView.setText(StringUtils.getAvatarAltFromUserName(contactName));
            mTimeTextView.setText(CalendarUtils.changeDateStringFormat(callLog.getStartTime(), CalendarUtils.DATE_LOG_FORMAT, CalendarUtils.DATE_SHOW_LOG_FORMAT));
            mDurationTextView.setText(StringUtils.getDurationTimeFormat(callLog.getDuration()));
            mStatusTextView.setText(callLog.getStatus());
            if (callLog.getStatus().equals(PowerDataApplication.getStringResource(R.string.twilio_status_completed))) {
                mIndicatorImageView.setColorFilter(PowerDataApplication.getIntColor(R.color.Design_green));
            } else if (callLog.getStatus().equals(PowerDataApplication.getStringResource(R.string.twilio_status_no_answer))) {
                mIndicatorImageView.setColorFilter(PowerDataApplication.getIntColor(R.color.Design_gold));
            } else if (callLog.getStatus().equals(PowerDataApplication.getStringResource(R.string.twilio_status_busy))) {
                mIndicatorImageView.setColorFilter(PowerDataApplication.getIntColor(R.color.Design_red));
            }

            if (callLog.getType().equals(PowerDataApplication.getStringResource(R.string.twilio_inbox_type))) {
                if(callLog.getStatus().equals(PowerDataApplication.getStringResource(R.string.twilio_status_completed))){
                    mTypeImageView.setImageDrawable(PowerDataApplication.getDrawableResource(R.drawable.ic_receive_calls));
                } else {
                    mTypeImageView.setImageDrawable(PowerDataApplication.getDrawableResource(R.drawable.ic_miss_cal));
                }
            }  else {
                mTypeImageView.setImageDrawable(PowerDataApplication.getDrawableResource(R.drawable.ic_outgoing));
            }

            mNumberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onChildItemSelected(new Contact(contactName, callLog.getContact()));
                }
            });
        }
    }

    @Override
    public CallLogGroupViewHolder onCreateParentViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_log_group_item, parent, false);
        return new CallLogGroupViewHolder(view);
    }

    @UiThread
    @Override
    public CallLogChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_log_list_item, parent, false);
        return new CallLogChildViewHolder(view);
    }

    @UiThread
    @Override
    public void onBindChildViewHolder(CallLogChildViewHolder holder, int parentPosition, int childPosition, CallLog callLog) {
        holder.onBind(callLog);
    }

    @Override
    public void onBindParentViewHolder(CallLogGroupViewHolder holder, int flatPosition, CallLogGroup group) {
        holder.onBind(group);
    }
}
