package com.kye.movieinformationapp.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kye.movieinformationapp.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    TextView txt_register,txt_forgot;
    EditText edt_name,edt_pw;
    Button btn_in,btn_out;
    SignInButton btn_google;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    private GoogleApiClient googleApiClient;
    private static int SIGN_GOOGLE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //로그인 화면

        auth = FirebaseAuth.getInstance(); //파이어베이스 인증 객체

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        edt_name = findViewById(R.id.edt_name);
        edt_pw = findViewById(R.id.edt_pw);
        btn_in = findViewById(R.id.btn_in);
        btn_out = findViewById(R.id.btn_out);
        txt_register = findViewById(R.id.txt_register);
        txt_forgot = findViewById(R.id.txt_forgot);
        btn_google = findViewById(R.id.btn_google);

        txt_forgot.setOnClickListener(this);
        txt_register.setOnClickListener(this);
        btn_in.setOnClickListener(this);
        btn_out.setOnClickListener(this);
        btn_google.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        if(v==btn_in){
            login();
        }else if(v==btn_out){
            finish();
        }else if(v==txt_register){
           Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
           startActivity(intent);
        }else if(v==txt_forgot){
           Intent intent = new Intent(getApplicationContext(),FindActivity.class);
           startActivity(intent);
        }else if(v==btn_google){
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent,SIGN_GOOGLE);
        }
    }


    private void login(){
        final String mail = edt_name.getText().toString();
        String pw = edt_pw.getText().toString();
        if(mail.isEmpty()){
            Toast.makeText(getApplicationContext(),"Email을 입력해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(pw.isEmpty()){
            Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("로그인중입니다. 잠시만 기다려주세요..");
        progressDialog.show();

        //firebase의 auth 객체를 이용하여 사용자가 입력한 이메일과 비밀번호를 signInWithEmailAndPassword 로 확인 하여 로그인을 함
        auth.signInWithEmailAndPassword(mail,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent();
                    intent.putExtra("mail",mail);
                    setResult(RESULT_OK,intent);
                    Toast.makeText(getApplicationContext(),"로그인이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                    finish();

                }else{
                    Toast.makeText(getApplicationContext(),"아이디 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //구글 로그인 인증을 했을 때 결과 값을 돌려 받음
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SIGN_GOOGLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){ //인증결과 성공적인지 확인
                GoogleSignInAccount account = result.getSignInAccount(); // 구글로그인 정보를 담고 있다
                resultLogin(account); //로그인 결과 값 출력 수행
            }
        }
    }
    private void resultLogin(final GoogleSignInAccount account) {
        AuthCredential credential  = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.setMessage("로그인중입니다. 잠시만 기다려주세요..");
                        progressDialog.show();

                        if(task.isSuccessful()){ //로그인 성공했으면
                            Intent intent = new Intent();
                            intent.putExtra("mail",account.getEmail());
                            intent.putExtra("photo",String.valueOf(account.getPhotoUrl()));
                            setResult(RESULT_OK,intent);
                            finish();
                            Toast.makeText(getApplicationContext(),"로그인이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                        }else { //로그인 실패시

                        }
                        progressDialog.dismiss();
                    }
                });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
