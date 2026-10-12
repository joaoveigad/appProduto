package com.seuapp.bancodados;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editNome;
    private EditText editPreco;
    private TextInputLayout layoutNome;
    private TextInputLayout layoutPreco;
    private Button botaoSalvar;
    private Button botaoEdicao;
    private LinearLayout painelEdicao;
    private Button botaoSubir;
    private Button botaoDescer;
    private Button botaoExcluir;
    private ListView listaProdutos;

    private ProdutoDbHelper dbHelper;
    private ProdutoAdapter adapter;
    private final List<String> itensLista = new ArrayList<>();
    private List<Produto> produtosAtuais = new ArrayList<>();

    private boolean modoEdicao = false;
    private int idSelecionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNome = findViewById(R.id.editNome);
        editPreco = findViewById(R.id.editPreco);
        layoutNome = findViewById(R.id.layoutNome);
        layoutPreco = findViewById(R.id.layoutPreco);
        botaoSalvar = findViewById(R.id.botaoSalvar);
        botaoEdicao = findViewById(R.id.botaoEdicao);
        painelEdicao = findViewById(R.id.painelEdicao);
        botaoSubir = findViewById(R.id.botaoSubir);
        botaoDescer = findViewById(R.id.botaoDescer);
        botaoExcluir = findViewById(R.id.botaoExcluir);
        listaProdutos = findViewById(R.id.listaProdutos);

        listaProdutos.setEmptyView(findViewById(R.id.textoListaVazia));

        dbHelper = new ProdutoDbHelper(this);

        adapter = new ProdutoAdapter(this, itensLista);
        listaProdutos.setAdapter(adapter);

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarOuAtualizar();
            }
        });

        botaoEdicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alternarModoEdicao();
            }
        });

        botaoSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moverSelecionado(-1);
            }
        });

        botaoDescer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moverSelecionado(1);
            }
        });

        botaoExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirSelecionado();
            }
        });

        listaProdutos.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                aoClicarItem(position);
            }
        });

        atualizarLista();
    }

    private void alternarModoEdicao() {
        modoEdicao = !modoEdicao;

        painelEdicao.setVisibility(modoEdicao ? View.VISIBLE : View.GONE);
        botaoEdicao.setText(modoEdicao
                ? R.string.botao_desabilitar_edicao
                : R.string.botao_habilitar_edicao);

        if (modoEdicao) {
            Toast.makeText(this, R.string.msg_modo_edicao_ativo, Toast.LENGTH_SHORT).show();
        } else {
            limparSelecao();
        }
    }

    private void aoClicarItem(int position) {
        if (!modoEdicao) {
            return;
        }

        Produto produto = produtosAtuais.get(position);

        if (produto.getId() == idSelecionado) {
            limparSelecao();
            return;
        }

        idSelecionado = produto.getId();
        adapter.setPosicaoSelecionada(position);

        editNome.setText(produto.getNome());
        editPreco.setText(String.format(Locale.getDefault(), "%.2f", produto.getPreco()));
        botaoSalvar.setText(R.string.botao_atualizar);
    }

    private void salvarOuAtualizar() {
        layoutNome.setError(null);
        layoutPreco.setError(null);

        String nome = editNome.getText().toString().trim();
        String precoTexto = editPreco.getText().toString().trim();

        if (nome.length() < 3) {
            layoutNome.setError(getString(R.string.erro_nome));
            return;
        }

        if (precoTexto.isEmpty()) {
            layoutPreco.setError(getString(R.string.erro_preco));
            return;
        }

        double preco;
        try {
            preco = Double.parseDouble(precoTexto);
        } catch (NumberFormatException e) {
            layoutPreco.setError(getString(R.string.erro_preco_numero));
            return;
        }

        if (preco <= 0) {
            layoutPreco.setError(getString(R.string.erro_preco));
            return;
        }

        if (modoEdicao && idSelecionado != -1) {
            dbHelper.atualizarProduto(idSelecionado, nome, preco);

            int id = idSelecionado;
            limparSelecao();
            atualizarLista();
            selecionarPorId(id);

            Toast.makeText(this, R.string.msg_atualizado, Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.inserirProduto(nome, preco);

        editNome.setText("");
        editPreco.setText("");
        editNome.requestFocus();

        Toast.makeText(this, R.string.msg_sucesso, Toast.LENGTH_SHORT).show();

        atualizarLista();
    }

    private void moverSelecionado(int direcao) {
        if (idSelecionado == -1) {
            Toast.makeText(this, R.string.msg_selecione, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean moveu = dbHelper.moverProduto(idSelecionado, direcao);
        if (!moveu) {
            Toast.makeText(this, R.string.msg_limite, Toast.LENGTH_SHORT).show();
            return;
        }

        int id = idSelecionado;
        atualizarLista();
        selecionarPorId(id);
    }

    private void excluirSelecionado() {
        if (idSelecionado == -1) {
            Toast.makeText(this, R.string.msg_selecione, Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.excluirProduto(idSelecionado);
        limparSelecao();

        Toast.makeText(this, R.string.msg_excluido, Toast.LENGTH_SHORT).show();

        atualizarLista();
    }

    private void atualizarLista() {
        produtosAtuais = dbHelper.listarProdutos();

        itensLista.clear();
        for (Produto p : produtosAtuais) {
            itensLista.add(String.format(
                    Locale.getDefault(),
                    "%s - R$ %.2f",
                    p.getNome(),
                    p.getPreco()
            ));
        }

        adapter.notifyDataSetChanged();
    }

    private void selecionarPorId(int id) {
        for (int i = 0; i < produtosAtuais.size(); i++) {
            if (produtosAtuais.get(i).getId() == id) {
                idSelecionado = id;
                adapter.setPosicaoSelecionada(i);
                editNome.setText(produtosAtuais.get(i).getNome());
                editPreco.setText(String.format(
                        Locale.getDefault(), "%.2f", produtosAtuais.get(i).getPreco()));
                botaoSalvar.setText(R.string.botao_atualizar);
                return;
            }
        }

        limparSelecao();
    }

    private void limparSelecao() {
        idSelecionado = -1;
        adapter.setPosicaoSelecionada(-1);

        botaoSalvar.setText(R.string.botao_salvar);

        editNome.setText("");
        editPreco.setText("");
        layoutNome.setError(null);
        layoutPreco.setError(null);
    }
}
