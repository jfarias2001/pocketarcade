package com.example.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pocketarcade.databinding.ActivityScannerBinding;

// Tela 3: analisa um texto digitado
public class ScannerActivity extends AppCompatActivity {

    private static final int TAMANHO_MINIMO = 3;
    private static final String VOGAIS = "aeiouáéíóúâêôãõàü";

    private ActivityScannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(MainActivity.TAG_CICLO, "ScannerActivity: onCreate");

        String apelido = getIntent().getStringExtra(MainActivity.EXTRA_APELIDO);
        if (apelido == null) {
            Toast.makeText(this, R.string.erro_sem_apelido, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        binding.textJogador.setText(getString(R.string.label_jogador, apelido));

        binding.botaoAnalisar.setOnClickListener(v -> analisar());
        binding.botaoLimpar.setOnClickListener(v -> limpar());
        binding.botaoVoltar.setOnClickListener(v -> finish());
        binding.botaoEncerrar.setOnClickListener(v -> encerrarSessao());
    }

    private void analisar() {
        String texto = binding.editMensagem.getText().toString().trim();

        if (texto.isEmpty()) {
            binding.editMensagem.setError(getString(R.string.erro_mensagem_vazia));
            binding.painelResultado.setVisibility(View.GONE);
            return;
        }
        if (texto.length() < TAMANHO_MINIMO) {
            binding.editMensagem.setError(getString(R.string.erro_mensagem_curta));
            binding.painelResultado.setVisibility(View.GONE);
            return;
        }

        binding.textCaracteres.setText(String.valueOf(contarSemEspacos(texto)));
        binding.textPalavras.setText(String.valueOf(contarPalavras(texto)));
        binding.textVogais.setText(String.valueOf(contarVogais(texto)));
        binding.textInvertido.setText(inverter(texto));
        binding.textPalindromo.setText(ehPalindromo(texto) ? R.string.sim : R.string.nao);

        mostrarResultado();
    }

    private int contarSemEspacos(String texto) {
        int total = 0;
        for (int i = 0; i < texto.length(); i++) {
            if (!Character.isWhitespace(texto.charAt(i))) {
                total++;
            }
        }
        return total;
    }

    private int contarPalavras(String texto) {
        // \\s+ separa por um ou mais espacos seguidos
        return texto.split("\\s+").length;
    }

    private int contarVogais(String texto) {
        int total = 0;
        String minusculo = texto.toLowerCase();
        for (int i = 0; i < minusculo.length(); i++) {
            if (VOGAIS.indexOf(minusculo.charAt(i)) >= 0) {
                total++;
            }
        }
        return total;
    }

    private String inverter(String texto) {
        return new StringBuilder(texto).reverse().toString();
    }

    private boolean ehPalindromo(String texto) {
        // ignora espacos e maiusculas: "A sacada da casa" vira "asacadadacasa"
        String limpo = texto.replaceAll("\\s+", "").toLowerCase();
        return limpo.equals(inverter(limpo));
    }

    private void mostrarResultado() {
        // ViewPropertyAnimator: o painel aparece descendo e ficando visivel
        binding.painelResultado.setVisibility(View.VISIBLE);
        binding.painelResultado.setAlpha(0f);
        binding.painelResultado.setTranslationY(-30f);
        binding.painelResultado.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400);
    }

    private void limpar() {
        binding.editMensagem.setText("");
        binding.editMensagem.setError(null);
        binding.painelResultado.setVisibility(View.GONE);
    }

    private void encerrarSessao() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.EXTRA_ULTIMO_MODULO, getString(R.string.scanner_titulo));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MainActivity.TAG_CICLO, "ScannerActivity: onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MainActivity.TAG_CICLO, "ScannerActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(MainActivity.TAG_CICLO, "ScannerActivity: onDestroy");
    }
}
