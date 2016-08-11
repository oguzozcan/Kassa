package com.mallardduckapps.kassa.services;

import android.util.Log;

import com.mallardduckapps.kassa.KassaApp;
import com.mallardduckapps.kassa.busevents.ApiErrorEvent;
import com.mallardduckapps.kassa.busevents.ExpenseEvents;
import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.utils.Constants;
import com.mallardduckapps.kassa.utils.KassaUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 01/08/16.
 */
public class ExpenseService {

    private final Bus mBus;
    private final KassaApp app;
    private ExpenseRestApi.GetExpensesRestApi getExpenseListRestApi;
    private ExpenseRestApi.GetExpenseRestApi getExpenseRestApi;
    private ExpenseRestApi.DeleteExpenseRestApi deleteExpenseRestApi;
    private ExpenseRestApi.PostExpenseRestApi postExpenseRestApi;
    private final String TAG = "ExpenseService";

    public ExpenseService(KassaApp app, ExpenseRestApi.GetExpensesRestApi getExpenseListRestApi, ExpenseRestApi.GetExpenseRestApi getExpenseRestApi,
                          ExpenseRestApi.PostExpenseRestApi postExpenseRestApi, ExpenseRestApi.DeleteExpenseRestApi deleteExpenseRestApi){
        this.app = app;
        this.mBus = app.getBus();
        this.getExpenseListRestApi = getExpenseListRestApi;
        this.getExpenseRestApi = getExpenseRestApi;
        this.postExpenseRestApi = postExpenseRestApi;
        this.deleteExpenseRestApi = deleteExpenseRestApi;
    }

    @Subscribe
    public void getExpenseList(final ExpenseEvents.ExpenseListRequest event){
        String token = app.getDataSaver().getString(Constants.ACCESS_TOKEN_KEY);
        Log.d(TAG, "GET EXPENSE LIST: token:" + token);
        if(token.equals("")){
            Log.d(TAG, "PROBLEM !!!! Token NULL" );
            return;
        }
        getExpenseListRestApi.getExpenseList(token, "").enqueue(new Callback<ArrayList<Expense>>() {
            @Override
            public void onResponse(Call<ArrayList<Expense>> call, Response<ArrayList<Expense>> response) {
                Log.d(TAG, "ON RESPONSE expenseList: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message() + "- url: " + call.request().url());
                if (response.isSuccessful()) {
                    mBus.post(new ExpenseEvents.ExpenseListResponse(response));
                } else {
                    //mBus.post(new AuthResponse(null, KassaUtils.getErrorMessage(response)));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Expense>> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void postExpense(final ExpenseEvents.PostExpenseRequest event){
        String token = app.getDataSaver().getString(Constants.ACCESS_TOKEN_KEY);
        if(token.equals("")){
            Log.d(TAG, "PROBLEM !!!! Token NULL" );
            return;
        }
        postExpenseRestApi.onExpenseCreated(token, "", event.getExpense()).enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                Log.d(TAG, "ON RESPONSE postexpense: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new ExpenseEvents.PostExpenseResponse(response));
                } else {
                    //mBus.post(new AuthResponse(null, KassaUtils.getErrorMessage(response)));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

    @Subscribe
    public void deleteExpense(final ExpenseEvents.DeleteExpenseRequest event){
        String token = app.getDataSaver().getString(Constants.ACCESS_TOKEN_KEY);
        if(token.equals("")){
            Log.d(TAG, "PROBLEM !!!! Token NULL" );
            return;
        }
        deleteExpenseRestApi.onExpenseDeleted(token, "", event.getExpenseId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "ON RESPONSE delete expense: " + response.isSuccessful() + " - responsecode: " + response.code() + " - response:" + response.message());
                if (response.isSuccessful()) {
                    mBus.post(new ExpenseEvents.DeleteExpenseResponse(response));
                } else {
                    //mBus.post(new AuthResponse(null, KassaUtils.getErrorMessage(response)));
                    mBus.post(new ApiErrorEvent(response.code(), KassaUtils.getErrorMessage(response), false));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "ON FAILURE: " + t.getMessage());
                t.printStackTrace();
                mBus.post(new ApiErrorEvent());
            }
        });
    }

}
