package com.example.geoquiz_v4_sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RespostasDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "respostas.db";

    public RespostasDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criar a tabela 'respostas'
        db.execSQL("create table " + RespostasDbSchema.RespostasTable.NAME + "(" +
                "uuid_questao text, " +
                "resposta_correta integer, " +
                "resposta_oferecida integer, " +
                "colou integer" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Caso a tabela já exista, removê-la e criar novamente (apenas para desenvolvimento)
        db.execSQL("DROP TABLE IF EXISTS " + RespostasDbSchema.RespostasTable.NAME);
        onCreate(db);
    }
}
