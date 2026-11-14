package com.example.ewallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ewallet.R;
import com.example.ewallet.database.DatabaseHelper;
import com.example.ewallet.model.User;
import com.example.ewallet.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPhone, etPassword;
    private MaterialButton btnLogin;
    private TextView tvGoToRegister;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        dbHelper = DatabaseHelper.getInstance(this);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        findViewById(R.id.btnQuickUser1).setOnClickListener(v -> {
            etPhone.setText("0901234567");
            etPassword.setText("123456");
        });

        findViewById(R.id.btnQuickUser2).setOnClickListener(v -> {
            etPhone.setText("0987654321");
            etPassword.setText("123456");
        });

        btnLogin.setOnClickListener(v -> handleLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void handleLogin() {
        String phone = (etPhone.getText() != null) ? etPhone.getText().toString().trim() : "";
        String password = (etPassword.getText() != null) ? etPassword.getText().toString().trim() : "";

        if (phone.isEmpty()) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (phone.length() != 10 || !phone.startsWith("0")) {
            etPhone.setError("Số điện thoại không hợp lệ (cần 10 chữ số bắt đầu bằng 0)");
            etPhone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        User user = dbHelper.login(phone, password);
        if (user != null) {
            sessionManager.saveLoginSession(user.getId(), user.getPhone(), user.getFullName());
            Toast.makeText(this, "Đăng nhập thành công! Xin chào " + user.getFullName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Số điện thoại hoặc mật khẩu không chính xác", Toast.LENGTH_LONG).show();
        }
    }
}
