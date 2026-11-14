package com.example.ewallet.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ewallet.R;
import com.example.ewallet.adapter.TransactionAdapter;
import com.example.ewallet.database.DatabaseHelper;
import com.example.ewallet.model.Transaction;
import com.example.ewallet.utils.CurrencyUtils;
import com.example.ewallet.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private EditText etSearchQuery;
    private MaterialButton btnFilterAll, btnFilterIn, btnFilterOut;
    private TextView tvTotalIn, tvTotalOut, tvEmptyHistory;
    private RecyclerView rvHistoryTransactions;
    private TransactionAdapter transactionAdapter;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int userId;
    private String currentFilter = "ALL";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = sessionManager.getUserId();

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterIn = findViewById(R.id.btnFilterIn);
        btnFilterOut = findViewById(R.id.btnFilterOut);
        tvTotalIn = findViewById(R.id.tvTotalIn);
        tvTotalOut = findViewById(R.id.tvTotalOut);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        rvHistoryTransactions = findViewById(R.id.rvHistoryTransactions);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvHistoryTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(null, transaction -> {
            Intent intent = new Intent(HistoryActivity.this, ReceiptActivity.class);
            intent.putExtra(ReceiptActivity.EXTRA_TRANSACTION_ID, transaction.getId());
            startActivity(intent);
        });
        rvHistoryTransactions.setAdapter(transactionAdapter);
    }

    private void setupListeners() {
        btnFilterAll.setOnClickListener(v -> setFilter("ALL"));
        btnFilterIn.setOnClickListener(v -> setFilter("IN"));
        btnFilterOut.setOnClickListener(v -> setFilter("OUT"));

        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = (s != null) ? s.toString().trim() : "";
                loadTransactions();
            }
        });
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        updateFilterButtonStyles();
        loadTransactions();
    }

    private void updateFilterButtonStyles() {
        int colorPrimary = ContextCompat.getColor(this, R.color.primary);
        int colorWhite = ContextCompat.getColor(this, R.color.white);
        int colorSecondary = ContextCompat.getColor(this, R.color.text_secondary);
        int colorDivider = ContextCompat.getColor(this, R.color.divider);

        // Reset all
        btnFilterAll.setBackgroundTintList(ColorStateList.valueOf(colorWhite));
        btnFilterAll.setTextColor(colorSecondary);
        btnFilterAll.setStrokeColor(ColorStateList.valueOf(colorDivider));

        btnFilterIn.setBackgroundTintList(ColorStateList.valueOf(colorWhite));
        btnFilterIn.setTextColor(colorSecondary);
        btnFilterIn.setStrokeColor(ColorStateList.valueOf(colorDivider));

        btnFilterOut.setBackgroundTintList(ColorStateList.valueOf(colorWhite));
        btnFilterOut.setTextColor(colorSecondary);
        btnFilterOut.setStrokeColor(ColorStateList.valueOf(colorDivider));

        // Highlight active
        MaterialButton activeButton = null;
        if ("ALL".equals(currentFilter)) {
            activeButton = btnFilterAll;
        } else if ("IN".equals(currentFilter)) {
            activeButton = btnFilterIn;
        } else if ("OUT".equals(currentFilter)) {
            activeButton = btnFilterOut;
        }

        if (activeButton != null) {
            activeButton.setBackgroundTintList(ColorStateList.valueOf(colorPrimary));
            activeButton.setTextColor(colorWhite);
            activeButton.setStrokeColor(ColorStateList.valueOf(colorPrimary));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }

    private void loadTransactions() {
        List<Transaction> list = dbHelper.getTransactions(userId, currentFilter, currentSearchQuery, 0);

        if (list.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            rvHistoryTransactions.setVisibility(View.GONE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
            rvHistoryTransactions.setVisibility(View.VISIBLE);
            transactionAdapter.setData(list);
        }

        calculateSummary(list);
    }

    private void calculateSummary(List<Transaction> list) {
        long sumIn = 0;
        long sumOut = 0;

        for (Transaction tx : list) {
            String type = tx.getType();
            if (Transaction.TYPE_TOP_UP.equals(type) || Transaction.TYPE_TRANSFER_IN.equals(type)) {
                sumIn += tx.getAmount();
            } else {
                sumOut += tx.getAmount();
            }
        }

        tvTotalIn.setText("Tổng thu: + " + CurrencyUtils.formatVND(sumIn));
        tvTotalOut.setText("Tổng chi: - " + CurrencyUtils.formatVND(sumOut));
    }
}
