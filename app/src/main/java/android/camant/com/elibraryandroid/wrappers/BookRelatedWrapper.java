package android.camant.com.elibraryandroid.wrappers;

import android.camant.com.elibraryandroid.models.Book;
import android.camant.com.elibraryandroid.models.Category;

import java.util.ArrayList;

/**
 * Created by Institute on 1/23/2017.
 */

public class BookRelatedWrapper {
    private android.camant.com.elibraryandroid.models.Category Category;
    private android.camant.com.elibraryandroid.models.Book Book;

    public android.camant.com.elibraryandroid.models.Category getCategory() {
        return Category;
    }

    public void setCategory(android.camant.com.elibraryandroid.models.Category category) {
        Category = category;
    }

    public android.camant.com.elibraryandroid.models.Book getBook() {
        return Book;
    }

    public void setBook(android.camant.com.elibraryandroid.models.Book book) {
        Book = book;
    }
}
