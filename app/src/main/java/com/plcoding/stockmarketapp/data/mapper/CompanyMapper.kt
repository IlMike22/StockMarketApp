package com.plcoding.stockmarketapp.data.mapper

import com.plcoding.stockmarketapp.data.local.CompanyListingEntity
import com.plcoding.stockmarketapp.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyListing(): CompanyListing =
    CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )

fun CompanyListing.toCompanyListingEntity(): CompanyListingEntity =
    CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )