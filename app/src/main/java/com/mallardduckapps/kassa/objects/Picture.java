package com.mallardduckapps.kassa.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by oguzemreozcan on 06/09/16.
 */
public class Picture {

    @SerializedName("picture_content")
    String pictureContent;

    @SerializedName("picture_extension")
    String pictureExtension;

    public Picture(String pictureContent, String pictureExtension) {
        this.pictureContent = pictureContent;
        this.pictureExtension = pictureExtension;
    }

    private byte[] imageEncoded;

    public byte[] getImageEncoded() {
        return imageEncoded;
    }

    public void setImageEncoded(byte[] imageEncoded) {
        this.imageEncoded = imageEncoded;
    }

    public String getPictureContent() {
        return pictureContent;
    }

    public void setPictureContent(String pictureContent) {
        this.pictureContent = pictureContent;
    }

    public String getPictureExtension() {
        return pictureExtension;
    }

    public void setPictureExtension(String pictureExtension) {
        this.pictureExtension = pictureExtension;
    }
}
