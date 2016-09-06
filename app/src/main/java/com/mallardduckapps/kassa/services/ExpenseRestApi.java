package com.mallardduckapps.kassa.services;

import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.utils.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by oguzemreozcan on 28/07/16.
 */
public class ExpenseRestApi {

    public interface GetExpensesRestApi{
        @GET(Constants.EXPENSE)
        Call<ArrayList<Expense>> getExpenseList(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    //TODO no endpoint like this
    public interface GetExpensesByCategoryRestApi{
        @GET(Constants.EXPENSE + "/" + "{categoryId}")
        Call<ArrayList<Expense>> getExpenseListByCategoryId(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("categoryId") int categoryId);
    }

    public interface PostExpenseRestApi{
        @POST(Constants.EXPENSE)
        Call<Expense> onExpenseCreated(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body Expense expenseJsonBody);
    }

    public interface GetExpenseRestApi{
        @GET(Constants.EXPENSE + "/" + "{expenseId}")
        Call<Expense> getExpenseWithId(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("expenseId") long expenseId);
    }

    public interface UpdateExpenseRestApi{
        @PATCH(Constants.EXPENSE + "/" + "{expenseId}")
        Call<Expense> onExpenseUpdated(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("expenseId") long expenseId, @Body Expense expenseJsonBody);
    }

    public interface DeleteExpenseRestApi{
        @DELETE(Constants.EXPENSE + "/"+ "{expenseId}")
        Call<String> onExpenseDeleted(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("expenseId") long expenseId);
    }

}
