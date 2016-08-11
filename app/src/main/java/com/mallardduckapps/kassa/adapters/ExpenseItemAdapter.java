package com.mallardduckapps.kassa.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mallardduckapps.kassa.R;
import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.utils.TimeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oguzemreozcan on 08/08/16.
 */
public class ExpenseItemAdapter extends RecyclerView.Adapter<ExpenseItemAdapter.DataObjectHolder> {

    private final WeakReference<Activity> activity;
    private ArrayList<Expense> data;
    private RemoveExpenseCallback removeExpenseCallback;
    private LayoutInflater inflater = null;
    private final RecyclerView expenseRecyclerView;
    private final String TAG = "ExpenseItemAdapter";

    private final int defaultBackColor;
    private int lastInsertedIndex; // so we can add some more items for testing purposes
    private boolean undoOn; // is undo on, you can turn it on from the toolbar menu
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    private HashMap<Expense, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    private List<Expense> itemsPendingRemoval;
    private static final int PENDING_REMOVAL_TIMEOUT = 2500; // 3sec

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        final TextView titleTv;
        final TextView subtitleTv;
        final TextView expensePriceTv;
        final TextView undoTxt;
        final RelativeLayout contentRow;
        final ImageView expenseIconIv;

        public DataObjectHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            subtitleTv = (TextView) itemView.findViewById(R.id.subtitleTv);
            expensePriceTv = (TextView) itemView.findViewById(R.id.expensePriceTv);
            expenseIconIv = (ImageView) itemView.findViewById(R.id.expenseIcon);
            undoTxt = (TextView) itemView.findViewById(R.id.undoTxt);
            contentRow = (RelativeLayout) itemView.findViewById(R.id.contentRow);
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_expense_item_layout, parent, false);
        view.setOnClickListener(new JobItemClickListener());
        return new DataObjectHolder(view);
    }

    public ExpenseItemAdapter(Activity act, RemoveExpenseCallback removeExpenseCallback, ArrayList<Expense> expenses, RecyclerView expenseRecyclerView) {
        this.activity = new WeakReference(act);
        this.expenseRecyclerView = expenseRecyclerView;
        this.removeExpenseCallback = removeExpenseCallback;
        inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        defaultBackColor = ContextCompat.getColor(activity.get(), R.color.white_two);
        itemsPendingRemoval = new ArrayList<>();
        if (expenses != null) {
            data = new ArrayList<>(expenses);
            Log.d(TAG, "DATA not null : " + data.size());
            lastInsertedIndex = data.size() -1;
        }
    }

    public void add(Expense data) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(data);
        int position = this.data.size() - 1;
        lastInsertedIndex ++;
        notifyItemInserted(position);
    }

    public void addData(ArrayList<Expense> data) {
        if (this.data == null) {
            this.data = data;
        } else {
            this.data.addAll(data);
        }
        lastInsertedIndex += data.size();
        notifyDataSetChanged();
    }

    public void remove(Expense item) {
        int position = data.indexOf(item);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(int position) {
        Expense item = data.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        data.remove(position);
        notifyItemRemoved(position);
    }

    public boolean isPendingRemoval(int position) {
        Expense item = data.get(position);
        return itemsPendingRemoval.contains(item);
    }

    public void pendingRemoval(int position) {
        final Expense item = data.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    //TODO callback to actually remove item
                    if(removeExpenseCallback != null){
                        removeExpenseCallback.removeExpense(item.getId());
                    }
                    remove(data.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        final Expense event = getItem(position);
        if (itemsPendingRemoval.contains(event)) {
            setContentVisible(holder, false);
            holder.undoTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(event);
                    pendingRunnables.remove(event);
                    if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(event);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(getItemIndex(event));//items.indexOf(item));
                }
            });
        }else{
            setContentVisible(holder, true);
            holder.undoTxt.setOnClickListener(null);
            holder.titleTv.setText(event.getName());
            holder.subtitleTv.setText(TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOTime, event.getDueDate()));
            holder.expensePriceTv.setText(String.format("%.2f %s", event.getPrice(), "TL"));
        }
//        Log.d(TAG, "TITLE: " + event.getServiceName());
    }

    private void setContentVisible(DataObjectHolder holder, boolean visible){
        if (visible) {
            holder.contentRow.setBackgroundColor(defaultBackColor);
            holder.titleTv.setVisibility(View.VISIBLE);
            holder.subtitleTv.setVisibility(View.VISIBLE);
            holder.expensePriceTv.setVisibility(View.VISIBLE);
            holder.expenseIconIv.setVisibility(View.VISIBLE);
            holder.undoTxt.setVisibility(View.GONE);
        }else{
            holder.contentRow.setBackgroundColor(Color.RED);
            holder.titleTv.setVisibility(View.INVISIBLE);
            holder.subtitleTv.setVisibility(View.INVISIBLE);
            holder.expensePriceTv.setVisibility(View.INVISIBLE);
            holder.expenseIconIv.setVisibility(View.INVISIBLE);
            holder.undoTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public Expense getItem(int position) {
        return data.get(position);
    }

    public int getItemIndex(Expense expense){
        if(data != null){
            int pos = 0;
            for(Expense e : data){
                if(e.equals(expense)){
                    return pos;
                }
                pos ++;
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    class JobItemClickListener implements View.OnClickListener {

        public JobItemClickListener() {
        }

        @Override
        public void onClick(View view) {
            int itemPosition = expenseRecyclerView.getChildLayoutPosition(view);
            final Expense event = getItem(itemPosition);
            Activity act = activity.get();
            if (act != null && event != null) {
//            Intent jobIntent = new Intent(act, OpportunityActivity.class);
//            String date = TimeUtils.convertDateTimeFormat(TimeUtils.dfISO, TimeUtils.dtfOutWOYear, event.getAppointmentDate());//event.getAppointmentDate();
//            jobIntent.putExtra(JobEvents.PARAM_JOB_ID, event.getJobId());
//            jobIntent.putExtra(JobEvents.PARAM_SERVICE_ID, event.getServiceId());
//            jobIntent.putExtra(JobEvents.PARAM_SERVICE_NAME, event.getServiceName());
//            jobIntent.putExtra(JobEvents.PARAM_CUSTOMER_NAME, event.getCustomerFullName());
//            jobIntent.putExtra(JobEvents.PARAM_QUOTE_REMAINING_TIME, event.getJobQuoteExpireDate());
//            jobIntent.putExtra(JobEvents.PARAM_PROVIDER_PROFILE_ID, event.getProfileId());
//            Log.d(TAG, "PROVIDER PROFILE ID: " + event.getProfileId() + " - sectionNumber : " + sectionNumber + "EVENT DATE: " + date);
//            jobIntent.putExtra(JobEvents.PARAM_JOB_DATE, date);
//            String addressTxt = event.getJobDistrict() + " " + event.getAddress() + "\n" + event.getJobCity() + ", " + event.getJobState();
//            jobIntent.putExtra(JobEvents.PARAM_JOB_ADDRESS, addressTxt);
//            act.startActivityForResult(jobIntent, Constants.UPDATE_JOBS_PAGE);
//            BaseActivity.setTranslateAnimation(act);
            }
        }
    }

    public interface RemoveExpenseCallback{
        void removeExpense(int expenseId);
    }
}



