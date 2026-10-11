package com.seuapp.bancodados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ProdutoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "produtos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "produtos";

    public static final String COL_ID = "id";
    public static final String COL_NOME = "nome";
    public static final String COL_PRECO = "preco";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOME + " TEXT NOT NULL, " +
                    COL_PRECO + " REAL NOT NULL)";

    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public ProdutoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    public long inserirProduto(String nome, double preco) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COL_NOME, nome);
        valores.put(COL_PRECO, preco);

        long id = db.insert(TABLE_NAME, null, valores);
        db.close();

        return id;
    }

    public List<Produto> listarProdutos() {
        List<Produto> produtos = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            int idxId = cursor.getColumnIndex(COL_ID);
            int idxNome = cursor.getColumnIndex(COL_NOME);
            int idxPreco = cursor.getColumnIndex(COL_PRECO);

            do {
                int id = cursor.getInt(idxId);
                String nome = cursor.getString(idxNome);
                double preco = cursor.getDouble(idxPreco);
                produtos.add(new Produto(id, nome, preco));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return produtos;
    }
}
