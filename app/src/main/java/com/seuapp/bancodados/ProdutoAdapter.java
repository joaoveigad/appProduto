package com.seuapp.bancodados;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class ProdutoAdapter extends ArrayAdapter<String> {

    private int posicaoSelecionada = -1;

    public ProdutoAdapter(Context context, List<String> itens) {
        super(context, android.R.layout.simple_list_item_1, itens);
    }

    public void setPosicaoSelecionada(int posicao) {
        this.posicaoSelecionada = posicao;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        if (position == posicaoSelecionada) {
            view.setBackgroundColor(getContext().getColor(R.color.cor_selecao));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }
}
