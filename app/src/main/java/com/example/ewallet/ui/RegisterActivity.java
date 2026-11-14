package com.example.ewallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ewallet.R;
import com.example.ewallet.database.DatabaseHelper;
import com.example.ewallet.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etPhone, etPassword, etConfirmPassword, etPinCode;
    private MaterialButton btnRegister;
    private ImageButton btnBack;
    private TextView tvGoToLogin;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPinCode = findViewById(R.id.etPinCode);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnBack.setOnClickListener(v -> finish());
        tvGoToLogin.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String fullName = (etFullName.getText() != null) ? etFullName.getText().toString().trim() : "";
        String phone = (etPhone.getText() != null) ? etPhone.getText().toString().trim() : "";
        String password = (etPassword.getText() != null) ? etPassword.getText().toString().trim() : "";
        String confirmPassword = (etConfirmPassword.getText() != null) ? etConfirmPassword.getText().toString().trim() : "";
        String pinCode = (etPinCode.getText() != null) ? etPinCode.getText().toString().trim() : "";

        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ và tên");
            etFullName.requestFocus();
            return;
        }

        if (phone.length() != 10 || !phone.startsWith("0")) {
            etPhone.setError("Số điện thoại không hợp lệ (cần 10 chữ số bắt đầu bằng 0)");
            etPhone.requestFocus();
            return;
        }

        if (dbHelper.isPhoneExists(phone)) {
            etPhone.setError("Số điện thoại này đã được đăng ký");
            etPhone.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        if (pinCode.length() != 6) {
            etPinCode.setError("Mã PIN phải đúng 6 chữ số");
            etPinCode.requestFocus();
            return;
        }

        long newUserId = dbHelper.registerUser(phone, fullName, password, pinCode);
        if (newUserId != -1) {
            sessionManager.saveLoginSession((int) newUserId, phone, fullName.toUpperCase());
            Toast.makeText(this, "Đăng ký thành công! Bạn nhận được 50.000 đ tân thủ", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
        }
    }
}
