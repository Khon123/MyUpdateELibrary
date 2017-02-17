package android.camant.com.elibraryandroid.db;

import android.camant.com.elibraryandroid.models.Book;
import android.camant.com.elibraryandroid.models.Category;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sreng on 1/15/2017.
 */

public class CategoryDbHelper extends BaseDbHelper {
    private static final String TABLE_NAME = "categories";
    public CategoryDbHelper(Context context) {
        super(context, TABLE_NAME+".db");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        super.onCreate(sqLiteDatabase);

        String query = "CREATE TABLE " + TABLE_NAME + "(" + getCreateTableQuery(Category.class) + ")";
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
    public void insertAll(List<Category> staffs){
        if(staffs != null && staffs.size() > 0){
            SQLiteDatabase db = getWritableDatabase();
            for(Category staff:staffs){
                db.insert(TABLE_NAME, "full_name", staff.getContentValues());
            }
        }
    }
    public void insert(Category staff){
        long id = getWritableDatabase().insert(TABLE_NAME, "full_name", staff.getContentValues());
        if(id >= 0) staff.setId((int)id);
    }

    public void insertOrUpdate(Category staff){
        super.insertOrUpdate(staff, TABLE_NAME);
    }

    public int getCount(){
        Cursor cursor = getReadableDatabase().rawQuery("SELECT count(*) FROM " + TABLE_NAME, null);
        if(cursor.moveToFirst()) return cursor.getInt(0);
        return 0;
    }

    public Category getById(int id) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{"" + id});
        Category staff = null;
        if(cursor.moveToFirst()){
            staff = buildFromCursor(cursor);
        }
        cursor.close();
        return staff;
    }
    private Category buildFromCursor(Cursor cursor){
        Category s = new Category();
        s.setId(cursor.getInt(cursor.getColumnIndex("id")));
        s.setModified(cursor.getString(cursor.getColumnIndex("modified")));
        s.setCreated(cursor.getString(cursor.getColumnIndex("created")));
        s.setName(cursor.getString(cursor.getColumnIndex("name")));
        s.setOrder(cursor.getInt(cursor.getColumnIndex("order")));
        s.setBooks_count(cursor.getInt(cursor.getColumnIndex("books_count")));
        return s;
    }
    public ArrayList<Category> listCategories(int offset, int limit){
        return listCategories(null, offset, limit);
    }

    public ArrayList<Category> listDownloadedCategories(String key, int offset, int limit){
        Cursor cursor;
        String[] params = {};
        if(key == null || key.isEmpty()){
            cursor = getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME + " LIMIT "+offset+", "+limit, params);
        }else{
            params = new String[]{"%"+key+"%"};
            cursor = getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME + " WHERE name LIKE ? LIMIT "+offset+", "+limit, params);
        }
        ArrayList<Category> categories = new ArrayList<>();
        if(cursor.moveToFirst()){
            BookDbHelper bookDbHelper = new BookDbHelper(context);
            Cursor c;
            while(!cursor.isAfterLast()){
                c=bookDbHelper.listAllBooks(cursor.getInt(cursor.getColumnIndex("id")), null, true, 0);
                if(c.moveToFirst()) {
                    categories.add(buildFromCursor(cursor));
                }
                c.close();
                cursor.moveToNext();
            }
        }
        cursor.close();
        return categories;
    }
    public ArrayList<Category> listCategories(String key, int offset, int limit){
        Cursor cursor;
        if(key == null || key.isEmpty()){
            cursor = getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME + " LIMIT "+offset+", "+limit, null);
        }else{
            cursor = getReadableDatabase().rawQuery("SELECT * FROM "+ TABLE_NAME + " WHERE name LIKE ? LIMIT "+offset+", "+limit, new String[]{"%"+key+"%"});
        }
        ArrayList<Category> categories = new ArrayList<>();
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                categories.add(buildFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return categories;
    }
}
