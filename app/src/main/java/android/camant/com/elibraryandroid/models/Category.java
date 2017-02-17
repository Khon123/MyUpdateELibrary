package android.camant.com.elibraryandroid.models;

import android.content.ContentValues;

/**
 * Created by Institute on 1/23/2017.
 */

public class Category extends BaseModel {
    private String name;
    private Integer order;
    private int books_count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public int getBooks_count() {
        return books_count;
    }

    public void setBooks_count(int books_count) {
        this.books_count = books_count;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();
        contentValues.put("name", name);
        contentValues.put("`order`", order);
        contentValues.put("books_count", books_count);
        return contentValues;
    }
}
