package com.demo.nikunj.splitwise;

/**
 * Created by Nikunj on 3/8/2018.
 */

public class User {
    private String fullname;
    private String email;
    private String number;
    private String imageUri="https://firebasestorage.googleapis.com/v0/b/demp-fb3d5.appspot.com/o/images%2Fdefault.jpg?alt=media&token=49faa457-096f-43fd-86fa-0c4133421faf";

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }



    public User() {
    }
    public User(String name)
    {
        this.fullname = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
