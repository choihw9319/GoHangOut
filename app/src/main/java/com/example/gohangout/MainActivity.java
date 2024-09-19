package com.example.gohangout;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText mypageNameString;
    private EditText mypageHomeString;
    private EditText mypageFavorString;
    private EditText mypageEmailString;
    private Button storageButton;
    private Button checkButton;
    private Button secessionButton; // 회원탈퇴 버튼
    private Button policyButton; // 정책 버튼
    private Button logoutButton; // 로그아웃 버튼
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mypageNameString = findViewById(R.id.Mypage_namestring);
        mypageHomeString = findViewById(R.id.Mypage_homestring);
        mypageFavorString = findViewById(R.id.Mypage_favorstring);
        mypageEmailString = findViewById(R.id.Mypage_emailstring);
        storageButton = findViewById(R.id.MyPage_Storagebutton);
        checkButton = findViewById(R.id.check);
        secessionButton = findViewById(R.id.MyPage_secessionbool); // 회원탈퇴 버튼
        policyButton = findViewById(R.id.MyPage_policybool); // 정책 버튼
        logoutButton = findViewById(R.id.MyPage_logoutbool); // 로그아웃 버튼

        storageButton.setVisibility(View.GONE); // 초기에는 버튼을 숨김

        // Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://152.67.209.177:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // EditText에 TextWatcher 추가
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 아무 작업도 하지 않음
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 아무 작업도 하지 않음
            }

            @Override
            public void afterTextChanged(Editable s) {
                // EditText 중 하나라도 변경되면 버튼을 표시
                if (!mypageNameString.getText().toString().isEmpty() ||
                        !mypageHomeString.getText().toString().isEmpty() ||
                        !mypageFavorString.getText().toString().isEmpty() ||
                        !mypageEmailString.getText().toString().isEmpty()) {
                    storageButton.setVisibility(View.VISIBLE);
                } else {
                    storageButton.setVisibility(View.GONE);
                }
            }
        };

        // 모든 EditText에 TextWatcher 추가
        mypageNameString.addTextChangedListener(textWatcher);
        mypageHomeString.addTextChangedListener(textWatcher);
        mypageFavorString.addTextChangedListener(textWatcher);
        mypageEmailString.addTextChangedListener(textWatcher);

        // Check 버튼 클릭 시 중복 체크
        checkButton.setOnClickListener(v -> {
            String enteredId = mypageNameString.getText().toString().trim();
            if (!enteredId.isEmpty()) {
                checkForDuplicateId(enteredId);
            } else {
                Toast.makeText(MainActivity.this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 저장 버튼 클릭 리스너 설정
        storageButton.setOnClickListener(v -> {
            saveData();
            storageButton.setVisibility(View.GONE); // 버튼 클릭 후 숨김
        });

        // 회원탈퇴 버튼 클릭 리스너 설정
        secessionButton.setOnClickListener(v -> showSeparationDialog());

        // 정책 버튼 클릭 리스너 설정
        policyButton.setOnClickListener(v -> showPolicyDialog());

        // 로그아웃 버튼 클릭 리스너 설정
        logoutButton.setOnClickListener(v -> showLogoutDialog());
    }

    private void showSeparationDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.policy_diallog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(true);  // 클릭 시 닫히도록 설정

        AlertDialog dialog = builder.create();

        // 다이얼로그 배경을 투명하게 설정
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        Button cancelButton = dialogView.findViewById(R.id.Mypage_nobool1);
        Button confirmButton = dialogView.findViewById(R.id.Mypage_yesbool1);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            performSecession();  // 실제 탈퇴 처리 로직을 호출
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showPolicyDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.secession_diallog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(true);  // 클릭 시 닫히도록 설정

        AlertDialog dialog = builder.create();

        // 다이얼로그 배경을 투명하게 설정
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.setOnCancelListener(dialogInterface -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showLogoutDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.logout_diallog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(true);  // 클릭 시 닫히도록 설정

        AlertDialog dialog = builder.create();

        // 다이얼로그 배경을 투명하게 설정
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        Button cancelButton = dialogView.findViewById(R.id.Mypage_nobool);
        Button confirmButton = dialogView.findViewById(R.id.Mypage_yesbool);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            performLogout();  // 데이터 초기화 및 로그아웃 처리 로직을 호출
            dialog.dismiss();
        });

        dialog.show();
    }

    private void checkForDuplicateId(String enteredId) {
        apiService.getServerId().enqueue(new Callback<ServerIdResponse>() {
            @Override
            public void onResponse(Call<ServerIdResponse> call, Response<ServerIdResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ServerIdResponse serverResponse = response.body();
                    if (serverResponse.getId() != null && serverResponse.getId().contains(enteredId)) {
                        Toast.makeText(MainActivity.this, "ID가 이미 사용 중입니다. 다른 ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        storageButton.setEnabled(false);
                    } else {
                        Toast.makeText(MainActivity.this, "ID를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                        storageButton.setEnabled(true);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "서버에서 ID 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerIdResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ID 확인 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData() {
        String name = mypageNameString.getText().toString();
        String home = mypageHomeString.getText().toString();
        String favor = mypageFavorString.getText().toString();
        String email = mypageEmailString.getText().toString();

        // 데이터를 파일에 저장
        try {
            FileOutputStream fileOutputStream = openFileOutput("user_data.txt", MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write("이름: " + name + "\n");
            outputStreamWriter.write("사는 곳: " + home + "\n");
            outputStreamWriter.write("자주 가는 곳: " + favor + "\n");
            outputStreamWriter.write("이메일: " + email + "\n");
            outputStreamWriter.close();

            Toast.makeText(this, "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show();

            // 서버에 데이터 전송
            sendPostRequest(email, name, home, favor); // id는 name으로 설정됨
        } catch (IOException e) {
            Toast.makeText(this, "데이터 저장 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPostRequest(String email, String id, String residence, String destination) {
        PostData postData = new PostData(email, id, residence, destination);

        apiService.createPost(postData).enqueue(new Callback<PostData>() {
            @Override
            public void onResponse(Call<PostData> call, Response<PostData> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Post 성공: " + response.body().getId(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(MainActivity.this, "Error: " + response.code() + " - " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PostData> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("에이쌉라",  t.getMessage() );
            }
        });
    }

    private void performSecession() {
        String idToDelete = mypageNameString.getText().toString().trim();
        if (!idToDelete.isEmpty()) {
            deleteDataFromServer(idToDelete);
        } else {
            Toast.makeText(this, "삭제할 ID를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDataFromServer(String id) {
        apiService.deleteAccount(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 서버에서 값을 제대로 삭제했는지 확인하는 로직 추가
                    Toast.makeText(MainActivity.this, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "회원탈퇴에 실패했습니다. 서버 오류.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "회원탈퇴 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogout() {
        // 서버에서 받아온 데이터 초기화
        mypageNameString.setText("");  // EditText 값 초기화

        // 필요한 경우 추가적으로 초기화할 데이터가 있을 수 있습니다.
        // 예를 들어, SharedPreferences나 다른 저장된 데이터를 초기화할 수 있습니다.

        // 초기화 완료 메시지
        Toast.makeText(this, "모든 데이터가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
