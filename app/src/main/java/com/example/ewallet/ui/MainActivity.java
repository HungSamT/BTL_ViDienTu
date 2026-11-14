package com.example.ewallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ewallet.R;
import com.example.ewallet.adapter.TransactionAdapter;
import com.example.ewallet.database.DatabaseHelper;
import com.example.ewallet.model.Transaction;
import com.example.ewallet.model.User;
import com.example.ewallet.utils.CurrencyUtils;
import com.example.ewallet.utils.SessionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvAvatarChar, tvGreeting, tvUserPhone, tvBalance, tvEmptyRecent;
    private ImageButton btnToggleBalance, btnProfileHeader;
    private RecyclerView rvRecentTransactions;
    private TransactionAdapter transactionAdapter;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private boolean isBalanceVisible = true;
    private long currentBalance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        tvAvatarChar = findViewById(R.id.tvAvatarChar);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvBalance = findViewById(R.id.tvBalance);
        tvEmptyRecent = findViewById(R.id.tvEmptyRecent);
        btnToggleBalance = findViewById(R.id.btnToggleBalance);
        btnProfileHeader = findViewById(R.id.btnProfileHeader);
        rvRecentTransactions = findViewById(R.id.rvRecentTransactions);
    }

    private void setupRecyclerView() {
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(null, transaction -> {
            Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
            intent.putExtra(ReceiptActivity.EXTRA_TRANSACTION_ID, transaction.getId());
            startActivity(intent);
        });
        rvRecentTransactions.setAdapter(transactionAdapter);
    }

    private void setupListeners() {
        btnToggleBalance.setOnClickListener(v -> {
            isBalanceVisible = !isBalanceVisible;
            updateBalanceDisplay();
        });

        findViewById(R.id.cardTopUp).setOnClickListener(v -> {
            Intent intent = new Intent(this, TopUpWithdrawActivity.class);
            intent.putExtra(TopUpWithdrawActivity.EXTRA_MODE, TopUpWithdrawActivity.MODE_TOP_UP);
            startActivity(intent);
        });

        findViewById(R.id.cardWithdraw).setOnClickListener(v -> {
            Intent intent = new Intent(this, TopUpWithdrawActivity.class);
            intent.putExtra(TopUpWithdrawActivity.EXTRA_MODE, TopUpWithdrawActivity.MODE_WITHDRAW);
            startActivity(intent);
        });

        findViewById(R.id.cardTransfer).setOnClickListener(v -> {
            Intent intent = new Intent(this, TransferActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.cardBank).setOnClickListener(v -> {
            Intent intent = new Intent(this, LinkedBankActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.tvViewAllTransactions).setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });

        btnProfileHeader.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.tabHistory).setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.tabProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        int userId = sessionManager.getUserId();
        User user = dbHelper.getUserById(userId);

        if (user != null) {
            tvGreeting.setText("Xin chào, " + user.getFullName());
            tvUserPhone.setText(user.getPhone());
            currentBalance = user.getBalance();

            if (!user.getFullName().isEmpty()) {
                tvAvatarChar.setText(String.valueOf(user.getFullName().charAt(0)).toUpperCase());
            }

            updateBalanceDisplay();
        }

        // Load 5 recent transactions
        List<Transaction> recentTransactions = dbHelper.getTransactions(userId, "ALL", null, 5);
        if (recentTransactions.isEmpty()) {
            tvEmptyRecent.setVisibility(View.VISIBLE);
            rvRecentTransactions.setVisibility(View.GONE);
        } else {
            tvEmptyRecent.setVisibility(View.GONE);
            rvRecentTransactions.setVisibility(View.VISIBLE);
            transactionAdapter.setData(recentTransactions);
        }
    }

    private void updateBalanceDisplay() {
        if (isBalanceVisible) {
            tvBalance.setText(CurrencyUtils.formatVND(currentBalance));
            btnToggleBalance.setImageResource(R.drawable.ic_visibility);
        } else {
            tvBalance.setText("•••••••• đ");
            btnToggleBalance.setImageResource(R.drawable.ic_visibility_off);
        }
    }
}
