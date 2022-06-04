package com.plcoding.stockmarketapp.data.remote

import com.plcoding.stockmarketapp.data.remote.dto.CompanyInfoDto
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface IStockApi {
    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(
        @Query("apikey") apiKey: String = API_KEY
    ): ResponseBody

    @GET("query?function=OVERVIEW")
    suspend fun getCompanyInfo(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("symbol") symbol: String
    ):CompanyInfoDto

    @GET("query?function=TIME_SERIES_INTRADAY&interval=60min&datatype=csv")
    suspend fun getIntradayInfo(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("symbol") symbol: String
    ):ResponseBody

    companion object {
        const val API_KEY = "LWKQAUP8YVDLVF2A"
        const val BASE_URL = "https://alphavantage.co"
    }
}

