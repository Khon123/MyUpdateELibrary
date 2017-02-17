package android.camant.com.elibraryandroid.srv;

import android.camant.com.elibraryandroid.db.BaseDbHelper;
import android.camant.com.elibraryandroid.db.BookDbHelper;
import android.camant.com.elibraryandroid.db.CategoryDbHelper;
import android.camant.com.elibraryandroid.models.Category;
import android.camant.com.elibraryandroid.utils.MainUtil;
import android.camant.com.elibraryandroid.wrappers.BookWrapper;
import android.camant.com.elibraryandroid.wrappers.CategoryRelatedWrapper;
import android.camant.com.elibraryandroid.wrappers.CategoryWrapper;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Institute on 1/23/2017.
 */

public class CategorySynchronizer {
    private Context context;
    public CategorySynchronizer(Context context){
        this.context = context;
    }
    public void sync(String baseUrl, int start) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ELibraryServer server = retrofit.create(ELibraryServer.class);
        Call<CategoryWrapper> categoryWrapperCall = server.listCategories(start, 20);
        Response<CategoryWrapper> response = categoryWrapperCall.execute();
        if(response != null){
            CategoryWrapper categoryWrapper = response.body();
            int count = insertOrUpdateCategoriesInDB(categoryWrapper);
            if(count >= 20){
                sync(baseUrl, count);
            }
        }
    }
    public void async(final String baseUrl, int start) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ELibraryServer server = retrofit.create(ELibraryServer.class);
        Call<CategoryWrapper> categoryWrapperCall = server.listCategories(start, 20);
        categoryWrapperCall.enqueue(new Callback<CategoryWrapper>() {
            @Override
            public void onResponse(Call<CategoryWrapper> call, Response<CategoryWrapper> response) {
                if(response != null){
                    CategoryWrapper categoryWrapper = response.body();
                    int count = insertOrUpdateCategoriesInDB(categoryWrapper);
                    if(count >= 20){
                        async(baseUrl, count);
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryWrapper> call, Throwable t) {

            }
        });

    }
    private int insertOrUpdateCategoriesInDB(final CategoryWrapper categoryWrapper){
        if(categoryWrapper != null && categoryWrapper.getDatas() != null && categoryWrapper.getDatas().size() > 0){
            CategoryDbHelper categoryDbHelper = new CategoryDbHelper(context);
            File file = new File(BaseDbHelper.DATABASE_FILE_PATH);
            file = new File(file, "book_files");
            if(!file.exists()) file.mkdir();
            if(file.exists()) {
                Category category;
                for (CategoryRelatedWrapper wrapper : categoryWrapper.getDatas()) {
                    category = wrapper.getCategory();
                    category.setBooks_count(wrapper.getBook().size());
                    categoryDbHelper.insertOrUpdate(wrapper.getCategory());
                }
                if (categoryWrapper.getDatas().size() >= 20) {
                    return categoryDbHelper.getCount();
                }
            }
        }
        return -1;
    }
}
