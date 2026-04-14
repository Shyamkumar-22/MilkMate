package com.example.milkmate.di;

import android.content.Context;

import com.example.milkmate.data.local.MilkmateDatabase;
import com.example.milkmate.data.local.dao.CustomerDao;
import com.example.milkmate.data.local.dao.MilkEntryDao;
import com.example.milkmate.data.local.dao.MilkRateDao;
import com.example.milkmate.data.local.dao.PaymentDao;
import com.example.milkmate.data.repository.CustomerRepository;
import com.example.milkmate.data.repository.MilkEntryRepository;
import com.example.milkmate.data.repository.MilkRateRepository;
import com.example.milkmate.data.repository.PaymentRepository;
import com.example.milkmate.utils.AppExecutors;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class AppModule {

    @Provides
    public static MilkmateDatabase provideDatabase(@ApplicationContext Context context) {
        return MilkmateDatabase.getInstance(context);
    }

    @Provides
    public static CustomerDao provideCustomerDao(MilkmateDatabase db) {
        return db.customerDao();
    }

    @Provides
    public static MilkEntryDao provideMilkEntryDao(MilkmateDatabase db) {
        return db.milkEntryDao();
    }

    @Provides
    public static MilkRateDao provideMilkRateDao(MilkmateDatabase db) {
        return db.milkRateDao();
    }

    @Provides
    public static PaymentDao providePaymentDao(MilkmateDatabase db) {
        return db.paymentDao();
    }

    @Provides
    public static AppExecutors provideExecutors() {
        return new AppExecutors();
    }

    @Provides
    public static CustomerRepository provideCustomerRepository(CustomerDao dao, AppExecutors executors) {
        return new CustomerRepository(dao, executors);
    }

    @Provides
    public static MilkEntryRepository provideMilkEntryRepository(MilkEntryDao entryDao,
                                                                 MilkRateDao rateDao,
                                                                 AppExecutors executors) {
        return new MilkEntryRepository(entryDao, rateDao, executors);
    }

    @Provides
    public static MilkRateRepository provideMilkRateRepository(MilkRateDao dao, AppExecutors executors) {
        return new MilkRateRepository(dao, executors);
    }

    @Provides
    public static PaymentRepository providePaymentRepository(PaymentDao dao,
                                                              MilkEntryRepository entryRepo,
                                                              AppExecutors executors) {
        return new PaymentRepository(dao, entryRepo, executors);
    }
}
