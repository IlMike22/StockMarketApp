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
                    intradayInfo.date.dayOfMonth == LocalDate.now().minusDays(1).dayOfMonth
                }.sortedBy { intradayInfo ->
                    intradayInfo.date.hour
                }
                .also { csvReader.close() }
        }
    }
}