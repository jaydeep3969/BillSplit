package com.demo.nikunj.splitwise;

/**
 * Created by Nikunj on 3/14/2018.
 */

public class Group {
    private String grpname;
    private String Uid;
    private String imageUri="https://firebasestorage.googleapis.com/v0/b/demp-fb3d5.appspot.com/o/images%2Fgroup-of-three-men-standing-side-by-side-hugging-each-other_318-63105.jpg?alt=media&token=e6986745-c8ab-4db3-bc7e-d3f1026f6356";

    public Group(String grpname, String uid) {
        this.grpname = grpname;
        Uid = uid;
    }

    public String getGrpname() {
        return grpname;
    }

    public void setGrpname(String grpname) {
        this.grpname = grpname;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
