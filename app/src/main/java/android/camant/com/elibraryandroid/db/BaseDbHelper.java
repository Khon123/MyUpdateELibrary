package android.camant.com.elibraryandroid.db;

import android.camant.com.elibraryandroid.models.BaseModel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by sreng on 1/15/2017.
 */

public class BaseDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 6;
    protected Context context;
    public final static String DATABASE_FILE_PATH = Environment.getExternalStoragePublicDirectory("CamAntELibrary").getAbsolutePath();
    public final static String BOOK_FILES_PATH = DATABASE_FILE_PATH + "/book_files/";
    public BaseDbHelper(Context context, String name_not_full_path){
        super(context, DATABASE_FILE_PATH + "/" + name_not_full_path, null, VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    protected String getCreateTableQuery(Class clazz){
        Method[] methods = clazz.getMethods();
        String methodName;
        Field field;
        String query = "";
        Class parameterTypes[];
        Class type;
        int a=0;
        for(Method method:methods){
            methodName = method.getName();
            if(methodName.startsWith("set")){
                methodName = methodName.substring(3);
                methodName = methodName.toLowerCase().charAt(0) + methodName.substring(1);
                parameterTypes = method.getParameterTypes();
                if(parameterTypes == null || parameterTypes.length == 0 || parameterTypes.length > 1) continue;
                type = parameterTypes[0];
                if(type.equals(String.class)) {
                    query += ",`" + methodName + "` TEXT";
                }else if(type.equals(Integer.class)
                        || type.equals(Integer.TYPE)
                        || type.equals(Long.TYPE)
                        || type.equals(Short.TYPE)
                        || type.equals(Byte.TYPE)
                        || type.equals(Long.class)
                        || type.equals(Short.class)
                        || type.equals(Byte.class)) {
                    query += ",`" + methodName + "` INTEGER";
                }else if(type.equals(Float.class)
                        || type.equals(Double.class)
                        || type.equals(Float.TYPE)
                        || type.equals(Double.TYPE)) {
                    query += ",`" + methodName + "` REAL";
                }else{
                    Log.e("Staff", methodName+type);
                    continue;
                }
                if(methodName.equals("id")){
                    query += " PRIMARY KEY";
                }
            }
        }
        query = query.substring(1);
        return query;
    }
    protected void insertOrUpdate(BaseModel model, String tableName){
        if(model != null){
            if(model.getId() > 0) {
                SQLiteDatabase db = getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE id=?", new String[]{"" + model.getId()});
                if (cursor.getCount() > 0) {
                    db.update(tableName, model.getContentValues(), "id=?", new String[]{"" + model.getId()});
                } else {
                    db.insert(tableName, "description", model.getContentValues());
                }
            }else{
                SQLiteDatabase db = getWritableDatabase();
                ContentValues contentValues = model.getContentValues();
                contentValues.remove("id");
                db.insert(tableName, "description", contentValues);
            }
        }
    }
}
