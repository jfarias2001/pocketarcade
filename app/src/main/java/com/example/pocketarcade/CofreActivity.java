package com.example.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pocketarcade.databinding.ActivityCofreBinding;
import com.example.pocketarcade.databinding.ItemTentativaBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Tela 5: descobrir um codigo de 4 digitos diferentes
public class CofreActivity extends AppCompatActivity {

    private static final int TAMANHO_CODIGO = 4;
    private static final int MAXIMO_TENTATIVAS = 8;

    private ActivityCofreBinding binding;

    private String codigoSecreto;
    private int tentativasUsadas;
    private long inicioPartida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCofreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(MainActivity.TAG_CICLO, "CofreActivity: onCreate");

        String apelido = getIntent().getStringExtra(MainActivity.EXTRA_APELIDO);
        if (apelido == null) {
            Toast.makeText(this, R.string.erro_sem_apelido, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        binding.textJogador.setText(getString(R.string.label_jogador, apelido));

        binding.botaoTentar.setOnClickListener(v -> tentar());
        binding.botaoNovaPartida.setOnClickListener(v -> novaPartida());
        binding.botaoVoltar.setOnClickListener(v -> finish());
        binding.botaoEncerrar.setOnClickListener(v -> encerrarSessao());

        novaPartida();
    }

    private void novaPartida() {
        codigoSecreto = gerarCodigo();
        tentativasUsadas = 0;
        // guarda a hora de inicio; o tempo so e calculado no final (sem cronometro rodando)
        inicioPartida = System.currentTimeMillis();

        binding.textCodigo.setText(R.string.cofre_oculto);
        binding.listaHistorico.removeAllViews();
        binding.textResultado.setVisibility(View.GONE);
        binding.botaoNovaPartida.setVisibility(View.GONE);
        binding.editPalpite.setText("");
        binding.editPalpite.setEnabled(true);
        binding.botaoTentar.setEnabled(true);
        atualizarTentativas();

        Log.d("Cofre", "codigo sorteado: " + codigoSecreto);
    }

    // embaralha os digitos de 0 a 9 e pega os 4 primeiros, assim nunca repete
    private String gerarCodigo() {
        List<Integer> digitos = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            digitos.add(i);
        }
        Collections.shuffle(digitos);

        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            codigo.append(digitos.get(i));
        }
        return codigo.toString();
    }

    private void tentar() {
        String palpite = binding.editPalpite.getText().toString().trim();

        if (!palpiteValido(palpite)) {
            return;
        }

        tentativasUsadas++;
        atualizarTentativas();
        adicionarAoHistorico(palpite);
        binding.editPalpite.setText("");

        if (palpite.equals(codigoSecreto)) {
            terminarPartida(true);
        } else if (tentativasUsadas == MAXIMO_TENTATIVAS) {
            terminarPartida(false);
        }
    }

    private boolean palpiteValido(String palpite) {
        if (palpite.isEmpty()) {
            binding.editPalpite.setError(getString(R.string.erro_palpite_vazio));
            return false;
        }
        if (palpite.length() != TAMANHO_CODIGO || !palpite.matches("[0-9]+")) {
            binding.editPalpite.setError(getString(R.string.erro_palpite_tamanho));
            return false;
        }
        if (temDigitoRepetido(palpite)) {
            binding.editPalpite.setError(getString(R.string.erro_palpite_repetido));
            return false;
        }
        return true;
    }

    private boolean temDigitoRepetido(String palpite) {
        for (int i = 0; i < palpite.length(); i++) {
            // se o digito aparece de novo mais para frente, esta repetido
            if (palpite.indexOf(palpite.charAt(i), i + 1) >= 0) {
                return true;
            }
        }
        return false;
    }

    private void adicionarAoHistorico(String palpite) {
        // infla o item_tentativa.xml com View Binding e ja coloca dentro da listaHistorico
        ItemTentativaBinding linha = ItemTentativaBinding.inflate(getLayoutInflater(), binding.listaHistorico, true);
        linha.textNumero.setText(getString(R.string.numero_tentativa, tentativasUsadas));

        TextView[] caixas = {linha.digito1, linha.digito2, linha.digito3, linha.digito4};
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            char digito = palpite.charAt(i);
            caixas[i].setText(String.valueOf(digito));
            caixas[i].setBackgroundColor(ContextCompat.getColor(this, corDaPista(digito, i)));
        }
    }

    // regra parecida com a do jogo Termo
    private int corDaPista(char digito, int posicao) {
        if (codigoSecreto.charAt(posicao) == digito) {
            return R.color.pista_verde;
        }
        if (codigoSecreto.indexOf(digito) >= 0) {
            return R.color.pista_amarelo;
        }
        return R.color.pista_cinza;
    }

    private void atualizarTentativas() {
        binding.textTentativas.setText(getString(R.string.tentativas, tentativasUsadas, MAXIMO_TENTATIVAS));
    }

    private void terminarPartida(boolean venceu) {
        long segundos = (System.currentTimeMillis() - inicioPartida) / 1000;

        binding.editPalpite.setEnabled(false);
        binding.botaoTentar.setEnabled(false);
        binding.textCodigo.setText(codigoSecreto);
        binding.textResultado.setVisibility(View.VISIBLE);
        binding.botaoNovaPartida.setVisibility(View.VISIBLE);

        if (venceu) {
            binding.textResultado.setText(getString(R.string.cofre_vitoria, tentativasUsadas, (int) segundos));
            binding.textResultado.setTextColor(ContextCompat.getColor(this, R.color.cor_vitoria));
            // sucesso: o texto cresce
            binding.textResultado.setScaleX(0.3f);
            binding.textResultado.setScaleY(0.3f);
            // o interpolator precisa ser trocado aqui, senao fica o CycleInterpolator da derrota anterior
            binding.textResultado.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(500);
        } else {
            binding.textResultado.setText(getString(R.string.cofre_derrota, codigoSecreto, tentativasUsadas, (int) segundos));
            binding.textResultado.setTextColor(ContextCompat.getColor(this, R.color.cor_derrota));
            // fracasso: o texto treme para os lados (CycleInterpolator faz ir e voltar 3 vezes)
            binding.textResultado.setTranslationX(0f);
            binding.textResultado.animate()
                    .translationX(25f)
                    .setInterpolator(new CycleInterpolator(3))
                    .setDuration(600);
        }
    }

    private void encerrarSessao() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.EXTRA_ULTIMO_MODULO, getString(R.string.painel_cofre_titulo));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MainActivity.TAG_CICLO, "CofreActivity: onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MainActivity.TAG_CICLO, "CofreActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(MainActivity.TAG_CICLO, "CofreActivity: onDestroy");
    }
}
