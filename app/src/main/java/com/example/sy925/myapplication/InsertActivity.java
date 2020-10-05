package com.example.sy925.myapplication;


import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class InsertActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private static final int GALLERY_CODE = 1001;
    protected static final int MODE_NEW    = 1000;
    protected static final int MODE_MODIFY = 1001;
    private int mMode;

    private FirebaseAuth auth;//인증
    private FirebaseStorage storage;  //파일(이미지,음악,동영상)
    private FirebaseDatabase database;//db역할

    private ImageView imgView;
    private EditText etxtTitle;
    private EditText etxtDesc;
    private Button btnUpload;
    private String imgPath;

    private ImageDTO orgDto;//편집할 때 사용
    private boolean isImageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);


        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        imgView = findViewById(R.id.imageView3);
        etxtTitle = findViewById(R.id.title);
        etxtDesc = findViewById(R.id.descrition);

        //업로드(사진,객체)버튼
        btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                upload(imgPath);

                Log.d(TAG, "---------------------->>>> image=" + imgPath);
            }
        });
//리스트 뷰 이동 버튼
        Button btnList = findViewById(R.id.btnList);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BoardActivity.class));
            }
        });
//로그아웃
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
//이미지를 누르면 앨범에서 선택할 수 있게 하는 코드
        imgView.setOnClickListener(new View.OnClickListener() {  //앨범을 호출하는 코드
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        Intent intent = getIntent(); //이 액티비티를 호출할 때 putExtra로 보낸 객체
        mMode = intent.getIntExtra("MODE", MODE_NEW);

        if (mMode == MODE_MODIFY) {
            orgDto = (ImageDTO) intent.getSerializableExtra("ITEM");
            loadData();
        }
    }
//onCreate()
    private void loadData() {
        if (orgDto != null) {
            Log.d(TAG, "original Item=>" +orgDto.toString());
            etxtTitle.setText(orgDto.title);
            etxtDesc.setText((orgDto.description));
            Glide.with(imgView.getContext()).load(orgDto.imageUrl).into(imgView);
            btnUpload.setText("수정");
        }
    }



    private void clearField() {
        etxtTitle.setText("");
        etxtDesc.setText("");
        imgView.setImageResource(R.color.colorAccent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GALLERY_CODE && data != null) {
            imgPath = getPath(data.getData());//앨범에서 선택한 이미지 경로저장

            if (imgPath != null) {
                File file = new File(imgPath);
                imgView.setImageURI(Uri.fromFile(file));

                if (mMode ==  MODE_MODIFY) {
                    isImageChanged = true;
                }
            }
        }
    }

    private void upload(String uri) {



        if (uri == null || uri.length() == 0) {
            return;
        }

        if (mMode == MODE_NEW || (mMode == MODE_MODIFY && isImageChanged == true)) {
            // TODO: Firebase의 기존 이미지 삭제

            StorageReference storageRef = storage.getReferenceFromUrl("gs://myproject-a6935.appspot.com");//내 db연동

            // File upload
            Uri file = Uri.fromFile(new File(uri));

            final StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);


            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // task에 다운로드 Uri 전달
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    DatabaseReference firebaseRef = database.getReference().child("images");
                    String id = firebaseRef.push().getKey(); // key를 ID로 사용

                    ImageDTO imageDto = new ImageDTO();
                    imageDto.imageName = riversRef.getName();
                    imageDto.imageUrl = task.getResult().toString(); // 중요: 다운로드 URL
                    imageDto.title = etxtTitle.getText().toString();
                    imageDto.description = etxtDesc.getText().toString();
                    imageDto.userId = auth.getCurrentUser().getEmail();


                    if (mMode == MODE_MODIFY) {
                        imageDto.id = orgDto.id;
                    } else {
                        imageDto.id = id;
                    }
                    firebaseRef.child(imageDto.id).setValue(imageDto);//DB에 넣는 것

                    Toast.makeText(getApplicationContext(), "등록이 완료되었습니다.", Toast.LENGTH_LONG).show();

                    clearField();//ui에 있는 필드를 없앤다-> 다시 채울 수 있게

                /*
                mFirebase.push().setValue(user);
                mFirebase.child(keyArray.get(pos)).setValue(user);
                mFirebase.child(keyArray.get(pos)).removeValue();
                 */

                }
            });
        } else {

        }
    }

// step .2 end

    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }
}
