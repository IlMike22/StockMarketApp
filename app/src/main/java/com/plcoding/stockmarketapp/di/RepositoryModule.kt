package com.plcoding.stockmarketapp.di

import com.plcoding.stockmarketapp.data.csv.CompanyListingsParser
import com.plcoding.stockmarketapp.data.csv.ICsvParser
import com.plcoding.stockmarketapp.data.csv.IntradayParser
import com.plcoding.stockmarketapp.data.repository.StockRepository
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.IStockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCompanyListingsParser(
        parser: CompanyListingsParser
    ): ICsvParser<CompanyListing>

    @Binds
    abstract fun bindRepository(
        repository: StockRepository
    ): IStockRepository

    @Binds
    abstract fun bindIntradayParser(
        parser: IntradayParser
    ): ICsvParser<IntradayInfo>
}
