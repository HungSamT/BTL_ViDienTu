package com.example.ewallet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ewallet.model.LinkedBank;
import com.example.ewallet.model.Transaction;
import com.example.ewallet.model.User;
import com.example.ewallet.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ewallet.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_LINKED_BANKS = "linked_banks";
    public static final String TABLE_TRANSACTIONS = "transactions";

    // Common columns
    public static final String COL_ID = "id";

    // Users columns
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_FULL_NAME = "full_name";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_PIN = "pin_code";
    public static final String COL_USER_BALANCE = "balance";
    public static final String COL_USER_CREATED_AT = "created_at";

    // Linked banks columns
    public static final String COL_BANK_USER_ID = "user_id";
    public static final String COL_BANK_NAME = "bank_name";
    public static final String COL_BANK_ACCOUNT_NUMBER = "account_number";
    public static final String COL_BANK_MOCK_BALANCE = "mock_balance";

    // Transactions columns
    public static final String COL_TX_USER_ID = "user_id";
    public static final String COL_TX_TYPE = "type";
    public static final String COL_TX_AMOUNT = "amount";
    public static final String COL_TX_COUNTERPARTY_NAME = "counterparty_name";
    public static final String COL_TX_COUNTERPARTY_INFO = "counterparty_info";
    public static final String COL_TX_NOTE = "note";
    public static final String COL_TX_BALANCE_AFTER = "balance_after";
    public static final String COL_TX_CREATED_AT = "created_at";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_PHONE + " TEXT UNIQUE NOT NULL, "
                + COL_USER_FULL_NAME + " TEXT NOT NULL, "
                + COL_USER_PASSWORD + " TEXT NOT NULL, "
                + COL_USER_PIN + " TEXT NOT NULL, "
                + COL_USER_BALANCE + " INTEGER NOT NULL DEFAULT 0, "
                + COL_USER_CREATED_AT + " TEXT NOT NULL"
                + ");";

        String createBanksTable = "CREATE TABLE " + TABLE_LINKED_BANKS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_BANK_USER_ID + " INTEGER NOT NULL, "
                + COL_BANK_NAME + " TEXT NOT NULL, "
                + COL_BANK_ACCOUNT_NUMBER + " TEXT NOT NULL, "
                + COL_BANK_MOCK_BALANCE + " INTEGER NOT NULL DEFAULT 5000000, "
                + "FOREIGN KEY(" + COL_BANK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE"
                + ");";

        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TX_USER_ID + " INTEGER NOT NULL, "
                + COL_TX_TYPE + " TEXT NOT NULL, "
                + COL_TX_AMOUNT + " INTEGER NOT NULL, "
                + COL_TX_COUNTERPARTY_NAME + " TEXT NOT NULL, "
                + COL_TX_COUNTERPARTY_INFO + " TEXT NOT NULL, "
                + COL_TX_NOTE + " TEXT DEFAULT '', "
                + COL_TX_BALANCE_AFTER + " INTEGER NOT NULL, "
                + COL_TX_CREATED_AT + " TEXT NOT NULL"
                + ");";

        db.execSQL(createUsersTable);
        db.execSQL(createBanksTable);
        db.execSQL(createTransactionsTable);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINKED_BANKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // User 1: NGUYEN VAN A
        ContentValues u1 = new ContentValues();
        u1.put(COL_USER_PHONE, "0901234567");
        u1.put(COL_USER_FULL_NAME, "NGUYEN VAN A");
        u1.put(COL_USER_PASSWORD, "123456");
        u1.put(COL_USER_PIN, "111111");
        u1.put(COL_USER_BALANCE, 2000000L);
        u1.put(COL_USER_CREATED_AT, "10/11/2026 08:00:00");
        long u1Id = db.insert(TABLE_USERS, null, u1);

        // User 1 Banks
        ContentValues b1 = new ContentValues();
        b1.put(COL_BANK_USER_ID, u1Id);
        b1.put(COL_BANK_NAME, "Vietcombank");
        b1.put(COL_BANK_ACCOUNT_NUMBER, "9876543210");
        b1.put(COL_BANK_MOCK_BALANCE, 10000000L);
        db.insert(TABLE_LINKED_BANKS, null, b1);

        ContentValues b2 = new ContentValues();
        b2.put(COL_BANK_USER_ID, u1Id);
        b2.put(COL_BANK_NAME, "MB Bank");
        b2.put(COL_BANK_ACCOUNT_NUMBER, "0901234567");
        b2.put(COL_BANK_MOCK_BALANCE, 5000000L);
        db.insert(TABLE_LINKED_BANKS, null, b2);

        // User 2: TRAN THI B
        ContentValues u2 = new ContentValues();
        u2.put(COL_USER_PHONE, "0987654321");
        u2.put(COL_USER_FULL_NAME, "TRAN THI B");
        u2.put(COL_USER_PASSWORD, "123456");
        u2.put(COL_USER_PIN, "222222");
        u2.put(COL_USER_BALANCE, 1500000L);
        u2.put(COL_USER_CREATED_AT, "11/11/2026 09:30:00");
        long u2Id = db.insert(TABLE_USERS, null, u2);

        // User 2 Bank
        ContentValues b3 = new ContentValues();
        b3.put(COL_BANK_USER_ID, u2Id);
        b3.put(COL_BANK_NAME, "Techcombank");
        b3.put(COL_BANK_ACCOUNT_NUMBER, "1903456789");
        b3.put(COL_BANK_MOCK_BALANCE, 8000000L);
        db.insert(TABLE_LINKED_BANKS, null, b3);

        // Seed Transactions for User 1
        ContentValues tx1 = new ContentValues();
        tx1.put(COL_TX_USER_ID, u1Id);
        tx1.put(COL_TX_TYPE, Transaction.TYPE_TOP_UP);
        tx1.put(COL_TX_AMOUNT, 1000000L);
        tx1.put(COL_TX_COUNTERPARTY_NAME, "Vietcombank");
        tx1.put(COL_TX_COUNTERPARTY_INFO, "9876543210");
        tx1.put(COL_TX_NOTE, "Nap tien tu Vietcombank");
        tx1.put(COL_TX_BALANCE_AFTER, 1700000L);
        tx1.put(COL_TX_CREATED_AT, "12/11/2026 10:15:00");
        db.insert(TABLE_TRANSACTIONS, null, tx1);

        ContentValues tx2 = new ContentValues();
        tx2.put(COL_TX_USER_ID, u1Id);
        tx2.put(COL_TX_TYPE, Transaction.TYPE_TRANSFER_OUT);
        tx2.put(COL_TX_AMOUNT, 200000L);
        tx2.put(COL_TX_COUNTERPARTY_NAME, "TRAN THI B");
        tx2.put(COL_TX_COUNTERPARTY_INFO, "0987654321");
        tx2.put(COL_TX_NOTE, "Chuyen tien an trua");
        tx2.put(COL_TX_BALANCE_AFTER, 1500000L);
        tx2.put(COL_TX_CREATED_AT, "13/11/2026 12:30:00");
        db.insert(TABLE_TRANSACTIONS, null, tx2);

        ContentValues tx2b = new ContentValues();
        tx2b.put(COL_TX_USER_ID, u2Id);
        tx2b.put(COL_TX_TYPE, Transaction.TYPE_TRANSFER_IN);
        tx2b.put(COL_TX_AMOUNT, 200000L);
        tx2b.put(COL_TX_COUNTERPARTY_NAME, "NGUYEN VAN A");
        tx2b.put(COL_TX_COUNTERPARTY_INFO, "0901234567");
        tx2b.put(COL_TX_NOTE, "Chuyen tien an trua");
        tx2b.put(COL_TX_BALANCE_AFTER, 1700000L);
        tx2b.put(COL_TX_CREATED_AT, "13/11/2026 12:30:00");
        db.insert(TABLE_TRANSACTIONS, null, tx2b);

        ContentValues tx3 = new ContentValues();
        tx3.put(COL_TX_USER_ID, u1Id);
        tx3.put(COL_TX_TYPE, Transaction.TYPE_TRANSFER_IN);
        tx3.put(COL_TX_AMOUNT, 500000L);
        tx3.put(COL_TX_COUNTERPARTY_NAME, "TRAN THI B");
        tx3.put(COL_TX_COUNTERPARTY_INFO, "0987654321");
        tx3.put(COL_TX_NOTE, "Tra tien phong tro");
        tx3.put(COL_TX_BALANCE_AFTER, 2000000L);
        tx3.put(COL_TX_CREATED_AT, "14/11/2026 09:00:00");
        db.insert(TABLE_TRANSACTIONS, null, tx3);

        ContentValues tx3b = new ContentValues();
        tx3b.put(COL_TX_USER_ID, u2Id);
        tx3b.put(COL_TX_TYPE, Transaction.TYPE_TRANSFER_OUT);
        tx3b.put(COL_TX_AMOUNT, 500000L);
        tx3b.put(COL_TX_COUNTERPARTY_NAME, "NGUYEN VAN A");
        tx3b.put(COL_TX_COUNTERPARTY_INFO, "0901234567");
        tx3b.put(COL_TX_NOTE, "Tra tien phong tro");
        tx3b.put(COL_TX_BALANCE_AFTER, 1200000L);
        tx3b.put(COL_TX_CREATED_AT, "14/11/2026 09:00:00");
        db.insert(TABLE_TRANSACTIONS, null, tx3b);
    }

    // --- Authentication & User Operations ---

    public User login(String phone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_PHONE + " = ? AND " + COL_USER_PASSWORD + " = ?",
                new String[]{phone, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    public boolean isPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID},
                COL_USER_PHONE + " = ?",
                new String[]{phone}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    public long registerUser(String phone, String fullName, String password, String pinCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newUserId = -1;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_USER_PHONE, phone);
            values.put(COL_USER_FULL_NAME, fullName.toUpperCase().trim());
            values.put(COL_USER_PASSWORD, password);
            values.put(COL_USER_PIN, pinCode);
            values.put(COL_USER_BALANCE, 50000L); // Tặng 50.000 đ tân thủ
            values.put(COL_USER_CREATED_AT, CurrencyUtils.getCurrentDateTime());

            newUserId = db.insert(TABLE_USERS, null, values);

            if (newUserId != -1) {
                // Tạo bản ghi TOP_UP khuyến mãi tân thủ
                ContentValues tx = new ContentValues();
                tx.put(COL_TX_USER_ID, newUserId);
                tx.put(COL_TX_TYPE, Transaction.TYPE_TOP_UP);
                tx.put(COL_TX_AMOUNT, 50000L);
                tx.put(COL_TX_COUNTERPARTY_NAME, "Hệ Thống Ví");
                tx.put(COL_TX_COUNTERPARTY_INFO, "Khuyến mãi tân thủ");
                tx.put(COL_TX_NOTE, "Quà chào mừng thành viên mới");
                tx.put(COL_TX_BALANCE_AFTER, 50000L);
                tx.put(COL_TX_CREATED_AT, CurrencyUtils.getCurrentDateTime());
                db.insert(TABLE_TRANSACTIONS, null, tx);

                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
        return newUserId;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    public User getUserByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_PHONE + " = ?",
                new String[]{phone}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    public boolean verifyPin(int userId, String pinInput) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_PIN},
                COL_ID + " = ? AND " + COL_USER_PIN + " = ?",
                new String[]{String.valueOf(userId), pinInput}, null, null, null);
        boolean valid = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return valid;
    }

    public boolean updatePassword(int userId, String oldPass, String newPass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, newPass);
        int rows = db.update(TABLE_USERS, values,
                COL_ID + " = ? AND " + COL_USER_PASSWORD + " = ?",
                new String[]{String.valueOf(userId), oldPass});
        return rows > 0;
    }

    public boolean updatePin(int userId, String oldPin, String newPin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PIN, newPin);
        int rows = db.update(TABLE_USERS, values,
                COL_ID + " = ? AND " + COL_USER_PIN + " = ?",
                new String[]{String.valueOf(userId), oldPin});
        return rows > 0;
    }

    // --- Linked Bank Operations ---

    public List<LinkedBank> getLinkedBanks(int userId) {
        List<LinkedBank> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LINKED_BANKS, null,
                COL_BANK_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null, COL_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                LinkedBank bank = new LinkedBank(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BANK_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BANK_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_BANK_ACCOUNT_NUMBER)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_BANK_MOCK_BALANCE))
                );
                list.add(bank);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public LinkedBank getBankById(int bankId) {
        SQLiteDatabase db = this.getReadableDatabase();
        LinkedBank bank = null;
        Cursor cursor = db.query(TABLE_LINKED_BANKS, null,
                COL_ID + " = ?",
                new String[]{String.valueOf(bankId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            bank = new LinkedBank(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_BANK_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_BANK_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_BANK_ACCOUNT_NUMBER)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_BANK_MOCK_BALANCE))
            );
            cursor.close();
        }
        return bank;
    }

    public long addLinkedBank(int userId, String bankName, String accountNumber, long initialMockBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BANK_USER_ID, userId);
        values.put(COL_BANK_NAME, bankName);
        values.put(COL_BANK_ACCOUNT_NUMBER, accountNumber);
        values.put(COL_BANK_MOCK_BALANCE, initialMockBalance);
        return db.insert(TABLE_LINKED_BANKS, null, values);
    }

    public boolean deleteLinkedBank(int bankId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_LINKED_BANKS, COL_ID + " = ?", new String[]{String.valueOf(bankId)});
        return rows > 0;
    }

    // --- Financial Transaction Operations (All wrapped in SQLite Transactions) ---

    public long topUpFromBank(int userId, int bankId, long amount) {
        if (amount <= 0) return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        long txId = -1;

        db.beginTransaction();
        try {
            // 1. Kiểm tra ngân hàng & số dư mock_balance
            LinkedBank bank = getBankById(bankId);
            if (bank == null || bank.getUserId() != userId || bank.getMockBalance() < amount) {
                return -1;
            }

            // 2. Lấy số dư ví người dùng hiện tại
            User user = getUserById(userId);
            if (user == null) {
                return -1;
            }

            // 3. Trừ tiền ngân hàng
            ContentValues bankValues = new ContentValues();
            bankValues.put(COL_BANK_MOCK_BALANCE, bank.getMockBalance() - amount);
            db.update(TABLE_LINKED_BANKS, bankValues, COL_ID + " = ?", new String[]{String.valueOf(bankId)});

            // 4. Cộng tiền vào ví người dùng
            long newBalance = user.getBalance() + amount;
            ContentValues userValues = new ContentValues();
            userValues.put(COL_USER_BALANCE, newBalance);
            db.update(TABLE_USERS, userValues, COL_ID + " = ?", new String[]{String.valueOf(userId)});

            // 5. Thêm bản ghi giao dịch
            ContentValues txValues = new ContentValues();
            txValues.put(COL_TX_USER_ID, userId);
            txValues.put(COL_TX_TYPE, Transaction.TYPE_TOP_UP);
            txValues.put(COL_TX_AMOUNT, amount);
            txValues.put(COL_TX_COUNTERPARTY_NAME, bank.getBankName());
            txValues.put(COL_TX_COUNTERPARTY_INFO, bank.getAccountNumber());
            txValues.put(COL_TX_NOTE, "Nạp tiền từ " + bank.getBankName());
            txValues.put(COL_TX_BALANCE_AFTER, newBalance);
            txValues.put(COL_TX_CREATED_AT, CurrencyUtils.getCurrentDateTime());

            txId = db.insert(TABLE_TRANSACTIONS, null, txValues);

            if (txId != -1) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return txId;
    }

    public long withdrawToBank(int userId, int bankId, long amount) {
        if (amount <= 0) return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        long txId = -1;

        db.beginTransaction();
        try {
            // 1. Kiểm tra ngân hàng
            LinkedBank bank = getBankById(bankId);
            if (bank == null || bank.getUserId() != userId) {
                return -1;
            }

            // 2. Kiểm tra số dư ví người dùng
            User user = getUserById(userId);
            if (user == null || user.getBalance() < amount) {
                return -1;
            }

            // 3. Trừ tiền ví người dùng
            long newBalance = user.getBalance() - amount;
            ContentValues userValues = new ContentValues();
            userValues.put(COL_USER_BALANCE, newBalance);
            db.update(TABLE_USERS, userValues, COL_ID + " = ?", new String[]{String.valueOf(userId)});

            // 4. Cộng tiền ngân hàng
            ContentValues bankValues = new ContentValues();
            bankValues.put(COL_BANK_MOCK_BALANCE, bank.getMockBalance() + amount);
            db.update(TABLE_LINKED_BANKS, bankValues, COL_ID + " = ?", new String[]{String.valueOf(bankId)});

            // 5. Thêm bản ghi giao dịch
            ContentValues txValues = new ContentValues();
            txValues.put(COL_TX_USER_ID, userId);
            txValues.put(COL_TX_TYPE, Transaction.TYPE_WITHDRAW);
            txValues.put(COL_TX_AMOUNT, amount);
            txValues.put(COL_TX_COUNTERPARTY_NAME, bank.getBankName());
            txValues.put(COL_TX_COUNTERPARTY_INFO, bank.getAccountNumber());
            txValues.put(COL_TX_NOTE, "Rút tiền về " + bank.getBankName());
            txValues.put(COL_TX_BALANCE_AFTER, newBalance);
            txValues.put(COL_TX_CREATED_AT, CurrencyUtils.getCurrentDateTime());

            txId = db.insert(TABLE_TRANSACTIONS, null, txValues);

            if (txId != -1) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return txId;
    }

    public long transferMoney(int senderId, String receiverPhone, long amount, String note) {
        if (amount <= 0) return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        long senderTxId = -1;

        db.beginTransaction();
        try {
            // 1. Kiểm tra người gửi
            User sender = getUserById(senderId);
            if (sender == null || sender.getBalance() < amount) {
                return -1;
            }

            // 2. Kiểm tra người nhận
            User receiver = getUserByPhone(receiverPhone);
            if (receiver == null || receiver.getId() == senderId) {
                return -1;
            }

            // 3. Cập nhật số dư người gửi
            long senderNewBalance = sender.getBalance() - amount;
            ContentValues senderVal = new ContentValues();
            senderVal.put(COL_USER_BALANCE, senderNewBalance);
            db.update(TABLE_USERS, senderVal, COL_ID + " = ?", new String[]{String.valueOf(senderId)});

            // 4. Cập nhật số dư người nhận
            long receiverNewBalance = receiver.getBalance() + amount;
            ContentValues receiverVal = new ContentValues();
            receiverVal.put(COL_USER_BALANCE, receiverNewBalance);
            db.update(TABLE_USERS, receiverVal, COL_ID + " = ?", new String[]{String.valueOf(receiver.getId())});

            String now = CurrencyUtils.getCurrentDateTime();
            String txNote = (note == null || note.trim().isEmpty()) ? (sender.getFullName() + " chuyen tien") : note.trim();

            // 5. Ghi kép 2 bản ghi giao dịch
            // Bản ghi người gửi (TRANSFER_OUT)
            ContentValues senderTx = new ContentValues();
            senderTx.put(COL_TX_USER_ID, senderId);
            senderTx.put(COL_TX_TYPE, Transaction.TYPE_TRANSFER_OUT);
            senderTx.put(COL_TX_AMOUNT, amount);
            senderTx.put(COL_TX_COUNTERPARTY_NAME, receiver.getFullName());
            senderTx.put(COL_TX_COUNTERPARTY_INFO, receiver.getPhone());
            senderTx.put(COL_TX_NOTE, txNote);
            senderTx.put(COL_TX_BALANCE_AFTER, senderNewBalance);
            senderTx.put(COL_TX_CREATED_AT, now);
            senderTxId = db.insert(TABLE_TRANSACTIONS, null, senderTx);

            // Bản ghi người nhận (TRANSFER_IN)
            ContentValues receiverTx = new ContentValues();
            receiverTx.put(COL_TX_USER_ID, receiver.getId());
            receiverTx.put(COL_TX_TYPE, Transaction.TYPE_TRANSFER_IN);
            receiverTx.put(COL_TX_AMOUNT, amount);
            receiverTx.put(COL_TX_COUNTERPARTY_NAME, sender.getFullName());
            receiverTx.put(COL_TX_COUNTERPARTY_INFO, sender.getPhone());
            receiverTx.put(COL_TX_NOTE, txNote);
            receiverTx.put(COL_TX_BALANCE_AFTER, receiverNewBalance);
            receiverTx.put(COL_TX_CREATED_AT, now);
            long receiverTxId = db.insert(TABLE_TRANSACTIONS, null, receiverTx);

            if (senderTxId != -1 && receiverTxId != -1) {
                db.setTransactionSuccessful();
            } else {
                senderTxId = -1;
            }
        } finally {
            db.endTransaction();
        }

        return senderTxId;
    }

    public List<Transaction> getTransactions(int userId, String filterType, String searchQuery, int limit) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder selection = new StringBuilder(COL_TX_USER_ID + " = ?");
        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add(String.valueOf(userId));

        // Filter type: ALL, IN, OUT
        if ("IN".equalsIgnoreCase(filterType)) {
            selection.append(" AND (" + COL_TX_TYPE + " = ? OR " + COL_TX_TYPE + " = ?)");
            selectionArgs.add(Transaction.TYPE_TOP_UP);
            selectionArgs.add(Transaction.TYPE_TRANSFER_IN);
        } else if ("OUT".equalsIgnoreCase(filterType)) {
            selection.append(" AND (" + COL_TX_TYPE + " = ? OR " + COL_TX_TYPE + " = ?)");
            selectionArgs.add(Transaction.TYPE_WITHDRAW);
            selectionArgs.add(Transaction.TYPE_TRANSFER_OUT);
        }

        // Search query
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String queryPattern = "%" + searchQuery.trim() + "%";
            selection.append(" AND (" + COL_TX_COUNTERPARTY_NAME + " LIKE ? OR "
                    + COL_TX_COUNTERPARTY_INFO + " LIKE ? OR "
                    + COL_TX_NOTE + " LIKE ?)");
            selectionArgs.add(queryPattern);
            selectionArgs.add(queryPattern);
            selectionArgs.add(queryPattern);
        }

        String limitClause = (limit > 0) ? String.valueOf(limit) : null;

        Cursor cursor = db.query(TABLE_TRANSACTIONS, null,
                selection.toString(),
                selectionArgs.toArray(new String[0]),
                null, null,
                COL_ID + " DESC",
                limitClause);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction tx = cursorToTransaction(cursor);
                list.add(tx);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }

    public Transaction getTransactionById(long txId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Transaction tx = null;
        Cursor cursor = db.query(TABLE_TRANSACTIONS, null,
                COL_ID + " = ?",
                new String[]{String.valueOf(txId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            tx = cursorToTransaction(cursor);
            cursor.close();
        }
        return tx;
    }

    // --- Helpers ---

    private User cursorToUser(Cursor cursor) {
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_FULL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PIN)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_BALANCE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_CREATED_AT))
        );
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        return new Transaction(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_TX_USER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TX_TYPE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TX_AMOUNT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TX_COUNTERPARTY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TX_COUNTERPARTY_INFO)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TX_NOTE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TX_BALANCE_AFTER)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TX_CREATED_AT))
        );
    }
}
