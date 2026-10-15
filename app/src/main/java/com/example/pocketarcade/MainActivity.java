package com.example.pocketarcade;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pocketarcade.databinding.ActivityMainBinding;

// Tela 1: apresentacao e apelido do jogador
public class MainActivity extends AppCompatActivity {

    // chaves dos extras usadas por todas as telas
    public static final String EXTRA_APELIDO = "com.example.pocketarcade.APELIDO";
    public static final String EXTRA_ULTIMO_MODULO = "com.example.pocketarcade.ULTIMO_MODULO";

    // tag usada no Log.d de todas as activities, para filtrar no Logcat
    public static final String TAG_CICLO = "CicloDeVida";

    public static final int TAMANHO_MINIMO_APELIDO = 3;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(TAG_CICLO, "MainActivity: onCreate");

        binding.botaoEntrar.setOnClickListener(v -> entrar());
    }

    // chamado quando alguem clica em "Encerrar sessao" em um desafio.
    // por causa das flags CLEAR_TOP e SINGLE_TOP, a Tela 1 que ja existe recebe a intent aqui
    // em vez de uma Tela 1 nova ser criada
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d(TAG_CICLO, "MainActivity: onNewIntent");

        String modulo = intent.getStringExtra(EXTRA_ULTIMO_MODULO);
        if (modulo != null) {
            binding.textUltimoModulo.setText(getString(R.string.ultimo_modulo, modulo));
            binding.textUltimoModulo.setVisibility(View.VISIBLE);
        }
    }

    private void entrar() {
        String apelido = binding.editApelido.getText().toString().trim();

        if (apelido.isEmpty()) {
            binding.editApelido.setError(getString(R.string.erro_apelido_vazio));
            return;
        }
        if (apelido.length() < TAMANHO_MINIMO_APELIDO) {
            binding.editApelido.setError(getString(R.string.erro_apelido_curto));
            return;
        }

        // desliga o botao para nao abrir a central duas vezes com clique duplo
        binding.botaoEntrar.setEnabled(false);

        // ViewPropertyAnimator: o botao diminui e quando termina volta ao tamanho normal
        binding.botaoEntrar.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.botaoEntrar.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .setListener(null);
                        abrirCentral(apelido);
                    }
                });
    }

    private void abrirCentral(String apelido) {
        // intent explicita: diz exatamente qual activity abrir
        Intent intent = new Intent(this, CentralActivity.class);
        intent.putExtra(EXTRA_APELIDO, apelido);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.botaoEntrar.setEnabled(true);
        Log.d(TAG_CICLO, "MainActivity: onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG_CICLO, "MainActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG_CICLO, "MainActivity: onDestroy");
    }
}
