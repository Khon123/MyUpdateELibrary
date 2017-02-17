package android.camant.com.elibraryandroid.models;

import android.content.ContentValues;

/**
 * Created by Institute on 1/23/2017.
 */

public class BaseModel {
    private int id;
    private String created, modified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
    public ContentValues getContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", getId());
        contentValues.put("created", getCreated());
        contentValues.put("modified", getModified());
        return contentValues;
    }
}
