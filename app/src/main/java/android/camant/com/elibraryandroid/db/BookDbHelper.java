package android.camant.com.elibraryandroid.db;

import android.camant.com.elibraryandroid.models.BaseModel;
import android.camant.com.elibraryandroid.models.Book;
import android.camant.com.elibraryandroid.models.Category;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sreng on 1/15/2017.
 */

public class BookDbHelper extends BaseDbHelper {
    private static final String TABLE_NAME = "books";
    public BookDbHelper(Context context) {
        super(context, TABLE_NAME+".db");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        super.onCreate(sqLiteDatabase);

        String query = "CREATE TABLE " + TABLE_NAME + "(" + getCreateTableQuery(Book.class) + ")";
        Log.d("CREATE_TABLE", query);
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        super.onUpgrade(sqLiteDatabase, i, i1);
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public void deleteAll(){
        getWritableDatabase().execSQL("DELETE FROM "+TABLE_NAME);
    }
    public void insertAll(List<Book> staffs){
        if(staffs != null && staffs.size() > 0){
            SQLiteDatabase db = getWritableDatabase();
            for(Book staff:staffs){
                db.insert(TABLE_NAME, "full_name", staff.getContentValues());
            }
        }
    }
    public void insert(Book staff){
        long id = getWritableDatabase().insert(TABLE_NAME, "full_name", staff.getContentValues());
        if(id >= 0) staff.setId((int)id);
    }

    public void insertOrUpdate(Book book){
        super.insertOrUpdate(book, TABLE_NAME);
    }

    public int getCount(){
        Cursor cursor = getReadableDatabase().rawQuery("SELECT count(*) FROM " + TABLE_NAME, null);
        if(cursor.moveToFirst()) return cursor.getInt(0);
        return 0;
    }

    public Book getById(int id) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{"" + id});
        Book staff = null;
        if(cursor.moveToFirst()){
            staff = buildFromCursor(cursor);
        }
        cursor.close();
        return staff;
    }
    private Book buildFromCursor(Cursor cursor){
        Book s = new Book();
        s.setId(cursor.getInt(cursor.getColumnIndex("id")));
        s.setModified(cursor.getString(cursor.getColumnIndex("modified")));
        s.setCreated(cursor.getString(cursor.getColumnIndex("created")));
        s.setBook_file(cursor.getString(cursor.getColumnIndex("book_file")));
        s.setBook_file_local(cursor.getString(cursor.getColumnIndex("book_file_local")));
        s.setThumbnail(cursor.getString(cursor.getColumnIndex("thumbnail")));
        s.setThumbnail_local(cursor.getString(cursor.getColumnIndex("thumbnail_local")));
        s.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        s.setCategory_id(cursor.getInt(cursor.getColumnIndex("category_id")));
        s.setOrder(cursor.getInt(cursor.getColumnIndex("order")));
        s.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        s.setProgress(cursor.getInt(cursor.getColumnIndex("progress")));
        s.setDownload_id(cursor.getLong(cursor.getColumnIndex("download_id")));
        return s;
    }
    public ArrayList<Book> listBooks(Integer categoryId, int offset, int limit){
        return listBooks(categoryId, false, offset, limit);
    }
    public ArrayList<Book> listBooks(Integer categoryId, boolean downloaded, int offset, int limit){
        Cursor cursor;
        String query = "SELECT * FROM " + TABLE_NAME;
        String where = "";
        ArrayList<String> params = new ArrayList<>();
        boolean hasAnd = false;
        if(categoryId != null) {
            where += " category_id=?";
            params.add(""+categoryId);
            hasAnd = true;
        }
        if(downloaded){
            if(hasAnd) query += " AND";
            where += " progress=?";
            params.add("100");
            hasAnd = true;
        }
        if(hasAnd){
            query += " WHERE";
        }
        query += where;
        query = query + " LIMIT " + offset + ", " + limit;
        cursor = getReadableDatabase().rawQuery(query, params.toArray(new String[0]));
        ArrayList<Book> books = new ArrayList<>();
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                books.add(buildFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return books;
    }
    public Cursor listAllBooks(Integer categoryId, String titleContains, int offset){
        return listAllBooks(categoryId, titleContains, false, offset);
    }
    public Cursor listAllBooks(Integer categoryId, String titleContains, boolean downloaded, int offset){
        Cursor cursor;
        ArrayList<String> arrayList = new ArrayList<>();
        boolean hasAnd = false;
        String query = "SELECT id as _id, * FROM " + TABLE_NAME;
        if(categoryId != null){
            arrayList.add(""+categoryId);
            query += " WHERE category_id=?";
            hasAnd = true;
        }
        if(titleContains != null && !titleContains.isEmpty()){
            arrayList.add('%'+titleContains+'%');
            if(categoryId != null) query += " AND";
            query += " title like ?";
            hasAnd = true;
        }
        if(downloaded){
            arrayList.add("100");
            if(hasAnd) query += " AND";
            query += " progress=?";
            hasAnd = true;
        }
        query += " LIMIT ?, ?";
        arrayList.add(""+offset);
        arrayList.add("20");

        cursor = getReadableDatabase().rawQuery(query, arrayList.toArray(new String[0]));

        return cursor;
    }
    public List<Book> filterBooks(Integer categoryId, String titleContains, int offset){
        return filterBooks(categoryId, titleContains, false, offset);
    }
    public List<Book> filterBooks(Integer categoryId, String titleContains, boolean downloaded, int offset){
        Cursor c = listAllBooks(categoryId, titleContains, downloaded, offset);
        ArrayList<Book> books = new ArrayList<>();
        if(c.moveToFirst()){
            while(!c.isAfterLast()){
                books.add(buildFromCursor(c));
                c.moveToNext();
            }
        }
        c.close();
        return books;
    }
    public void insertOrUpdateExcludeDownloadStat(Book model){
        if(model != null){
            if(model.getId() > 0) {
                SQLiteDatabase db = getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{"" + model.getId()});
                if (cursor.getCount() > 0) {
                    ContentValues contentValues = model.getContentValues();
                    contentValues.remove("download_id");
                    contentValues.remove("progress");
                    db.update(TABLE_NAME, contentValues, "id=?", new String[]{"" + model.getId()});
                } else {
                    db.insert(TABLE_NAME, "description", model.getContentValues());
                }
            }else{
                SQLiteDatabase db = getWritableDatabase();
                ContentValues contentValues = model.getContentValues();
                contentValues.remove("id");
                db.insert(TABLE_NAME, "description", contentValues);
            }
        }
    }
}
