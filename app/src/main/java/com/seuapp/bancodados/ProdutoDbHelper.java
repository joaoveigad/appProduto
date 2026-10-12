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
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "produtos";

    public static final String COL_ID = "id";
    public static final String COL_NOME = "nome";
    public static final String COL_PRECO = "preco";
    public static final String COL_ORDEM = "ordem";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOME + " TEXT NOT NULL, " +
                    COL_PRECO + " REAL NOT NULL, " +
                    COL_ORDEM + " INTEGER NOT NULL DEFAULT 0)";

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
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME +
                    " ADD COLUMN " + COL_ORDEM + " INTEGER NOT NULL DEFAULT 0");
            db.execSQL("UPDATE " + TABLE_NAME +
                    " SET " + COL_ORDEM + " = " + COL_ID);
        } else {
            db.execSQL(SQL_DROP_TABLE);
            onCreate(db);
        }
    }

    public long inserirProduto(String nome, double preco) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COL_NOME, nome);
        valores.put(COL_PRECO, preco);
        valores.put(COL_ORDEM, proximaOrdem(db));

        long id = db.insert(TABLE_NAME, null, valores);
        db.close();

        return id;
    }

    public int atualizarProduto(int id, String nome, double preco) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COL_NOME, nome);
        valores.put(COL_PRECO, preco);

        int linhas = db.update(
                TABLE_NAME,
                valores,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();

        return linhas;
    }

    public int excluirProduto(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int linhas = db.delete(
                TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();

        return linhas;
    }

    public boolean moverProduto(int id, int direcao) {
        SQLiteDatabase db = getWritableDatabase();

        List<Integer> ids = new ArrayList<>();
        List<Integer> ordens = new ArrayList<>();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COL_ID, COL_ORDEM},
                null,
                null,
                null,
                null,
                COL_ORDEM + " ASC"
        );

        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(0));
            ordens.add(cursor.getInt(1));
        }
        cursor.close();

        int indice = ids.indexOf(id);
        if (indice == -1) {
            db.close();
            return false;
        }

        int alvo = indice + direcao;
        if (alvo < 0 || alvo >= ids.size()) {
            db.close();
            return false;
        }

        trocarOrdem(db, ids.get(indice), ordens.get(alvo));
        trocarOrdem(db, ids.get(alvo), ordens.get(indice));

        db.close();
        return true;
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
                COL_ORDEM + " ASC"
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

    private int proximaOrdem(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(
                "SELECT IFNULL(MAX(" + COL_ORDEM + "), -1) + 1 FROM " + TABLE_NAME,
                null
        );

        int ordem = 0;
        if (cursor.moveToFirst()) {
            ordem = cursor.getInt(0);
        }
        cursor.close();

        return ordem;
    }

    private void trocarOrdem(SQLiteDatabase db, int id, int ordem) {
        ContentValues valores = new ContentValues();
        valores.put(COL_ORDEM, ordem);
        db.update(
                TABLE_NAME,
                valores,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
}
