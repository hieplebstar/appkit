package com.bstar.powerdata.fragments.log;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.bstar.powerdata.PowerDataApplication;
import com.bstar.powerdata.R;
import com.bstar.powerdata.base.ButterKnifeEventFragment;
import com.bstar.powerdata.models.CallLog;
import com.bstar.powerdata.models.CallLogGroup;
import com.bstar.powerdata.models.Contact;
import com.bstar.powerdata.utils.CalendarUtils;
import com.bstar.powerdata.views.adapters.CallLogExpandableAdapter;
import com.bstar.powerdata.views.adapters.CallLogListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;

public class LogFragment extends ButterKnifeEventFragment implements LogView, CallLogExpandableAdapter.OnChildItemClickListener {

    @BindView(R.id.recyclerview_call_log)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressbar_call_log)
    ProgressBar mProgressBar;
    @BindView(R.id.radiobutton_call_log_type_all)
    RadioButton mAllRadioButton;
    @BindView(R.id.radiobutton_call_log_type_missed)
    RadioButton mMissedRadioButton;

    private CallLogExpandableAdapter mAdapter;
    private LogPresenter mPresenter;

    public static LogFragment newInstance() {
        return new LogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new LogPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start(this);
        mPresenter.doLoadCallLog(getActivity().getApplicationContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.stop();
    }

    @Override
    public void showCallLog(List<CallLogGroup> callLogGroupList) {
        if (callLogGroupList == null) return;
        mProgressBar.setVisibility(View.GONE);
        mAdapter = new CallLogExpandableAdapter(getActivity(), callLogGroupList, this);
        mAdapter.expandAllParents();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDeviceNameChanged() {
        mPresenter.doLoadCallLog(getActivity().getApplicationContext());
    }

    @OnCheckedChanged(R.id.radiobutton_call_log_type_all)
    void onChangeType(CompoundButton compoundButton, boolean checked) {
        mPresenter.setShowAllCallLog(checked);
        if(checked){
            mMissedRadioButton.setTextColor(PowerDataApplication.getIntColor(R.color.Design_green));
            mAllRadioButton.setTextColor(PowerDataApplication.getIntColor(R.color.Design_white));
        } else {
            mMissedRadioButton.setTextColor(PowerDataApplication.getIntColor(R.color.Design_white));
            mAllRadioButton.setTextColor(PowerDataApplication.getIntColor(R.color.Design_green));
        }
    }

    @Override
    public void onChildItemSelected(Contact contact) {
        mPresenter.outGoingCall(contact);
    }
}
