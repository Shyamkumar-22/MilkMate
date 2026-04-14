package com.example.milkmate.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.milkmate.data.local.dao.CustomerDao;
import com.example.milkmate.data.local.dao.MilkEntryDao;
import com.example.milkmate.data.local.dao.MilkRateDao;
import com.example.milkmate.data.local.dao.PaymentDao;
import com.example.milkmate.data.local.entity.CustomerEntity;
import com.example.milkmate.data.local.entity.MilkEntryEntity;
import com.example.milkmate.data.local.entity.MilkRateEntity;
import com.example.milkmate.data.local.entity.PaymentEntity;

/**
 * Room database for MilkMate - offline-first.
 * All DAOs are exposed. Use Executors for background operations.
 */
@Database(
    entities = {
        CustomerEntity.class,
        MilkEntryEntity.class,
        MilkRateEntity.class,
        PaymentEntity.class
    },
    version = 2,
    exportSchema = false
)
public abstract class MilkmateDatabase extends RoomDatabase {

    private static final String DB_NAME = "milkmate_db";

    public abstract CustomerDao customerDao();
    public abstract MilkEntryDao milkEntryDao();
    public abstract MilkRateDao milkRateDao();
    public abstract PaymentDao paymentDao();

    private static volatile MilkmateDatabase INSTANCE;

    public static MilkmateDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MilkmateDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MilkmateDatabase.class,
                            DB_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
