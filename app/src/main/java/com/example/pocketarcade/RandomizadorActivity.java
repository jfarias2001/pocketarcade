package com.example.pocketarcade;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pocketarcade.databinding.ActivityRandomizadorBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Tela 6: cadastro de palavras por categoria e sorteio
public class RandomizadorActivity extends AppCompatActivity {

    private static final int COLUNAS_GRADE = 2;

    private ActivityRandomizadorBinding binding;
    private final List<Item> itens = new ArrayList<>();
    private ItemAdapter adapter;
    private final Random random = new Random();

    private boolean modoGrade = false;
    private MediaPlayer somSorteio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRandomizadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(MainActivity.TAG_CICLO, "RandomizadorActivity: onCreate");

        String apelido = getIntent().getStringExtra(MainActivity.EXTRA_APELIDO);
        if (apelido == null) {
            Toast.makeText(this, R.string.erro_sem_apelido, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        binding.textJogador.setText(getString(R.string.label_jogador, apelido));

        somSorteio = MediaPlayer.create(this, R.raw.sorteio);

        carregarItensIniciais();
        adapter = new ItemAdapter(itens);
        binding.recyclerItens.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerItens.setAdapter(adapter);

        binding.botaoAdicionar.setOnClickListener(v -> adicionar());
        binding.botaoSortear.setOnClickListener(v -> sortear());
        binding.botaoLayout.setOnClickListener(v -> alternarLayout());
        binding.botaoVoltar.setOnClickListener(v -> finish());
        binding.botaoEncerrar.setOnClickListener(v -> encerrarSessao());
    }

    // alguns itens para a lista nao comecar vazia (fica so na memoria, sem banco de dados)
    private void carregarItensIniciais() {
        String[] categorias = getResources().getStringArray(R.array.categorias);
        // cada lista de exemplos esta na mesma ordem das categorias
        int[] exemplos = {R.array.exemplos_filmes, R.array.exemplos_comidas, R.array.exemplos_lugares, R.array.exemplos_desafios};

        for (int i = 0; i < categorias.length; i++) {
            for (String palavra : getResources().getStringArray(exemplos[i])) {
                itens.add(new Item(palavra, categorias[i]));
            }
        }
    }

    private void adicionar() {
        String valor = binding.editValor.getText().toString().trim();
        if (valor.isEmpty()) {
            binding.editValor.setError(getString(R.string.erro_valor_vazio));
            return;
        }
        String categoria = binding.spinnerCategoria.getSelectedItem().toString();

        itens.add(new Item(valor, categoria));
        int posicaoNova = itens.size() - 1;
        adapter.notifyItemInserted(posicaoNova);
        binding.recyclerItens.scrollToPosition(posicaoNova);

        binding.editValor.setText("");
    }

    private void sortear() {
        String categoria = binding.spinnerCategoria.getSelectedItem().toString();

        // separa so os itens da categoria escolhida
        List<Item> daCategoria = new ArrayList<>();
        for (Item item : itens) {
            if (item.getCategoria().equals(categoria)) {
                daCategoria.add(item);
            }
        }

        if (daCategoria.isEmpty()) {
            Toast.makeText(this, getString(R.string.aviso_categoria_vazia, categoria), Toast.LENGTH_SHORT).show();
            return;
        }

        Item sorteado = daCategoria.get(random.nextInt(daCategoria.size()));
        binding.textSorteado.setText(getString(R.string.random_sorteado, sorteado.getPalavra()));

        // animacao: o resultado gira no eixo X e aparece
        binding.textSorteado.setRotationX(90f);
        binding.textSorteado.setAlpha(0f);
        binding.textSorteado.animate().rotationX(0f).alpha(1f).setDuration(500);

        if (somSorteio.isPlaying()) {
            somSorteio.pause();
        }
        somSorteio.seekTo(0);
        somSorteio.start();
    }

    private void alternarLayout() {
        modoGrade = !modoGrade;
        if (modoGrade) {
            binding.recyclerItens.setLayoutManager(new GridLayoutManager(this, COLUNAS_GRADE));
            binding.botaoLayout.setText(R.string.botao_ver_lista);
        } else {
            binding.recyclerItens.setLayoutManager(new LinearLayoutManager(this));
            binding.botaoLayout.setText(R.string.botao_ver_grade);
        }
    }

    private void encerrarSessao() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.EXTRA_ULTIMO_MODULO, getString(R.string.painel_random_titulo));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MainActivity.TAG_CICLO, "RandomizadorActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (somSorteio != null && somSorteio.isPlaying()) {
            somSorteio.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MainActivity.TAG_CICLO, "RandomizadorActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (somSorteio != null) {
            somSorteio.release();
            somSorteio = null;
        }
        Log.d(MainActivity.TAG_CICLO, "RandomizadorActivity: onDestroy");
    }
}
