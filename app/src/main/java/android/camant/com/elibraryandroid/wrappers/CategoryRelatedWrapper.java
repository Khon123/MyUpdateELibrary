package android.camant.com.elibraryandroid.wrappers;

import android.camant.com.elibraryandroid.models.Book;
import android.camant.com.elibraryandroid.models.Category;

import java.util.ArrayList;

/**
 * Created by Institute on 1/23/2017.
 */

public class CategoryRelatedWrapper {
    private android.camant.com.elibraryandroid.models.Category Category;
    private ArrayList<Book> Book = new ArrayList<>();

    public android.camant.com.elibraryandroid.models.Category getCategory() {
        return Category;
    }

    public void setCategory(android.camant.com.elibraryandroid.models.Category category) {
        Category = category;
    }

    public ArrayList<android.camant.com.elibraryandroid.models.Book> getBook() {
        return Book;
    }

    public void setBook(ArrayList<android.camant.com.elibraryandroid.models.Book> book) {
        Book = book;
    }
}
