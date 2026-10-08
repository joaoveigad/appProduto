package com.seuapp.bancodados;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editNome;
    private EditText editPreco;
    private Button botaoSalvar;
    private ListView listaProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNome = findViewById(R.id.editNome);
        editPreco = findViewById(R.id.editPreco);
        botaoSalvar = findViewById(R.id.botaoSalvar);
        listaProdutos = findViewById(R.id.listaProdutos);

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        MainActivity.this,
                        R.string.msg_etapa_interface,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
