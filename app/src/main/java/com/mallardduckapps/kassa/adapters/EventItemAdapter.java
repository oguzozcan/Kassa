package com.mallardduckapps.kassa.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mallardduckapps.kassa.AddNewExpenseActivity;
import com.mallardduckapps.kassa.R;
import com.mallardduckapps.kassa.objects.Event;
import com.mallardduckapps.kassa.utils.Constants;
import com.mallardduckapps.kassa.utils.TimeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 02/10/16.
 */

public class EventItemAdapter extends RecyclerView.Adapter<EventItemAdapter.DataObjectHolder> {

    private final WeakReference<Activity> activity;
    private ArrayList<Event> data;
    private LayoutInflater inflater = null;
    private final RecyclerView eventRecyclerView;
    private final String TAG = "EventItemAdapter";

    private final int defaultBackColor;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        final TextView titleTv;
        final TextView subtitleTv;
        final TextView eventPriceTv;
        final RelativeLayout contentRow;
        final ImageView eventIconIv;
        final ImageView backgroundImage;

        public DataObjectHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            subtitleTv = (TextView) itemView.findViewById(R.id.subtitleTv);
            eventPriceTv = (TextView) itemView.findViewById(R.id.eventPriceTv);
            eventIconIv = (ImageView) itemView.findViewById(R.id.eventIcon);
            backgroundImage = (ImageView) itemView.findViewById(R.id.backgroundImage);
            contentRow = (RelativeLayout) itemView.findViewById(R.id.contentRow);
        }
    }

    @Override
    public EventItemAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_event_item_layout, parent, false);
        view.setOnClickListener(new EventItemAdapter.JobItemClickListener());
        return new EventItemAdapter.DataObjectHolder(view);
    }

    public EventItemAdapter(Activity act, ArrayList events, RecyclerView eventRecyclerView) {
        this.activity = new WeakReference(act);
        this.eventRecyclerView = eventRecyclerView;
        inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        defaultBackColor = ContextCompat.getColor(activity.get(), R.color.white_two);
        if (events != null) {
            data = new ArrayList<>(events);
            Log.d(TAG, "DATA not null : " + data.size());
        }
    }

    public void add(Event data) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(data);
        int position = this.data.size() - 1;
        notifyItemInserted(position);
    }

    public void addData(ArrayList<Event> data) {
        if (this.data == null) {
            this.data = data;
        } else {
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void remove(Event item) {
        int position = data.indexOf(item);
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(EventItemAdapter.DataObjectHolder holder, int position) {
        final Event event = getItem(position);
        holder.titleTv.setText(event.getName());
        String startDate =TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOTime, event.getStartDate());
        String endDate = TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOTime, event.getEndDate());
        String subTitle = startDate + " - " + endDate;
        holder.subtitleTv.setText(subTitle);
        //holder.eventPriceTv.setText(String.format("%.2f %s", event.getPrice(), "TL"));
//        Log.d(TAG, "TITLE: " + event.getServiceName());
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public Event getItem(int position) {
        return data.get(position);
    }

    public int getItemIndex(Event expense) {
        if (data != null) {
            int pos = 0;
            for (Event e : data) {
                if (e.equals(expense)) {
                    return pos;
                }
                pos++;
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class JobItemClickListener implements View.OnClickListener {

        public JobItemClickListener() {
        }

        @Override
        public void onClick(View view) {
            int itemPosition = eventRecyclerView.getChildLayoutPosition(view);
            final Event event = getItem(itemPosition);
            Activity act = activity.get();
            if (act != null && event != null) {
                Intent intent = new Intent(act, AddNewExpenseActivity.class);
                intent.putExtra(AddNewExpenseActivity.CATEGORY_ID_KEY, event.getCategoryId());
                intent.putExtra(AddNewExpenseActivity.POST_OR_UPDATE_KEY, false);
                intent.putExtra(AddNewExpenseActivity.EXPENSE_KEY, event);
                act.startActivityForResult(intent, Constants.EXPENSE_UPDATED);

            }
        }
    }
}
