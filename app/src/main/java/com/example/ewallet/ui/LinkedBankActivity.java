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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ewallet.R;
import com.example.ewallet.adapter.BankAdapter;
import com.example.ewallet.database.DatabaseHelper;
import com.example.ewallet.model.LinkedBank;
import com.example.ewallet.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class LinkedBankActivity extends AppCompatActivity {

    private RecyclerView rvBanks;
    private TextView tvEmptyBanks;
    private BankAdapter bankAdapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int currentUserId;

    private static final String[] BANK_NAMES = {
            "Vietcombank", "MB Bank", "Techcombank", "BIDV", "ACB", "VPBank"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_bank);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = sessionManager.getUserId();

        ImageButton btnBack = findViewById(R.id.btnBack);
        rvBanks = findViewById(R.id.rvBanks);
        tvEmptyBanks = findViewById(R.id.tvEmptyBanks);
        MaterialButton btnAddBank = findViewById(R.id.btnAddBank);

        btnBack.setOnClickListener(v -> finish());

        rvBanks.setLayoutManager(new LinearLayoutManager(this));
        bankAdapter = new BankAdapter(null, (bank, position) -> showDeleteConfirmationDialog(bank));
        rvBanks.setAdapter(bankAdapter);

        btnAddBank.setOnClickListener(v -> showAddBankDialog());

        loadBanks();
    }

    private void loadBanks() {
        List<LinkedBank> banks = dbHelper.getLinkedBanks(currentUserId);
        if (banks.isEmpty()) {
            tvEmptyBanks.setVisibility(View.VISIBLE);
            rvBanks.setVisibility(View.GONE);
        } else {
            tvEmptyBanks.setVisibility(View.GONE);
            rvBanks.setVisibility(View.VISIBLE);
            bankAdapter.setData(banks);
        }
    }

    private void showDeleteConfirmationDialog(LinkedBank bank) {
        new AlertDialog.Builder(this)
                .setTitle("Hủy liên kết")
                .setMessage("Bạn có chắc chắn muốn hủy liên kết ngân hàng " + bank.getBankName() + " (STK: " + bank.getAccountNumber() + ")?")
                .setPositiveButton("Hủy liên kết", (dialog, which) -> {
                    boolean success = dbHelper.deleteLinkedBank(bank.getId());
                    if (success) {
                        Toast.makeText(this, "Đã hủy liên kết thành công", Toast.LENGTH_SHORT).show();
                        loadBanks();
                    } else {
                        Toast.makeText(this, "Không thể hủy liên kết, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showAddBankDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_bank);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        AppCompatSpinner spinnerBankName = dialog.findViewById(R.id.spinnerBankName);
        TextInputEditText etAccountNumber = dialog.findViewById(R.id.etAccountNumber);
        TextInputEditText etMockBalance = dialog.findViewById(R.id.etMockBalance);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancelAddBank);
        MaterialButton btnSubmit = dialog.findViewById(R.id.btnSubmitAddBank);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, BANK_NAMES);
        spinnerBankName.setAdapter(spinnerAdapter);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String selectedBank = spinnerBankName.getSelectedItem().toString();
            String accNum = (etAccountNumber.getText() != null) ? etAccountNumber.getText().toString().trim() : "";
            String balanceStr = (etMockBalance.getText() != null) ? etMockBalance.getText().toString().trim() : "";

            if (accNum.isEmpty()) {
                etAccountNumber.setError("Vui lòng nhập số tài khoản ngân hàng");
                etAccountNumber.requestFocus();
                return;
            }

            long mockBalance = 5000000L;
            if (!balanceStr.isEmpty()) {
                try {
                    mockBalance = Long.parseLong(balanceStr);
                } catch (NumberFormatException e) {
                    mockBalance = 5000000L;
                }
            }

            long result = dbHelper.addLinkedBank(currentUserId, selectedBank, accNum, mockBalance);
            if (result != -1) {
                Toast.makeText(this, "Liên kết thành công ngân hàng " + selectedBank, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadBanks();
            } else {
                Toast.makeText(this, "Liên kết thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
