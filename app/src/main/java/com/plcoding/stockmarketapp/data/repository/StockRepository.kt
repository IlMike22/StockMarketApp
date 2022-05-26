package com.plcoding.stockmarketapp.data.repository

import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.toCompanyListing
import com.plcoding.stockmarketapp.data.remote.IStockApi
import com.plcoding.stockmarketapp.domain.model.CompanyListing
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
    val api: IStockApi,
    val database: StockDatabase
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
                response.byteStream() // TODO parse csv file but in separte class (SOLID)
            } catch (exception: IOException) {
                exception.printStackTrace()
                emit(Resource.Error("Could not load data."))
            } catch (exception: HttpException) {
                exception.printStackTrace()
                emit(Resource.Error(exception.message ?: "Unknown http error."))
            }
        }
    }
}