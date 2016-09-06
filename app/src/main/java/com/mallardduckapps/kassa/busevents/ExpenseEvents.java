package com.mallardduckapps.kassa.busevents;

import com.mallardduckapps.kassa.objects.Expense;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * Created by oguzemreozcan on 01/08/16.
 */
public class ExpenseEvents {

    public static class ExpenseListRequest{
    }

    public static class ExpenseListResponse{
        private Response<ArrayList<Expense>> response;

        public ExpenseListResponse(Response<ArrayList<Expense>> response) {
            this.response = response;
        }

        public Response<ArrayList<Expense>> getResponse() {
            return response;
        }
    }

    public static class ExpenseListByCategoryRequest{
        private int categoryId;

        public ExpenseListByCategoryRequest(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getCategoryId() {
            return categoryId;
        }
    }

    public static class ExpenseListByCategoryResponse{
        private Response<ArrayList<Expense>> response;

        public ExpenseListByCategoryResponse(Response<ArrayList<Expense>> response) {
            this.response = response;
        }

        public Response<ArrayList<Expense>> getResponse() {
            return response;
        }
    }

    public static class PostExpenseRequest{
        private final Expense expense;

        public PostExpenseRequest(Expense expense) {
            this.expense = expense;
        }

        public Expense getExpense() {
            return expense;
        }
    }

    public static class PostExpenseResponse{
        private Response<Expense> response;

        public PostExpenseResponse(Response<Expense> response) {
            this.response = response;
        }

        public Response<Expense> getExpense() {
            return response;
        }
    }

    public static class GetExpenseRequest{
        private int expenseId;

        public GetExpenseRequest(int expenseId) {
            this.expenseId = expenseId;
        }

        public int getExpenseId() {
            return expenseId;
        }
    }

    public static class GetExpenseResponse{
        private Response<Expense> response;

        public GetExpenseResponse(Response<Expense> response) {
            this.response = response;
        }

        public Response<Expense> getExpense() {
            return response;
        }
    }

    public static class DeleteExpenseRequest{
        private int expenseId;

        public DeleteExpenseRequest(int expenseId) {
            this.expenseId = expenseId;
        }

        public int getExpenseId() {
            return expenseId;
        }
    }

    public static class DeleteExpenseResponse{
        private Response<String> response;

        public DeleteExpenseResponse(Response<String> response) {
            this.response = response;
        }

        public Response<String> getExpense() {
            return response;
        }
    }

    public static class UpdateExpenseRequest{
        private int expenseId;
        private Expense expense;

        public UpdateExpenseRequest(Expense expense) {
            this.expense = expense;
            this.expenseId = expense.getId();//expenseId;
        }

        public int getExpenseId() {
            return expenseId;
        }

        public Expense getExpense() {
            return expense;
        }
    }

    public static class UpdateExpenseResponse{
        private Response<Expense> response;

        public UpdateExpenseResponse(Response<Expense> response) {
            this.response = response;
        }

        public Response<Expense> getExpense() {
            return response;
        }
    }
}
