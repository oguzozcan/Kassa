package com.mallardduckapps.kassa;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mallardduckapps.kassa.adapters.ExpenseItemAdapter;
import com.mallardduckapps.kassa.objects.TypeItem;
import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSwipeListItemActivity extends BaseActivity implements ExpenseItemAdapter.RemoveExpenseCallback  {

    protected RecyclerView expenseListView;
    protected RelativeLayout loadingLayout;
    protected int categoryId = -1;

    @Override
    protected void setTag() {
        TAG = "Swipe_List_Activity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        expenseListView = (RecyclerView) findViewById(R.id.expenseListView);
        expenseListView.setLayoutManager(mLayoutManager);
        expenseListView.setNestedScrollingEnabled(false);
        expenseListView.setItemAnimator(new DefaultItemAnimator());
        expenseListView.setHasFixedSize(true);
        setUpAnimationDecoratorHelper();

        categoryId = getIntent().getExtras().getInt(AddNewExpenseActivity.CATEGORY_ID_KEY, 1);
        String title = "";
        switch (categoryId){
            case Constants.CATEGORY_ID_DAILY:
                title = getString(R.string.daily);
                break;
            case Constants.CATEGORY_ID_HOME:
                title = getString(R.string.home);
                break;
            case Constants.CATEGORY_ID_EVENT:
                title = getString(R.string.event);
                break;
            case Constants.CATEGORY_ID_WORK:
                title = getString(R.string.work);
                break;
        }
        getSupportActionBar().setTitle(title);
    }

    protected List<TypeItem> getTypeItems(int type){
        TypedArray imgs;
        TypedArray names;
        TypedArray typeIds;
        if(type == Constants.CATEGORY_ID_EVENT){
            imgs = getResources().obtainTypedArray(R.array.event_type_images);
            names = getResources().obtainTypedArray(R.array.event_type_names);
            typeIds = getResources().obtainTypedArray(R.array.event_type_ids);
        }else{
            imgs = getResources().obtainTypedArray(R.array.daily_type_images);
            names = getResources().obtainTypedArray(R.array.daily_type_names);
            typeIds = getResources().obtainTypedArray(R.array.daily_type_ids);
        }

        int size = imgs.length();
        ArrayList<TypeItem> items = new ArrayList<>(size);
        for(int i = 0 ; i < size; i ++){
            TypeItem item = new TypeItem(type, names.getString(i), imgs.getResourceId(i, -1), typeIds.getResourceId(i, -1));
            items.add(item);
        }
        imgs.recycle();
        names.recycle();
        return items;
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    protected void setUpAnimationDecoratorHelper() {
        expenseListView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }
                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {
                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;
                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();
                    // this we need to find out
                    int top = 0;
                    int bottom = 0;
                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }
        });
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    protected void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(BaseSwipeListItemActivity.this, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }
            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ExpenseItemAdapter adapter = (ExpenseItemAdapter)recyclerView.getAdapter();
                if (adapter.isUndoOn() && adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                ExpenseItemAdapter adapter = (ExpenseItemAdapter)expenseListView.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }
                if (!initiated) {
                    init();
                }
                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                xMark.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(expenseListView);
    }

    public interface OnTypeListItemInteractionListener {
        void onItemClick(TypeItem item);
    }
}
