package android.camant.com.elibraryandroid.srv;

import android.camant.com.elibraryandroid.models.Book;
import android.camant.com.elibraryandroid.wrappers.BookWrapper;
import android.camant.com.elibraryandroid.wrappers.CategoryWrapper;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Institute on 1/23/2017.
 */

public interface ELibraryServer {
    @POST("categories") Call<CategoryWrapper> listCategories();
    @FormUrlEncoded @POST("categories") Call<CategoryWrapper> listCategories(@Field("offset") int offset);
    @FormUrlEncoded @POST("categories") Call<CategoryWrapper> listCategories(@Field("offset") int offset, @Field("limit") int limit);
    @FormUrlEncoded @POST("categories") Call<CategoryWrapper> listCategories(@Field("offset") int offset, @Field("limit") int limit, @Field("name") String name);

    @POST("books/{category_id}") Call<BookWrapper> listBooks(@Path("category_id") int categoryId);
    @FormUrlEncoded @POST("books/{category_id}") Call<BookWrapper> listBooks(@Path("category_id") int categoryId, @Field("offset") int offset, @Field("limit") int limit);
    @FormUrlEncoded @POST("books/{category_id}") Call<BookWrapper> listBooks(@Path("category_id") int categoryId, @Field("offset") int offset, @Field("limit") int limit, @Field("title") String title);

    @POST("books") Call<BookWrapper> listBooks();
    @FormUrlEncoded @POST("books") Call<BookWrapper> listBooks(@Field("offset") int offset, @Field("limit") int limit);
    @FormUrlEncoded @POST("books") Call<BookWrapper> listBooks(@Field("offset") int offset, @Field("limit") int limit, @Field("title") String title);
}
