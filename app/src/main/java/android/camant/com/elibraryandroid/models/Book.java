package android.camant.com.elibraryandroid.models;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Institute on 1/23/2017.
 */

public class Book extends BaseModel implements Parcelable {
    private String title;
    private Integer order;
    private String book_file;
    private String thumbnail;
    private String description;
    private int category_id;
    private String thumbnail_local;
    private String book_file_local;
    private long download_id = -1;
    private int progress;

    protected Book(Parcel in) {
        title = in.readString();
        book_file = in.readString();
        thumbnail = in.readString();
        description = in.readString();
        category_id = in.readInt();
        thumbnail_local = in.readString();
        book_file_local = in.readString();
        download_id = in.readLong();
        progress = in.readInt();
    }
    public Book(){

    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getBook_file() {
        return book_file;
    }

    public void setBook_file(String book_file) {
        this.book_file = book_file;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getThumbnail_local() {
        return thumbnail_local;
    }

    public void setThumbnail_local(String thumbnail_local) {
        this.thumbnail_local = thumbnail_local;
    }

    public String getBook_file_local() {
        return book_file_local;
    }

    public void setBook_file_local(String book_file_local) {
        this.book_file_local = book_file_local;
    }

    public long getDownload_id() {
        return download_id;
    }

    public void setDownload_id(long download_id) {
        this.download_id = download_id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = super.getContentValues();
        contentValues.put("title", title);
        contentValues.put("`order`", order);
        contentValues.put("book_file", book_file);
        contentValues.put("thumbnail", thumbnail);
        contentValues.put("description", description);
        contentValues.put("category_id", category_id);
        contentValues.put("download_id", download_id);
        contentValues.put("progress", progress);
        if(thumbnail_local != null && !thumbnail_local.isEmpty()) {
            contentValues.put("thumbnail_local", thumbnail_local);
        }
        if(book_file_local != null && !book_file_local.isEmpty()) {
            contentValues.put("book_file_local", book_file_local);
        }
        return contentValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(book_file);
        parcel.writeString(thumbnail);
        parcel.writeString(description);
        parcel.writeInt(category_id);
        parcel.writeString(thumbnail_local);
        parcel.writeString(book_file_local);
        parcel.writeLong(download_id);
        parcel.writeInt(progress);
    }
}