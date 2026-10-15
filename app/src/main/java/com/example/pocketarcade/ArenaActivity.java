package com.example.pocketarcade;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pocketarcade.databinding.ActivityArenaBinding;

import java.util.Random;

// Tela 4: pedra, papel e tesoura, melhor de tres
public class ArenaActivity extends AppCompatActivity {

    private static final int PEDRA = 0;
    private static final int PAPEL = 1;
    private static final int TESOURA = 2;

    private static final int VITORIAS_PARA_GANHAR = 2;

    // a posicao no vetor e o numero da jogada (0 = pedra, 1 = papel, 2 = tesoura)
    private static final int[] IMAGENS = {R.drawable.ic_pedra, R.drawable.ic_papel, R.drawable.ic_tesoura_jogada};
    private static final int[] NOMES = {R.string.pedra, R.string.papel, R.string.tesoura};

    private ActivityArenaBinding binding;
    private final Random random = new Random();

    private int pontosJogador = 0;
    private int pontosDispositivo = 0;

    // os players sao criados uma vez so no onCreate e reaproveitados em toda rodada
    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;
    private MediaPlayer somEmpate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityArenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(MainActivity.TAG_CICLO, "ArenaActivity: onCreate");

        String apelido = getIntent().getStringExtra(MainActivity.EXTRA_APELIDO);
        if (apelido == null) {
            Toast.makeText(this, R.string.erro_sem_apelido, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        binding.textJogador.setText(getString(R.string.label_jogador, apelido));

        somVitoria = MediaPlayer.create(this, R.raw.vitoria);
        somDerrota = MediaPlayer.create(this, R.raw.derrota);
        somEmpate = MediaPlayer.create(this, R.raw.empate);

        binding.botaoPedra.setOnClickListener(v -> jogar(PEDRA));
        binding.botaoPapel.setOnClickListener(v -> jogar(PAPEL));
        binding.botaoTesoura.setOnClickListener(v -> jogar(TESOURA));
        binding.botaoNovaPartida.setOnClickListener(v -> novaPartida());
        binding.botaoVoltar.setOnClickListener(v -> finish());
        binding.botaoEncerrar.setOnClickListener(v -> encerrarSessao());

        novaPartida();
    }

    private void jogar(int jogadaJogador) {
        int jogadaDispositivo = random.nextInt(3);

        mostrarJogadas(jogadaJogador, jogadaDispositivo);

        String nomeJogador = getString(NOMES[jogadaJogador]);
        String nomeDispositivo = getString(NOMES[jogadaDispositivo]);

        if (jogadaJogador == jogadaDispositivo) {
            // empate nao soma ponto
            mostrarResultadoRodada(getString(R.string.rodada_empate, nomeJogador), R.color.cor_empate);
            tocar(somEmpate);
        } else if (jogadorGanhou(jogadaJogador, jogadaDispositivo)) {
            pontosJogador++;
            mostrarResultadoRodada(getString(R.string.rodada_vitoria, nomeJogador, nomeDispositivo), R.color.cor_vitoria);
            tocar(somVitoria);
        } else {
            pontosDispositivo++;
            mostrarResultadoRodada(getString(R.string.rodada_derrota, nomeJogador, nomeDispositivo), R.color.cor_derrota);
            tocar(somDerrota);
        }

        atualizarPlacar();
        verificarFimDePartida();
    }

    private boolean jogadorGanhou(int jogador, int dispositivo) {
        return (jogador == PEDRA && dispositivo == TESOURA)
                || (jogador == PAPEL && dispositivo == PEDRA)
                || (jogador == TESOURA && dispositivo == PAPEL);
    }

    private void mostrarJogadas(int jogadaJogador, int jogadaDispositivo) {
        binding.imageJogador.setImageResource(IMAGENS[jogadaJogador]);
        binding.imageDispositivo.setImageResource(IMAGENS[jogadaDispositivo]);
        binding.imageJogador.setVisibility(View.VISIBLE);
        binding.imageDispositivo.setVisibility(View.VISIBLE);

        // jogada do jogador entra vindo da esquerda
        binding.imageJogador.setTranslationX(-150f);
        binding.imageJogador.setAlpha(0f);
        binding.imageJogador.animate().translationX(0f).alpha(1f).setDuration(400);

        // jogada do dispositivo entra girando
        binding.imageDispositivo.setRotation(-180f);
        binding.imageDispositivo.setAlpha(0f);
        binding.imageDispositivo.animate().rotation(0f).alpha(1f).setDuration(500);
    }

    private void mostrarResultadoRodada(String mensagem, int cor) {
        binding.textResultadoRodada.setText(mensagem);
        binding.textResultadoRodada.setTextColor(ContextCompat.getColor(this, cor));

        binding.textResultadoRodada.setScaleX(0.5f);
        binding.textResultadoRodada.setScaleY(0.5f);
        binding.textResultadoRodada.animate().scaleX(1f).scaleY(1f).setDuration(350);
    }

    private void atualizarPlacar() {
        binding.textPlacar.setText(getString(R.string.placar, pontosJogador, pontosDispositivo));
    }

    private void verificarFimDePartida() {
        if (pontosJogador == VITORIAS_PARA_GANHAR) {
            terminarPartida(getString(R.string.partida_vitoria), R.color.cor_vitoria);
        } else if (pontosDispositivo == VITORIAS_PARA_GANHAR) {
            terminarPartida(getString(R.string.partida_derrota), R.color.cor_derrota);
        }
    }

    private void terminarPartida(String mensagem, int cor) {
        ativarBotoesDeJogada(false);

        binding.textResultadoPartida.setText(mensagem);
        binding.textResultadoPartida.setTextColor(ContextCompat.getColor(this, cor));
        binding.textResultadoPartida.setVisibility(View.VISIBLE);
        binding.textResultadoPartida.setAlpha(0f);
        binding.textResultadoPartida.setTranslationY(40f);
        binding.textResultadoPartida.animate().alpha(1f).translationY(0f).setDuration(600);
    }

    private void novaPartida() {
        pontosJogador = 0;
        pontosDispositivo = 0;
        atualizarPlacar();
        ativarBotoesDeJogada(true);

        binding.textResultadoRodada.setText(R.string.arena_aguardando);
        binding.textResultadoRodada.setTextColor(ContextCompat.getColor(this, R.color.texto));
        binding.textResultadoPartida.setVisibility(View.GONE);
        binding.imageJogador.setVisibility(View.INVISIBLE);
        binding.imageDispositivo.setVisibility(View.INVISIBLE);
    }

    private void ativarBotoesDeJogada(boolean ativo) {
        binding.botaoPedra.setEnabled(ativo);
        binding.botaoPapel.setEnabled(ativo);
        binding.botaoTesoura.setEnabled(ativo);
        // deixa os botoes meio apagados quando a partida acabou
        float transparencia = ativo ? 1f : 0.4f;
        binding.botaoPedra.setAlpha(transparencia);
        binding.botaoPapel.setAlpha(transparencia);
        binding.botaoTesoura.setAlpha(transparencia);
    }

    private void tocar(MediaPlayer som) {
        // se o som ainda estiver tocando da rodada anterior, volta para o inicio
        if (som.isPlaying()) {
            som.pause();
        }
        som.seekTo(0);
        som.start();
    }

    private void pausarSom(MediaPlayer som) {
        if (som != null && som.isPlaying()) {
            som.pause();
        }
    }

    private void liberarSom(MediaPlayer som) {
        if (som != null) {
            som.release();
        }
    }

    private void encerrarSessao() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.EXTRA_ULTIMO_MODULO, getString(R.string.arena_titulo));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MainActivity.TAG_CICLO, "ArenaActivity: onResume");
    }

    // ao sair da tela o som e pausado (nao uso stop)
    @Override
    protected void onPause() {
        super.onPause();
        pausarSom(somVitoria);
        pausarSom(somDerrota);
        pausarSom(somEmpate);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MainActivity.TAG_CICLO, "ArenaActivity: onStop");
    }

    // release() libera o player e ele vai para o estado End
    @Override
    protected void onDestroy() {
        super.onDestroy();
        liberarSom(somVitoria);
        liberarSom(somDerrota);
        liberarSom(somEmpate);
        somVitoria = null;
        somDerrota = null;
        somEmpate = null;
        Log.d(MainActivity.TAG_CICLO, "ArenaActivity: onDestroy");
    }
}
