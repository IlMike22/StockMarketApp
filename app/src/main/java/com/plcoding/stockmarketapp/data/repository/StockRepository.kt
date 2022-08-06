package com.plcoding.stockmarketapp.data.repository

import com.plcoding.stockmarketapp.data.csv.ICsvParser
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.toCompanyInfo
import com.plcoding.stockmarketapp.data.mapper.toCompanyListing
import com.plcoding.stockmarketapp.data.mapper.toCompanyListingEntity
import com.plcoding.stockmarketapp.data.remote.IStockApi
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.IStockRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val api: IStockApi,
    private val companyListingsParser: ICsvParser<CompanyListing>,
    private val intradayParser: ICsvParser<IntradayInfo>,
    database: StockDatabase
) : IStockRepository {
    private val dao = database.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(
                Resource.Success(
                    data = localListings.map { localListingEntry ->
                        localListingEntry.toCompanyListing()
                    })
            )

            val isInitialState = localListings.isEmpty() && query.isBlank()
            val isLoadFromCache = !isInitialState && !fetchFromRemote
            if (isLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (exception: IOException) {
                exception.printStackTrace()
                emit(Resource.Error("Could not load data."))
                null
            } catch (exception: HttpException) {
                exception.printStackTrace()
                emit(Resource.Error(exception.message ?: "Unknown http error."))
                null
            }
            remoteListings?.let { listings ->
                dao.clearCompanyListing()
                dao.insertCompanyListing(
                    listings.map { listing -> listing.toCompanyListingEntity() }
                )

                emit(
                    Resource.Success(
                        dao
                            .searchCompanyListing("") // stick to single source of truth
                            .map { entity ->
                                entity.toCompanyListing()
                            })
                )

                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = Resource.Success(api.getCompanyInfo(symbol = symbol).toCompanyInfo())
            result
        } catch (exception: IOException) {
            exception.printStackTrace()
            Resource.Error(message = "Couldnt load intraday info")
        } catch (exception: HttpException) {
            exception.printStackTrace()
            Resource.Error(message = exception.message ?: "Unknown http exception comapny info")
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol = symbol)
            val results = intradayParser.parse(response.byteStream())
            Resource.Success(results)
        } catch (exception: IOException) {
            Resource.Error(message = exception.message ?: "Couldnt load intraday info from api")
        } catch (exception: HttpException) {
            Resource.Error(message = exception.message ?: "Unknown http exception intraday info")
        }
    }
}