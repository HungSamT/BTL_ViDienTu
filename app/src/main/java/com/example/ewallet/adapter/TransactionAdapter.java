package com.example.ewallet.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ewallet.R;
import com.example.ewallet.model.Transaction;
import com.example.ewallet.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    private List<Transaction> transactionList = new ArrayList<>();
    private final OnTransactionClickListener clickListener;

    public TransactionAdapter(List<Transaction> list, OnTransactionClickListener clickListener) {
        if (list != null) {
            this.transactionList = list;
        }
        this.clickListener = clickListener;
    }

    public void setData(List<Transaction> list) {
        this.transactionList = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction tx = transactionList.get(position);
        Context context = holder.itemView.getContext();

        String type = tx.getType();
        boolean isMoneyIn = Transaction.TYPE_TOP_UP.equals(type) || Transaction.TYPE_TRANSFER_IN.equals(type);

        String title;
        int iconRes;

        if (Transaction.TYPE_TOP_UP.equals(type)) {
            title = "Nạp tiền từ " + tx.getCounterpartyName();
            iconRes = R.drawable.ic_top_up;
        } else if (Transaction.TYPE_TRANSFER_IN.equals(type)) {
            title = "Nhận tiền từ " + tx.getCounterpartyName();
            iconRes = R.drawable.ic_transfer;
        } else if (Transaction.TYPE_WITHDRAW.equals(type)) {
            title = "Rút tiền về " + tx.getCounterpartyName();
            iconRes = R.drawable.ic_withdraw;
        } else {
            title = "Chuyển tiền đến " + tx.getCounterpartyName();
            iconRes = R.drawable.ic_transfer;
        }

        holder.tvTxTitle.setText(title);
        holder.tvTxDate.setText(tx.getCreatedAt());

        int colorIn = ContextCompat.getColor(context, R.color.money_in);
        int colorOut = ContextCompat.getColor(context, R.color.money_out);

        if (isMoneyIn) {
            holder.tvTxAmount.setText(CurrencyUtils.formatSignedVND(type, tx.getAmount()));
            holder.tvTxAmount.setTextColor(colorIn);
            holder.ivTxIcon.setImageResource(iconRes);
            holder.ivTxIcon.setBackgroundResource(R.drawable.bg_circle_green);
            holder.ivTxIcon.setImageTintList(ColorStateList.valueOf(colorIn));
        } else {
            holder.tvTxAmount.setText(CurrencyUtils.formatSignedVND(type, tx.getAmount()));
            holder.tvTxAmount.setTextColor(colorOut);
            holder.ivTxIcon.setImageResource(iconRes);
            holder.ivTxIcon.setBackgroundResource(R.drawable.bg_circle_red);
            holder.ivTxIcon.setImageTintList(ColorStateList.valueOf(colorOut));
        }

        if (tx.getNote() != null && !tx.getNote().trim().isEmpty()) {
            holder.tvTxNote.setVisibility(View.VISIBLE);
            holder.tvTxNote.setText(tx.getNote());
        } else {
            holder.tvTxNote.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onTransactionClick(tx);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTxIcon;
        TextView tvTxTitle;
        TextView tvTxDate;
        TextView tvTxNote;
        TextView tvTxAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTxIcon = itemView.findViewById(R.id.ivTxIcon);
            tvTxTitle = itemView.findViewById(R.id.tvTxTitle);
            tvTxDate = itemView.findViewById(R.id.tvTxDate);
            tvTxNote = itemView.findViewById(R.id.tvTxNote);
            tvTxAmount = itemView.findViewById(R.id.tvTxAmount);
        }
    }
}
