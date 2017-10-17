package com.firebase.cursoandroid.clonezap.activity;

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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button btnCadastrar;
    private Usuario usuario;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome = (EditText) findViewById(R.id.edit_nome);
        email = (EditText) findViewById(R.id.edit_email);
        senha = (EditText) findViewById(R.id.edit_senha);
        btnCadastrar = (Button) findViewById(R.id.botao_cadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario = new Usuario();
                usuario.setNome(nome.getText().toString());
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                cadastrarUsuario();
            }
        });
    }

    private void cadastrarUsuario() {
        auth = ConfiguracaoFirebase.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(CadastroUsuarioActivity.this, "Sucesso ao cadastrar usuario!", Toast.LENGTH_SHORT).show();

                    usuario.setId( task.getResult().getUser().getUid() );
                    usuario.salvar();

                    auth.signOut();
                    finish();
                } else {
                    String erroExecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExecao = "Digite uma senha mais forte, contendo caracteres e com letras e números!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExecao = "O e-mail digitado é invalido, digite um novo e-mail!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erroExecao = "e-mail já cadastrado!";
                    } catch (Exception e) {
                        erroExecao = "Ao cadastrar usuario!";
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroUsuarioActivity.this, "Erro:" + erroExecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
