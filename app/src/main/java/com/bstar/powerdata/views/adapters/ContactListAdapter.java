package com.bstar.powerdata.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bstar.powerdata.R;
import com.bstar.powerdata.models.Contact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemSelected(Contact contact);
    }

    private List<Contact> mContactList;
    private OnItemClickListener mOnItemClickListener;

    public ContactListAdapter(OnItemClickListener onItemClickListener) {
        mContactList = new ArrayList<>();
        mOnItemClickListener = onItemClickListener;
    }

    public void setItems(List<Contact> list) {
        if (list == null) return;
        this.mContactList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_contact_item_name)
        TextView mNameTextView;
        @BindView(R.id.contact_item_container)
        View mContainerLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Contact contact = mContactList.get(position);
        holder.mNameTextView.setText(contact.getUserName());
        holder.mContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemSelected(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }
}
