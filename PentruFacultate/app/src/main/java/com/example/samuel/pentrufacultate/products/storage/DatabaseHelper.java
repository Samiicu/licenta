package com.example.samuel.pentrufacultate.products.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.samuel.pentrufacultate.products.models.CatalogProduct;
import com.example.samuel.pentrufacultate.products.models.CatalogProducts;

import java.util.ArrayList;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by Parsania Hardik on 11/01/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "products_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PRODUCTS = "products";
    private static final String PRODUCT_UID = "produc_id";
    private static final String PRODUCT_NAME = "product_name";
    String SELECT_ALL_PRODUCTS_QUERRY = "SELECT  * FROM " + TABLE_PRODUCTS;
    /*CREATE TABLE students ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone_number TEXT......);*/

    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE "
            + TABLE_PRODUCTS + "(" + PRODUCT_UID
            + " TEXT PRIMARY KEY," + PRODUCT_NAME + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("table", CREATE_TABLE_STUDENTS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STUDENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_PRODUCTS + "'");
        onCreate(db);
    }

    public void addProductDetail(String product_name, String product_uid) {
        SQLiteDatabase db = getWritableDatabase();
        // Creating content values
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, product_name);
        values.put(PRODUCT_UID, product_uid);
        // insert row in students table
        db.insertWithOnConflict(TABLE_PRODUCTS, null, values, CONFLICT_REPLACE);
    }


    public void addBulkOfProducts(List<CatalogProduct> productsList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (CatalogProduct product : productsList) {
                Log.i("Saving: -> ", product.getId() + " -- " + product.getName());
                values.put(PRODUCT_NAME, product.getName());
                values.put(PRODUCT_UID, product.getId());
                db.insertWithOnConflict(TABLE_PRODUCTS, null, values, CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<String> getAllProducts() {
        ArrayList<String> studentsArrayList = new ArrayList<String>();
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_ALL_PRODUCTS_QUERRY, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                name = c.getString(c.getColumnIndex(PRODUCT_NAME));
                // adding to Students list
                studentsArrayList.add(name);
            } while (c.moveToNext());
            Log.d("array", studentsArrayList.toString());
        }
        c.close();
        return studentsArrayList;
    }

    public CatalogProduct getProductByName(String productName) {
        CatalogProduct catalogProduct = new CatalogProduct();
        catalogProduct.setName(productName);
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_PRODUCTS + " where "+PRODUCT_NAME+" =?";
        Cursor c = db.rawQuery(query, new String[]{productName});
        if (c != null&& c.getCount()!=0) {
            c.moveToFirst();
            catalogProduct.setId(c.getString(c.getColumnIndex(PRODUCT_UID)));
            c.close();
        } else {
            catalogProduct.setId("0");
        }
        return catalogProduct;
    }

    public int getNumberOfProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(SELECT_ALL_PRODUCTS_QUERRY, null);
        int count = c.getCount();
        c.close();
        return count;
    }
}