package com.mallardduckapps.kassa.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mallardduckapps.kassa.AddNewEventActivity;
import com.mallardduckapps.kassa.R;
import com.mallardduckapps.kassa.objects.EventMember;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by oguzemreozcan on 02/10/16.
 */

public class MemberItemRecyclerViewAdapter extends RecyclerView.Adapter<MemberItemRecyclerViewAdapter.ViewHolder> {

    private final List<EventMember> mValues;
    private final OnMemberInteractionListener mListener;
    //private final int color;

    public MemberItemRecyclerViewAdapter(List<EventMember> items, OnMemberInteractionListener listener) {
        mValues = items;
        mListener = listener;
        //this.color = color;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.memberName.setText(holder.mItem.getPerson().getName());
        //holder.memberImage.setImageResource(holder.mItem.resId);
        //holder.mView.setBackgroundColor(color);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMemberClick(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CircleImageView memberImage;
        public final TextView memberName;
        public EventMember mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            memberImage = (CircleImageView) view.findViewById(R.id.memberIcon);
            memberName = (TextView) view.findViewById(R.id.memberName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + memberName.getText() + "'";
        }
    }

    public interface OnMemberInteractionListener {
        void onMemberClick(EventMember item);
    }
}

