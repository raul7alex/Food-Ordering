package com.progandroid.foodordering.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.progandroid.foodordering.entities.FavoriteObject;

import java.util.ArrayList;
import java.util.List;

public class Query extends DatabaseObject{

    public Query(Context context) {
        super(context);
    }

    public List<FavoriteObject> listFavoriteMenu(){
        List<FavoriteObject> favoriteObject = new ArrayList<FavoriteObject>();
        String query = "select * from favorite";
        Cursor cursor = this.getDbConnection().rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
                float price = cursor.getFloat(cursor.getColumnIndexOrThrow("price"));
                favoriteObject.add(new FavoriteObject(id, name, path, price));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteObject;
    }

    public void addFavoriteMenuItem(String name, String path, float price){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("path", path);
        values.put("price", price);
        getDbConnection().insert("favorite", null, values);
    }

    public boolean deleteFavoriteMenuItem(int id){
        return getDbConnection().delete("favorite", "id = " + id, null) > 0;
    }
}
