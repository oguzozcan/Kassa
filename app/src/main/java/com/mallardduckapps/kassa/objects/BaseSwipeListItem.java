package com.mallardduckapps.kassa.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 20/09/16.
 */

public class BaseSwipeListItem implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("name")
    private String name;
    @SerializedName("create_date")
    private String createDate;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("sub_category_id")
    private int subCategoryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.createDate);
        dest.writeInt(this.categoryId);
        dest.writeInt(this.subCategoryId);
    }

    public BaseSwipeListItem() {
    }

    protected BaseSwipeListItem(Parcel in) {
        this.id = in.readInt();
        this.userId = in.readInt();
        this.name = in.readString();
        this.createDate = in.readString();
        this.categoryId = in.readInt();
        this.subCategoryId = in.readInt();
    }

    public static final Parcelable.Creator<BaseSwipeListItem> CREATOR = new Parcelable.Creator<BaseSwipeListItem>() {
        @Override
        public BaseSwipeListItem createFromParcel(Parcel source) {
            return new BaseSwipeListItem(source);
        }

        @Override
        public BaseSwipeListItem[] newArray(int size) {
            return new BaseSwipeListItem[size];
        }
    };
}
