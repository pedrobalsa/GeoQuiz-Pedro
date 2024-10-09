package com.example.geoquiz_v4_sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*
  Modelo de projeto para a Atividade 1.
  Será preciso adicionar o cadastro das respostas do usuário ao Quiz, conforme
  definido no Canvas.

  GitHub: https://github.com/udofritzke/GeoQuiz
 */

public class MainActivity extends AppCompatActivity {
    private Button mBotaoVerdadeiro;
    private Button mBotaoFalso;
    private Button mBotaoProximo;
    private Button mBotaoMostra;
    private Button mBotaoDeleta;

    private Button mBotaoCola;

    private TextView mTextViewQuestao;
    private TextView mTextViewQuestoesArmazenadas;

    private static final String TAG = "QuizActivity";
    private static final String CHAVE_INDICE = "INDICE";
    private static final int CODIGO_REQUISICAO_COLA = 0;

    private Questao[] mBancoDeQuestoes = new Questao[]{
            new Questao(R.string.questao_suez, true),
            new Questao(R.string.questao_alemanha, false)
    };

    QuestaoDB mQuestoesDb;

    private int mIndiceAtual = 0;

    private boolean mEhColador;

    private RespostaDB mRespostaDB;

    @Override
    protected void onCreate(Bundle instanciaSalva) {
        super.onCreate(instanciaSalva);
        setContentView(R.layout.activity_main);
        //Log.d(TAG, "onCreate()");
        mRespostaDB = new RespostaDB(this);
        mRespostaDB.deleteAllRespostas();
        if (instanciaSalva != null) {
            mIndiceAtual = instanciaSalva.getInt(CHAVE_INDICE, 0);
        }

        mTextViewQuestao = (TextView) findViewById(R.id.view_texto_da_questao);
        atualizaQuestao();

        mBotaoVerdadeiro = (Button) findViewById(R.id.botao_verdadeiro);
        // utilização de classe anônima interna
        mBotaoVerdadeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(true);
            }
        });

        mBotaoFalso = (Button) findViewById(R.id.botao_falso);
        mBotaoFalso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaResposta(false);
            }
        });
        mBotaoProximo = (Button) findViewById(R.id.botao_proximo);
        mBotaoProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIndiceAtual = (mIndiceAtual + 1) % mBancoDeQuestoes.length;
                mEhColador = false;
                atualizaQuestao();
            }
        });

        mBotaoCola = (Button) findViewById(R.id.botao_cola);
        mBotaoCola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inicia ColaActivity
                // Intent intent = new Intent(MainActivity.this, ColaActivity.class);
                boolean respostaEVerdadeira = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
                Intent intent = ColaActivity.novoIntent(MainActivity.this, respostaEVerdadeira);
                //startActivity(intent);
                startActivityForResult(intent, CODIGO_REQUISICAO_COLA);
            }
        });

        //Cursor cur = mQuestoesDb.queryQuestao ("_id = ?", val);////(null, null);
        //String [] val = {"1"};
        mBotaoMostra = (Button) findViewById(R.id.botao_mostra_questoes);
        mBotaoMostra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        /*
          Acesso ao SQLite para exibir as respostas armazenadas
        */
                if (mRespostaDB == null) return;

                if (mTextViewQuestoesArmazenadas == null) {
                    mTextViewQuestoesArmazenadas = (TextView) findViewById(R.id.texto_questoes_a_apresentar);
                } else {
                    mTextViewQuestoesArmazenadas.setText(""); // Limpar o conteúdo antes de mostrar novas respostas
                }

                Cursor cursor = mRespostaDB.getAllRespostas(); // Obter todas as respostas

                if (cursor != null) {
                    if (cursor.getCount() == 0) {
                        mTextViewQuestoesArmazenadas.setText("Nenhuma resposta registrada.");
                        Log.i("MSGS", "Nenhuma resposta encontrada.");
                    } else {
                        StringBuilder builder = new StringBuilder();
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            // Recuperar os dados da resposta
                            String uuidQuestao = cursor.getString(cursor.getColumnIndex(RespostasDbSchema.RespostasTable.Cols.UUID_QUESTAO));
                            int respostaCorreta = cursor.getInt(cursor.getColumnIndex(RespostasDbSchema.RespostasTable.Cols.RESPOSTA_CORRETA));
                            int respostaOferecida = cursor.getInt(cursor.getColumnIndex(RespostasDbSchema.RespostasTable.Cols.RESPOSTA_OFERECIDA));
                            int colou = cursor.getInt(cursor.getColumnIndex(RespostasDbSchema.RespostasTable.Cols.COLOU));

                            // Montar a string para exibição
                            String textoResposta = "Questão: " + uuidQuestao +
                                    ", Correto: " + (respostaCorreta == 1 ? "Sim" : "Não") +
                                    ", Resposta: " + (respostaOferecida == 1 ? "Verdadeiro" : "Falso") +
                                    ", Colou: " + (colou == 1 ? "Sim" : "Não");

                            // Exibir as respostas
                            builder.append(textoResposta).append("\n");

                            cursor.moveToNext();
                        }

                        // Adicionar o conteúdo ao TextView
                        mTextViewQuestoesArmazenadas.setText(builder.toString());
                    }

                    cursor.close(); // Fechar o cursor após o uso
                } else {
                    Log.i("MSGS", "Cursor nulo!");
                }
            }
        });


        mBotaoDeleta = (Button) findViewById(R.id.botao_deleta);
        mBotaoDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRespostaDB != null) {
                    // Deletar todas as respostas
                    mRespostaDB.deleteAllRespostas();

                    // Feedback visual
                    mTextViewQuestoesArmazenadas.setText("Respostas deletadas.");
                    Toast.makeText(MainActivity.this, "Respostas deletadas", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void atualizaQuestao() {
        int questao = mBancoDeQuestoes[mIndiceAtual].getTextoRespostaId();
        mTextViewQuestao.setText(questao);
    }

    private void verificaResposta(boolean respostaPressionada) {
        boolean respostaCorreta = mBancoDeQuestoes[mIndiceAtual].isRespostaCorreta();
        int idMensagemResposta = 0;

        mRespostaDB.addResposta(mBancoDeQuestoes[mIndiceAtual].getId().toString(), respostaCorreta, respostaPressionada, mEhColador);

        if (mEhColador) {
            idMensagemResposta = R.string.toast_julgamento;
        } else {
            if (respostaPressionada == respostaCorreta) {
                idMensagemResposta = R.string.toast_correto;
            } else
                idMensagemResposta = R.string.toast_incorreto;
        }
        Toast.makeText(this, idMensagemResposta, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle instanciaSalva) {
        super.onSaveInstanceState(instanciaSalva);
        Log.i(TAG, "onSaveInstanceState()");
        instanciaSalva.putInt(CHAVE_INDICE, mIndiceAtual);
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent dados) {
        if (codigoResultado != Activity.RESULT_OK) {
            return;
        }
        if (codigoRequisicao == CODIGO_REQUISICAO_COLA) {
            if (dados == null) {
                return;
            }
            mEhColador = ColaActivity.foiMostradaResposta(dados);
        }
    }
}