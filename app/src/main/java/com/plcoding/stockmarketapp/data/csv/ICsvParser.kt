package com.plcoding.stockmarketapp.data.csv

import java.io.InputStream

interface ICsvParser<T> {
    suspend fun parse(inputStream: InputStream): List<T>
}