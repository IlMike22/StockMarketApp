package com.plcoding.stockmarketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.plcoding.stockmarketapp.data.local.StockDatabase.Companion.DATABASE_VERSION

@Database(
    entities = [CompanyListingEntity::class],
    version = DATABASE_VERSION
)
abstract class StockDatabase : RoomDatabase() {
    abstract val dao: IStockDao

    companion object {
        const val DATABASE_VERSION = 1
    }
}