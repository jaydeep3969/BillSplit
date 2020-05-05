package com.demo.nikunj.splitwise;

import java.io.Serializable;

/**
 * Created by Nikunj on 3/20/2018.
 */

public class UserUID implements Serializable {
    private String id;
    private String name;
    private float amount;
    private String imageUri="https://firebasestorage.googleapis.com/v0/b/demp-fb3d5.appspot.com/o/images%2Fdefault.jpg?alt=media&token=49faa457-096f-43fd-86fa-0c4133421faf";

    public UserUID(String id) {
        this.id = id;
    }

    public UserUID() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
