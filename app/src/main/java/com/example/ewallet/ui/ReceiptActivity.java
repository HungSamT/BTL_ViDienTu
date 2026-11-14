package com.example.ewallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ewallet.R;
import com.example.ewallet.database.DatabaseHelper;
import com.example.ewallet.model.Transaction;
import com.example.ewallet.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ReceiptActivity extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION_ID = "EXTRA_TRANSACTION_ID";

    private TextView tvReceiptAmount, tvReceiptTxId, tvReceiptType, tvCounterpartyLabel;
    private TextView tvReceiptCounterpartyName, tvReceiptCounterpartyInfo, tvReceiptNote;
    private TextView tvReceiptBalanceAfter, tvReceiptCreatedAt;
    private MaterialButton btnBackToHome;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        dbHelper = DatabaseHelper.getInstance(this);

        initViews();

        long txId = getIntent().getLongExtra(EXTRA_TRANSACTION_ID, -1);
        if (txId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin giao dịch", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTransactionDetails(txId);
    }

    private void initViews() {
        tvReceiptAmount = findViewById(R.id.tvReceiptAmount);
        tvReceiptTxId = findViewById(R.id.tvReceiptTxId);
        tvReceiptType = findViewById(R.id.tvReceiptType);
        tvCounterpartyLabel = findViewById(R.id.tvCounterpartyLabel);
        tvReceiptCounterpartyName = findViewById(R.id.tvReceiptCounterpartyName);
        tvReceiptCounterpartyInfo = findViewById(R.id.tvReceiptCounterpartyInfo);
        tvReceiptNote = findViewById(R.id.tvReceiptNote);
        tvReceiptBalanceAfter = findViewById(R.id.tvReceiptBalanceAfter);
        tvReceiptCreatedAt = findViewById(R.id.tvReceiptCreatedAt);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadTransactionDetails(long txId) {
        Transaction tx = dbHelper.getTransactionById(txId);
        if (tx == null) {
            Toast.makeText(this, "Giao dịch không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String type = tx.getType();
        boolean isMoneyIn = Transaction.TYPE_TOP_UP.equals(type) || Transaction.TYPE_TRANSFER_IN.equals(type);

        int colorIn = ContextCompat.getColor(this, R.color.money_in);
        int colorOut = ContextCompat.getColor(this, R.color.money_out);

        tvReceiptAmount.setText(CurrencyUtils.formatSignedVND(type, tx.getAmount()));
        tvReceiptAmount.setTextColor(isMoneyIn ? colorIn : colorOut);

        tvReceiptTxId.setText(String.format(Locale.getDefault(), "#EW%05d", tx.getId()));

        if (Transaction.TYPE_TOP_UP.equals(type)) {
            tvReceiptType.setText("Nạp tiền vào ví");
            tvCounterpartyLabel.setText("Ngân hàng");
        } else if (Transaction.TYPE_WITHDRAW.equals(type)) {
            tvReceiptType.setText("Rút tiền về ngân hàng");
            tvCounterpartyLabel.setText("Ngân hàng");
        } else if (Transaction.TYPE_TRANSFER_OUT.equals(type)) {
            tvReceiptType.setText("Chuyển tiền đến ví");
            tvCounterpartyLabel.setText("Người nhận");
        } else {
            tvReceiptType.setText("Nhận tiền từ ví");
            tvCounterpartyLabel.setText("Người gửi");
        }

        tvReceiptCounterpartyName.setText(tx.getCounterpartyName());
        tvReceiptCounterpartyInfo.setText(tx.getCounterpartyInfo());
        tvReceiptNote.setText((tx.getNote() != null && !tx.getNote().isEmpty()) ? tx.getNote() : "--");
        tvReceiptBalanceAfter.setText(CurrencyUtils.formatVND(tx.getBalanceAfter()));
        tvReceiptCreatedAt.setText(tx.getCreatedAt());
    }
}
