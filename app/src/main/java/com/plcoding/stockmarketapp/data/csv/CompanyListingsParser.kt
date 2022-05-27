package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingsParser @Inject constructor() : ICsvParser<CompanyListing> {
    override suspend fun parse(stream: InputStream): List<CompanyListing> {
        val csvReader = CSVReader(InputStreamReader(stream))

        return withContext(Dispatchers.IO) {
            csvReader.readAll()
                .drop(1) // remove first item because it`s only the header info
                .mapNotNull { entry ->
                    val symbol = entry.getOrNull(0)
                    val name = entry.getOrNull(1)
                    val exchange = entry.getOrNull(2)
                    CompanyListing(
                        name = name ?: return@mapNotNull null,
                        symbol = symbol ?: return@mapNotNull null,
                        exchange = exchange ?: return@mapNotNull null
                    )
                }
                .also { csvReader.close() }
        }
    }
}