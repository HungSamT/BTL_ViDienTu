package com.example.ewallet.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ewallet.R;
import com.example.ewallet.database.DatabaseHelper;
import com.example.ewallet.model.User;
import com.example.ewallet.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileAvatar, tvProfileFullName, tvProfilePhone, tvProfileCreatedAt;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = sessionManager.getUserId();

        initViews();
        setupEvents();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        tvProfileAvatar = findViewById(R.id.tvProfileAvatar);
        tvProfileFullName = findViewById(R.id.tvProfileFullName);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvProfileCreatedAt = findViewById(R.id.tvProfileCreatedAt);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupEvents() {
        findViewById(R.id.itemManageBanks).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, LinkedBankActivity.class));
        });

        findViewById(R.id.itemChangePin).setOnClickListener(v -> showChangePinDialog());

        findViewById(R.id.itemChangePassword).setOnClickListener(v -> showChangePasswordDialog());

        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutConfirmation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void loadUserInfo() {
        currentUser = dbHelper.getUserById(userId);
        if (currentUser != null) {
            tvProfileFullName.setText(currentUser.getFullName());
            tvProfilePhone.setText(currentUser.getPhone());
            tvProfileCreatedAt.setText("Ngày tham gia: " + currentUser.getCreatedAt());

            if (!currentUser.getFullName().isEmpty()) {
                tvProfileAvatar.setText(String.valueOf(currentUser.getFullName().charAt(0)).toUpperCase());
            }
        }
    }

    private void showChangePinDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pin_confirm);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = dialog.findViewById(R.id.tvPinDialogTitle);
        TextView tvMsg = dialog.findViewById(R.id.tvPinDialogMessage);
        TextInputEditText etOldPin = dialog.findViewById(R.id.etPinInput);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancelPin);
        MaterialButton btnConfirm = dialog.findViewById(R.id.btnConfirmPin);

        tvTitle.setText("Đổi mã PIN giao dịch");
        tvMsg.setText("Bước 1: Nhập mã PIN cũ hiện tại (6 chữ số):");
        etOldPin.setHint("Mã PIN cũ");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String oldPin = (etOldPin.getText() != null) ? etOldPin.getText().toString().trim() : "";
            if (oldPin.length() != 6) {
                Toast.makeText(this, "Mã PIN phải đủ 6 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dbHelper.verifyPin(userId, oldPin)) {
                Toast.makeText(this, "Mã PIN cũ không chính xác!", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            showEnterNewPinDialog(oldPin);
        });

        dialog.show();
    }

    private void showEnterNewPinDialog(String oldPin) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pin_confirm);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = dialog.findViewById(R.id.tvPinDialogTitle);
        TextView tvMsg = dialog.findViewById(R.id.tvPinDialogMessage);
        TextInputEditText etNewPin = dialog.findViewById(R.id.etPinInput);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancelPin);
        MaterialButton btnConfirm = dialog.findViewById(R.id.btnConfirmPin);

        tvTitle.setText("Thiết lập mã PIN mới");
        tvMsg.setText("Bước 2: Nhập mã PIN mới gồm 6 chữ số:");
        etNewPin.setHint("Mã PIN mới");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String newPin = (etNewPin.getText() != null) ? etNewPin.getText().toString().trim() : "";
            if (newPin.length() != 6) {
                Toast.makeText(this, "Mã PIN mới phải đúng 6 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.updatePin(userId, oldPin, newPin);
            if (success) {
                Toast.makeText(this, "Đổi mã PIN giao dịch thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Cập nhật mã PIN thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi mật khẩu đăng nhập");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 12);

        final com.google.android.material.textfield.TextInputEditText etOldPass = new com.google.android.material.textfield.TextInputEditText(this);
        etOldPass.setHint("Mật khẩu hiện tại");
        etOldPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etOldPass);

        final com.google.android.material.textfield.TextInputEditText etNewPass = new com.google.android.material.textfield.TextInputEditText(this);
        etNewPass.setHint("Mật khẩu mới (tối thiểu 6 ký tự)");
        etNewPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPass);

        builder.setView(layout);

        builder.setPositiveButton("Đổi mật khẩu", (dialog, which) -> {
            String oldPass = (etOldPass.getText() != null) ? etOldPass.getText().toString().trim() : "";
            String newPass = (etNewPass.getText() != null) ? etNewPass.getText().toString().trim() : "";

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.updatePassword(userId, oldPass, newPass);
            if (success) {
                Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Mật khẩu cũ không đúng hoặc cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    sessionManager.logout();
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
