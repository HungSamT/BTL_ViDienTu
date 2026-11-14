package com.example.ewallet.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;

import com.example.ewallet.R;
import com.example.ewallet.database.DatabaseHelper;
import com.example.ewallet.model.LinkedBank;
import com.example.ewallet.model.User;
import com.example.ewallet.utils.CurrencyUtils;
import com.example.ewallet.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class TopUpWithdrawActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String MODE_TOP_UP = "MODE_TOP_UP";
    public static final String MODE_WITHDRAW = "MODE_WITHDRAW";

    private String currentMode = MODE_TOP_UP;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int userId;
    private User currentUser;
    private List<LinkedBank> bankList = new ArrayList<>();

    private TextView tvHeaderTitle, tvCurrentBalance, tvBankLabel, tabTopUp, tabWithdraw;
    private AppCompatSpinner spinnerBanks;
    private LinearLayout layoutNoBank;
    private TextInputEditText etAmount;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_withdraw);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = sessionManager.getUserId();

        String modeFromIntent = getIntent().getStringExtra(EXTRA_MODE);
        if (MODE_WITHDRAW.equals(modeFromIntent)) {
            currentMode = MODE_WITHDRAW;
        } else {
            currentMode = MODE_TOP_UP;
        }

        initViews();
        setupEvents();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        tvBankLabel = findViewById(R.id.tvBankLabel);
        tabTopUp = findViewById(R.id.tabTopUp);
        tabWithdraw = findViewById(R.id.tabWithdraw);
        spinnerBanks = findViewById(R.id.spinnerBanks);
        layoutNoBank = findViewById(R.id.layoutNoBank);
        etAmount = findViewById(R.id.etAmount);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnQuick50k).setOnClickListener(v -> etAmount.setText("50000"));
        findViewById(R.id.btnQuick100k).setOnClickListener(v -> etAmount.setText("100000"));
        findViewById(R.id.btnQuick200k).setOnClickListener(v -> etAmount.setText("200000"));
        findViewById(R.id.btnQuick500k).setOnClickListener(v -> etAmount.setText("500000"));

        findViewById(R.id.btnGoToLinkBank).setOnClickListener(v -> {
            startActivity(new Intent(this, LinkedBankActivity.class));
        });
    }

    private void setupEvents() {
        tabTopUp.setOnClickListener(v -> switchMode(MODE_TOP_UP));
        tabWithdraw.setOnClickListener(v -> switchMode(MODE_WITHDRAW));
        btnSubmit.setOnClickListener(v -> validateAndConfirm());
    }

    private void switchMode(String mode) {
        currentMode = mode;
        int colorPrimary = ContextCompat.getColor(this, R.color.primary);
        int colorSecondary = ContextCompat.getColor(this, R.color.text_secondary);

        if (MODE_TOP_UP.equals(currentMode)) {
            tvHeaderTitle.setText("Nạp tiền vào ví");
            tvBankLabel.setText("Nguồn tiền từ ngân hàng liên kết");
            tabTopUp.setTextColor(colorPrimary);
            tabWithdraw.setTextColor(colorSecondary);
            btnSubmit.setText("XÁC NHẬN NẠP TIỀN");
        } else {
            tvHeaderTitle.setText("Rút tiền về ngân hàng");
            tvBankLabel.setText("Rút về tài khoản ngân hàng");
            tabTopUp.setTextColor(colorSecondary);
            tabWithdraw.setTextColor(colorPrimary);
            btnSubmit.setText("XÁC NHẬN RÚT TIỀN");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        switchMode(currentMode);
    }

    private void loadData() {
        currentUser = dbHelper.getUserById(userId);
        if (currentUser != null) {
            tvCurrentBalance.setText(CurrencyUtils.formatVND(currentUser.getBalance()));
        }

        bankList = dbHelper.getLinkedBanks(userId);
        if (bankList.isEmpty()) {
            layoutNoBank.setVisibility(View.VISIBLE);
            spinnerBanks.setVisibility(View.GONE);
            btnSubmit.setEnabled(false);
        } else {
            layoutNoBank.setVisibility(View.GONE);
            spinnerBanks.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(true);

            List<String> bankDisplayNames = new ArrayList<>();
            for (LinkedBank b : bankList) {
                bankDisplayNames.add(b.getBankName() + " - " + b.getAccountNumber()
                        + " (Số dư: " + CurrencyUtils.formatVND(b.getMockBalance()) + ")");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, bankDisplayNames);
            spinnerBanks.setAdapter(adapter);
        }
    }

    private void validateAndConfirm() {
        if (bankList.isEmpty()) {
            Toast.makeText(this, "Vui lòng liên kết ngân hàng trước khi thực hiện", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = (etAmount.getText() != null) ? etAmount.getText().toString().trim() : "";
        if (amountStr.isEmpty()) {
            etAmount.setError("Vui lòng nhập số tiền");
            etAmount.requestFocus();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Số tiền không hợp lệ");
            etAmount.requestFocus();
            return;
        }

        if (amount < 10000) {
            etAmount.setError("Số tiền tối thiểu là 10.000 đ");
            etAmount.requestFocus();
            return;
        }

        int selectedIndex = spinnerBanks.getSelectedItemPosition();
        if (selectedIndex < 0 || selectedIndex >= bankList.size()) {
            Toast.makeText(this, "Vui lòng chọn ngân hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        LinkedBank selectedBank = bankList.get(selectedIndex);

        if (MODE_TOP_UP.equals(currentMode)) {
            if (amount > selectedBank.getMockBalance()) {
                etAmount.setError("Số dư trong thẻ không đủ (" + CurrencyUtils.formatVND(selectedBank.getMockBalance()) + ")");
                etAmount.requestFocus();
                return;
            }
        } else {
            if (amount > currentUser.getBalance()) {
                etAmount.setError("Số dư ví không đủ (" + CurrencyUtils.formatVND(currentUser.getBalance()) + ")");
                etAmount.requestFocus();
                return;
            }
        }

        showPinDialog(selectedBank, amount);
    }

    private void showPinDialog(LinkedBank bank, long amount) {
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

        String actionName = MODE_TOP_UP.equals(currentMode) ? "Nạp tiền" : "Rút tiền";
        tvTitle.setText("Xác thực " + actionName);
        tvMsg.setText("Xác nhận " + actionName + " " + CurrencyUtils.formatVND(amount) + "\nVui lòng nhập mã PIN 6 số:");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String pinInput = (etPin.getText() != null) ? etPin.getText().toString().trim() : "";
            if (pinInput.length() != 6) {
                Toast.makeText(this, "Mã PIN phải đủ 6 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isPinValid = dbHelper.verifyPin(userId, pinInput);
            if (!isPinValid) {
                Toast.makeText(this, "Mã PIN không chính xác! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            processTransaction(bank, amount);
        });

        dialog.show();
    }

    private void processTransaction(LinkedBank bank, long amount) {
        long txId;
        if (MODE_TOP_UP.equals(currentMode)) {
            txId = dbHelper.topUpFromBank(userId, bank.getId(), amount);
        } else {
            txId = dbHelper.withdrawToBank(userId, bank.getId(), amount);
        }

        if (txId != -1) {
            Intent intent = new Intent(this, ReceiptActivity.class);
            intent.putExtra(ReceiptActivity.EXTRA_TRANSACTION_ID, txId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Giao dịch không thành công. Vui lòng thử lại sau!", Toast.LENGTH_LONG).show();
        }
    }
}
