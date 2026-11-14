package com.example.ewallet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ewallet.R;
import com.example.ewallet.model.LinkedBank;
import com.example.ewallet.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

    public interface OnBankDeleteListener {
        void onBankDelete(LinkedBank bank, int position);
    }

    private List<LinkedBank> bankList = new ArrayList<>();
    private final OnBankDeleteListener deleteListener;

    public BankAdapter(List<LinkedBank> list, OnBankDeleteListener deleteListener) {
        if (list != null) {
            this.bankList = list;
        }
        this.deleteListener = deleteListener;
    }

    public void setData(List<LinkedBank> list) {
        this.bankList = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        LinkedBank bank = bankList.get(position);
        holder.tvBankName.setText(bank.getBankName());
        holder.tvAccountNumber.setText("STK: " + bank.getAccountNumber());
        holder.tvMockBalance.setText("Số dư thẻ: " + CurrencyUtils.formatVND(bank.getMockBalance()));

        holder.btnDeleteBank.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onBankDelete(bank, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    static class BankViewHolder extends RecyclerView.ViewHolder {
        TextView tvBankName;
        TextView tvAccountNumber;
        TextView tvMockBalance;
        ImageButton btnDeleteBank;

        public BankViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBankName = itemView.findViewById(R.id.tvBankName);
            tvAccountNumber = itemView.findViewById(R.id.tvAccountNumber);
            tvMockBalance = itemView.findViewById(R.id.tvMockBalance);
            btnDeleteBank = itemView.findViewById(R.id.btnDeleteBank);
        }
    }
}
