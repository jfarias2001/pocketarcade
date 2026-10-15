package com.example.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pocketarcade.databinding.ActivityCentralBinding;

// Tela 2: menu com os quatro desafios
public class CentralActivity extends AppCompatActivity {

    private ActivityCentralBinding binding;
    private String apelido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCentralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(MainActivity.TAG_CICLO, "CentralActivity: onCreate");

        // validacao no destino: sem apelido valido nao tem como abrir a central
        apelido = getIntent().getStringExtra(MainActivity.EXTRA_APELIDO);
        if (apelido == null || apelido.trim().length() < MainActivity.TAMANHO_MINIMO_APELIDO) {
            Toast.makeText(this, R.string.erro_sem_apelido, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.textSaudacao.setText(getString(R.string.saudacao, apelido));

        // animacao XML de entrada, cada painel comeca um pouco depois do anterior
        animarEntrada(binding.painelScanner, 0);
        animarEntrada(binding.painelArena, 120);
        animarEntrada(binding.painelCofre, 240);
        animarEntrada(binding.painelRandom, 360);

        binding.botaoScanner.setOnClickListener(v -> abrirDesafio(ScannerActivity.class));
        binding.botaoArena.setOnClickListener(v -> abrirDesafio(ArenaActivity.class));
        binding.botaoCofre.setOnClickListener(v -> abrirDesafio(CofreActivity.class));
        binding.botaoRandom.setOnClickListener(v -> abrirDesafio(RandomizadorActivity.class));

        // finish() fecha a central e mostra a Tela 1 que ja estava na pilha
        binding.botaoVoltar.setOnClickListener(v -> finish());
    }

    private void animarEntrada(View painel, long atraso) {
        Animation animacao = AnimationUtils.loadAnimation(this, R.anim.entrada_painel);
        animacao.setStartOffset(atraso);
        painel.startAnimation(animacao);
    }

    private void abrirDesafio(Class<?> telaDesafio) {
        Intent intent = new Intent(this, telaDesafio);
        intent.putExtra(MainActivity.EXTRA_APELIDO, apelido);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MainActivity.TAG_CICLO, "CentralActivity: onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MainActivity.TAG_CICLO, "CentralActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(MainActivity.TAG_CICLO, "CentralActivity: onDestroy");
    }
}
