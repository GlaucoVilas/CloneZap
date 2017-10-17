package com.firebase.cursoandroid.clonezap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.cursoandroid.clonezap.R;
import com.firebase.cursoandroid.clonezap.config.ConfiguracaoFirebase;
import com.firebase.cursoandroid.clonezap.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button btnLogar;
    private Usuario usuario;
    private FirebaseAuth auth;

    /*private EditText nome;
    private EditText ddi;
    private EditText ddd;
    private EditText telefone;
    private Button cadastrar;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();

        email = (EditText) findViewById(R.id.edit_email);
        senha = (EditText) findViewById(R.id.edit_senha);
        btnLogar = (Button) findViewById(R.id.btn_login);

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                validarLogin();
            }
        });

        /*Permissao.validaPermissoes(1, this, permissoesNecessarias);

        nome = (EditText) findViewById(R.id.edit_nome);
        ddi = (EditText) findViewById(R.id.edit_ddi);
        ddd = (EditText) findViewById(R.id.edit_ddd);
        telefone = (EditText) findViewById(R.id.edit_phone);
        cadastrar = (Button) findViewById(R.id.btn_cadastrar);

        /*******************Mascara do telefone - rtoshiro/MaskFormatter(github)*******************/
        /*SimpleMaskFormatter simpleMaskFormatterDDI = new SimpleMaskFormatter("+NN");
        MaskTextWatcher maskTextWatcherDDI = new MaskTextWatcher(ddi, simpleMaskFormatterDDI);
        ddi.addTextChangedListener(maskTextWatcherDDI);

        SimpleMaskFormatter simpleMaskFormatterDDD = new SimpleMaskFormatter("NN");
        MaskTextWatcher maskTextWatcherDDD = new MaskTextWatcher(ddd, simpleMaskFormatterDDD);
        ddd.addTextChangedListener(maskTextWatcherDDD);

        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("NNNNN-NNNN");
        MaskTextWatcher maskTextWatcher = new MaskTextWatcher(telefone, simpleMaskFormatter);
        telefone.addTextChangedListener(maskTextWatcher);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomeUsuario = nome.getText().toString();
                String telefoneCompleto =
                        ddi.getText().toString() +
                                ddd.getText().toString() +
                                telefone.getText().toString();

                String telefoneSemFormatacao = telefoneCompleto.replaceAll("[^0-9]", "");

                //Não recomendavel, uso somente para estudo. (gerar Token no servidor é o correto!)
                Random random = new Random();
                int numeroRandomico = random.nextInt(9999 - 1000) + 1000;
                String token = String.valueOf(numeroRandomico);
                String mensagemEnvio = "CloneZap - Código de Confirmação: " + token;

                //Salvando os dados para a validação
                Preferencias preferencias = new Preferencias(LoginActivity.this);
                preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneSemFormatacao, token);

                //Envio do SMS
                boolean enviarSMS = enviarSMS("+" + telefoneSemFormatacao, mensagemEnvio);


                if (enviarSMS) {
                    Intent intent = new Intent(LoginActivity.this, ValidadorActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Problema ao enviar SMS, tente novamente!", Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    private void verificarUsuarioLogado() {
        auth = ConfiguracaoFirebase.getFirebaseAuth();
        if (auth.getCurrentUser() != null) {
            abrirTelaPrincipal();
        }

    }

    private void validarLogin() {
        auth = ConfiguracaoFirebase.getFirebaseAuth();
        auth.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    abrirTelaPrincipal();
                    Toast.makeText(LoginActivity.this, "Sucesso ao fazer login!", Toast.LENGTH_SHORT).show();
                } else {
                    String erroExecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        erroExecao = "E-mail não existe ou foi desativado!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExecao = "Senha incorreta!";
                    } catch (Exception e) {
                        erroExecao = "Erro ao logar!";
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, "Erro: " + erroExecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void abrirCadastroUsuario(View view) {
        Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }
    /*//Envio de SMS
    private boolean enviarSMS(String telefone, String mensagem) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, mensagem, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int resultado : grantResults) {

            if (resultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }

        }

    }

    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar esse app, é necessário aceitar as permissões");

        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }*/
}

