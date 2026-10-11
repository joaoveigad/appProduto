package com.seuapp.bancodados;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private ListView listaProdutos;

    private ProdutoDbHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private final List<String> itensLista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNome = findViewById(R.id.editNome);
        editPreco = findViewById(R.id.editPreco);
        layoutNome = findViewById(R.id.layoutNome);
        layoutPreco = findViewById(R.id.layoutPreco);
        botaoSalvar = findViewById(R.id.botaoSalvar);
        listaProdutos = findViewById(R.id.listaProdutos);

        listaProdutos.setEmptyView(findViewById(R.id.textoListaVazia));

        dbHelper = new ProdutoDbHelper(this);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itensLista
        );
        listaProdutos.setAdapter(adapter);

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarProduto();
            }
        });

        atualizarLista();
    }

    private void salvarProduto() {
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

        dbHelper.inserirProduto(nome, preco);

        editNome.setText("");
        editPreco.setText("");
        editNome.requestFocus();

        Toast.makeText(this, R.string.msg_sucesso, Toast.LENGTH_SHORT).show();

        atualizarLista();
    }

    private void atualizarLista() {
        List<Produto> produtos = dbHelper.listarProdutos();

        itensLista.clear();
        for (Produto p : produtos) {
            itensLista.add(String.format(
                    Locale.getDefault(),
                    "%s - R$ %.2f",
                    p.getNome(),
                    p.getPreco()
            ));
        }

        adapter.notifyDataSetChanged();
    }
}
