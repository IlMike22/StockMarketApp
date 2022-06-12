package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.mapper.toIntradayInfo
import com.plcoding.stockmarketapp.data.remote.dto.IntraDayInfoDto
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayParser @Inject constructor() : ICsvParser<IntradayInfo> {
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))

        return withContext(Dispatchers.IO) {
            csvReader.readAll()
                .drop(1) // remove first item because it`s only the header info
                .mapNotNull { entry ->
                    val timestamp = entry.getOrNull(0) ?: return@mapNotNull null
                    val close = entry.getOrNull(4) ?: return@mapNotNull null
                    val dto = IntraDayInfoDto(
                        timestamp = timestamp,
                        close = close.toDouble()
                    )

                    dto.toIntradayInfo()
                }.filter { intradayInfo ->
                    // TODO filter out if today and / or yesterday it was sunday or saturday due on this days there are no values!
                    // TODO experimental set 4 instead of 1
                    // other TODO: Caching for CompanyInfo (as well as for CompanyListings we already have)
                    intradayInfo.date.dayOfMonth == LocalDate.now().minusDays(4).dayOfMonth
                }.sortedBy { intradayInfo ->
                    intradayInfo.date.hour
                }
                .also { csvReader.close() }
        }
    }
}