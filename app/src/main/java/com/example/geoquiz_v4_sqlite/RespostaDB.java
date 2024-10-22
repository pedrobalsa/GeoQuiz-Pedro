package com.example.geoquiz_v4_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RespostaDB {
    private SQLiteDatabase mDatabase;

    public RespostaDB(Context context) {
        // Agora usando o RespostasDBHelper
        mDatabase = new RespostasDBHelper(context).getWritableDatabase();
    }

    public void addResposta(String uuidQuestao, boolean respostaCorreta, boolean respostaOferecida, boolean colou) {
        ContentValues values = new ContentValues();
        values.put(RespostasDbSchema.RespostasTable.Cols.UUID_QUESTAO, uuidQuestao);
        values.put(RespostasDbSchema.RespostasTable.Cols.RESPOSTA_CORRETA, respostaCorreta ? 1 : 0);
        values.put(RespostasDbSchema.RespostasTable.Cols.RESPOSTA_OFERECIDA, respostaOferecida ? 1 : 0);
        values.put(RespostasDbSchema.RespostasTable.Cols.COLOU, colou ? 1 : 0);

        mDatabase.insert(RespostasDbSchema.RespostasTable.NAME, null, values);
    }

    public Cursor getAllRespostas() {
        return mDatabase.query(
                RespostasDbSchema.RespostasTable.NAME,
                null, null, null, null, null, null
        );
    }

    public void deleteAllRespostas() {
        // Deleta todas as respostas da tabela
        mDatabase.delete(RespostasDbSchema.RespostasTable.NAME, null, null);
    }

}
