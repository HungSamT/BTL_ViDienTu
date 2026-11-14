package com.example.ewallet.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ewallet.R;
import com.example.ewallet.database.DatabaseHelper;
import com.example.ewallet.model.User;
import com.example.ewallet.utils.CurrencyUtils;
import com.example.ewallet.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class TransferActivity extends AppCompatActivity {

    private TextInputEditText etReceiverPhone, etTransferAmount, etNote;
    private TextView tvCurrentBalance, tvReceiverInfo;
    private LinearLayout layoutReceiverResult;
    private ImageView ivReceiverStatus;
    private MaterialButton btnContinueTransfer;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int senderId;
    private User currentUser;
    private User receiverUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        senderId = sessionManager.getUserId();

        initViews();
        setupEvents();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        etReceiverPhone = findViewById(R.id.etReceiverPhone);
        layoutReceiverResult = findViewById(R.id.layoutReceiverResult);
        ivReceiverStatus = findViewById(R.id.ivReceiverStatus);
        tvReceiverInfo = findViewById(R.id.tvReceiverInfo);
        etTransferAmount = findViewById(R.id.etTransferAmount);
        etNote = findViewById(R.id.etNote);
        btnContinueTransfer = findViewById(R.id.btnContinueTransfer);

        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnQuick50k).setOnClickListener(v -> etTransferAmount.setText("50000"));
        findViewById(R.id.btnQuick100k).setOnClickListener(v -> etTransferAmount.setText("100000"));
        findViewById(R.id.btnQuick200k).setOnClickListener(v -> etTransferAmount.setText("200000"));
        findViewById(R.id.btnQuick500k).setOnClickListener(v -> etTransferAmount.setText("500000"));
    }

    private void setupEvents() {
        etReceiverPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phone = (s != null) ? s.toString().trim() : "";
                if (phone.length() == 10) {
                    checkReceiverPhone(phone);
                } else {
                    receiverUser = null;
                    layoutReceiverResult.setVisibility(View.GONE);
                }
            }
        });

        btnContinueTransfer.setOnClickListener(v -> validateAndProceed());
    }

    private void checkReceiverPhone(String phone) {
        if (currentUser != null && phone.equals(currentUser.getPhone())) {
            receiverUser = null;
            showReceiverStatus(false, "Không thể tự chuyển tiền cho chính mình");
            return;
        }

        User foundUser = dbHelper.getUserByPhone(phone);
        if (foundUser != null) {
            receiverUser = foundUser;
            showReceiverStatus(true, "Người nhận: " + foundUser.getFullName());
        } else {
            receiverUser = null;
            showReceiverStatus(false, "Không tìm thấy tài khoản ví với SĐT này");
        }
    }

    private void showReceiverStatus(boolean success, String message) {
        layoutReceiverResult.setVisibility(View.VISIBLE);
        tvReceiverInfo.setText(message);

        int colorIn = ContextCompat.getColor(this, R.color.money_in);
        int colorOut = ContextCompat.getColor(this, R.color.money_out);

        if (success) {
            layoutReceiverResult.setBackgroundResource(R.drawable.bg_circle_green);
            ivReceiverStatus.setImageResource(R.drawable.ic_check_circle);
            ivReceiverStatus.setColorFilter(colorIn);
            tvReceiverInfo.setTextColor(colorIn);
        } else {
            layoutReceiverResult.setBackgroundResource(R.drawable.bg_circle_red);
            ivReceiverStatus.setImageResource(R.drawable.ic_visibility_off);
            ivReceiverStatus.setColorFilter(colorOut);
            tvReceiverInfo.setTextColor(colorOut);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = dbHelper.getUserById(senderId);
        if (currentUser != null) {
            tvCurrentBalance.setText(CurrencyUtils.formatVND(currentUser.getBalance()));
            if (etNote.getText() == null || etNote.getText().toString().isEmpty()) {
                etNote.setHint(currentUser.getFullName() + " chuyen tien");
            }
        }
    }

    private void validateAndProceed() {
        if (receiverUser == null) {
            etReceiverPhone.setError("Vui lòng nhập SĐT người nhận hợp lệ");
            etReceiverPhone.requestFocus();
            return;
        }

        String amountStr = (etTransferAmount.getText() != null) ? etTransferAmount.getText().toString().trim() : "";
        if (amountStr.isEmpty()) {
            etTransferAmount.setError("Vui lòng nhập số tiền cần chuyển");
            etTransferAmount.requestFocus();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr);
        } catch (NumberFormatException e) {
            etTransferAmount.setError("Số tiền không hợp lệ");
            etTransferAmount.requestFocus();
            return;
        }

        if (amount < 1000) {
            etTransferAmount.setError("Số tiền tối thiểu là 1.000 đ");
            etTransferAmount.requestFocus();
            return;
        }

        if (currentUser == null || amount > currentUser.getBalance()) {
            etTransferAmount.setError("Số dư ví không đủ (" + CurrencyUtils.formatVND(currentUser != null ? currentUser.getBalance() : 0) + ")");
            etTransferAmount.requestFocus();
            return;
        }

        String note = (etNote.getText() != null) ? etNote.getText().toString().trim() : "";
        if (note.isEmpty()) {
            note = currentUser.getFullName() + " chuyen tien";
        }

        showPinDialog(receiverUser, amount, note);
    }

    private void showPinDialog(User receiver, long amount, String note) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pin_confirm);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = dialog.findViewById(R.id.tvPinDialogTitle);
        TextView tvMsg = dialog.findViewById(R.id.tvPinDialogMessage);
        TextInputEditText etPin = dialog.findViewById(R.id.etPinInput);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancelPin);
        MaterialButton btnConfirm = dialog.findViewById(R.id.btnConfirmPin);

        tvTitle.setText("Xác nhận chuyển tiền");
        tvMsg.setText("Chuyển " + CurrencyUtils.formatVND(amount) + " đến " + receiver.getFullName() + " (" + receiver.getPhone() + ")\nNhập mã PIN 6 số để hoàn tất:");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String pinInput = (etPin.getText() != null) ? etPin.getText().toString().trim() : "";
            if (pinInput.length() != 6) {
                Toast.makeText(this, "Mã PIN phải đủ 6 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isPinValid = dbHelper.verifyPin(senderId, pinInput);
            if (!isPinValid) {
                Toast.makeText(this, "Mã PIN không chính xác! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            processTransfer(receiver.getPhone(), amount, note);
        });

        dialog.show();
    }

    private void processTransfer(String receiverPhone, long amount, String note) {
        long senderTxId = dbHelper.transferMoney(senderId, receiverPhone, amount, note);

        if (senderTxId != -1) {
            Intent intent = new Intent(this, ReceiptActivity.class);
            intent.putExtra(ReceiptActivity.EXTRA_TRANSACTION_ID, senderTxId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Giao dịch chuyển tiền thất bại. Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
        }
    }
}
