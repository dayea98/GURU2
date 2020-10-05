package com.example.sy925.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sy925.myapplication.model.Member;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegActivity extends AppCompatActivity {

    EditText regName;
    EditText regTel;
    EditText regNickname;
    EditText regId;
    EditText regPwd;
    EditText regPwdQ;
    EditText regPwdA;

    private final String TAG = this.getClass().getSimpleName();
    private static final int REQ_CODE_G_SIGN = 5000;

    private GoogleSignInClient mGoogleClient;   // 구글 클라이언트 인증객체
    private FirebaseAuth firebaseAuth;          // firebase 인증객체
    private FirebaseAuth.AuthStateListener mAuthListener; // firebase Listener

    private EditText etxtEmail;
    private EditText etxtPwd;

    ProgressDialog dialog;
    //BackgroudTask backgroudTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        // 구글인증 옵션
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleClient = GoogleSignIn.getClient(this, gso);

        // firebase 인증객체
        firebaseAuth = FirebaseAuth.getInstance();

        Button btnCheckNick=findViewById(R.id.btnCheckNick);
        Button btnCheckId=findViewById(R.id.btnCheckId);
        Button btnOK=findViewById(R.id.btnOK);
        regName = findViewById(R.id.regName);
        regTel = findViewById(R.id.regTel);
        regNickname = findViewById(R.id.regNickname);
        regId = findViewById(R.id.regId);
        regPwd = findViewById(R.id.regPwd);
        regPwdQ = findViewById(R.id.regPwdQ);
        regPwdA = findViewById(R.id.regPwdA);
/*
        btnCheckNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckNickname();
            }
        });
        btnCheckId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckId();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
            }
        });*/

        // 구글로그인 요청
        final SignInButton btnGoogle = findViewById(R.id.btnGoogle);
/*        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 프로그레그스 바 출력
                dialog.show();

                // 구글인증 실행
                Intent intent = mGoogleClient.getSignInIntent();
                startActivityForResult(intent, REQ_CODE_G_SIGN);
            }
        });
*/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
/*
                if (user != null) { // 로그인 완료
                    // TODO: 메인 화면으로 이동
                    Intent intent =new Intent (RegActivity.this,InsertActivity.class);//getApplicationContext()라고 써도됨
                    startActivity(intent);
                    finish();
                } else {
                    // 로그아웃
                }*/
            }
        };


        etxtEmail = findViewById(R.id.etxtEmail);
        etxtPwd = findViewById(R.id.etxtPwd);

        // E-mail 인증버튼
        btnCheckId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                // firebase E-mail 인증요청
                createUser(regId.getText().toString(), regPwd.getText().toString());
            }
        });
/*
        Button btnLogout = findViewById(R.id.btnLogout);
     btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
            }
        });
*/
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("데이터를 확인하고 있습니다.");
    }
    private void CheckNickname(){
        //닉네임 중복체크
    }
    private void CheckId(){
        //아이디 중복체크



    }
    private void SaveData() {
        //데이터저장
        Member member = new Member();


        member.setName(regName.getText().toString());
        member.setName(regTel.getText().toString());
        member.setName(regNickname.getText().toString());
        member.setName(regId.getText().toString());
        member.setName(regPwd.getText().toString());
        member.setName(regPwdQ.getText().toString());
        member.setName(regPwdA.getText().toString());

        dialog.show();

        // firebase E-mail 인증요청
       // createUser(etxtEmail.getText().toString(), etxtPwd.getText().toString());
      //  createUser(member.getId(),member.getPwd());

    }

    // 사용자  Email 등록
    private void createUser(final String email, final String pwd) {
        firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        dialog.dismiss(); // 다이얼로그 제거

                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().getEmail();
                            Toast.makeText(getApplicationContext(), "아이디 생성", Toast.LENGTH_LONG).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "아이디 중복", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    // E-mial 인증
    private void loginEmail(String email, String pwd) {
        firebaseAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String email = firebaseAuth.getCurrentUser().getEmail();
                            Log.d(TAG, "test:"+ email);
                        } else {
                            Log.d(TAG, "인증 실패:");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQ_CODE_G_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try { // 구글인증 성공
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // firebase에 등록
                firebaseAuthWithGoogle(account);
            } catch (Exception ex) { // 구글인증 실패
                ex.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        dialog.dismiss();
                        if (task.isSuccessful()) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Firebase 아이디 생성이 완료:" + user.getEmail()
                                    , Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Firebase 인증실패"
                                    , Toast.LENGTH_LONG).show();
                            destroy();
                        }

                    }
                });
    }

    @Override
    protected void onStart()
    {
        firebaseAuth.addAuthStateListener(mAuthListener); // firebase에 리스너 등록
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener); // firebase 리스너 제거
        }
    }

    private void destroy() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }
}

