package com.mallardduckapps.kassa.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mallardduckapps.kassa.BaseSwipeListItemActivity;
import com.mallardduckapps.kassa.R;
import com.mallardduckapps.kassa.objects.TypeItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TypeItem} and makes a call to the
 * specified {@link BaseSwipeListItemActivity.OnTypeListItemInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TypeItemRecyclerViewAdapter extends RecyclerView.Adapter<TypeItemRecyclerViewAdapter.ViewHolder> {

    private final List<TypeItem> mValues;
    private final BaseSwipeListItemActivity.OnTypeListItemInteractionListener mListener;
    private final int color;

    public TypeItemRecyclerViewAdapter(List<TypeItem> items, BaseSwipeListItemActivity.OnTypeListItemInteractionListener listener, int color) {
        mValues = items;
        mListener = listener;
        this.color = color;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.type_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.typeText.setText(holder.mItem.name);
        holder.typeImage.setImageResource(holder.mItem.resId);
        holder.mView.setBackgroundColor(color);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onItemClick(holder.mItem);
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
        public final ImageView typeImage;
        public final TextView typeText;
        public TypeItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            typeImage = (ImageView) view.findViewById(R.id.typeIcon);
            typeText = (TextView) view.findViewById(R.id.typeText);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + typeText.getText() + "'";
        }
    }
}
