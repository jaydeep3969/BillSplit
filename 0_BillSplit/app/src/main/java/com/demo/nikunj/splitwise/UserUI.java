package com.demo.nikunj.splitwise;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikunj on 3/13/2018.
 */

public class UserUI {
    private String name;
    private float Amount;
    private String imageUri="https://firebasestorage.googleapis.com/v0/b/demp-fb3d5.appspot.com/o/images%2Fdefault.jpg?alt=media&token=49faa457-096f-43fd-86fa-0c4133421faf";

    public UserUI() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
