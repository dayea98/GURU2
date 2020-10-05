package com.example.sy925.myapplication;

import java.io.Serializable;

public class ImageDTO implements Serializable {

    public String id;
    public String imageName;
    public String imageUrl;
    public String title;
    public String description;
    public String userId;


    @Override
    public String toString() {
        return "ImgaeDTO{" +
                "id=" + id + "/n, " +
                "imageName=" + imageName + "/n, " +
                "imagUrl=" + imageUrl + "/n, " +
                "title=" + title + "/n, " +
                "desc=" + description + "/n, " +
                "userId=" + userId  + "}";
    }
}
