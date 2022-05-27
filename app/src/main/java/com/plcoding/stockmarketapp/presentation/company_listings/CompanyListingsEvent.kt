package com.plcoding.stockmarketapp.presentation.company_listings

sealed class CompanyListingsEvent {
    data class OnSearchClick(val query: String) : CompanyListingsEvent()
    object OnPullToRefresh : CompanyListingsEvent()
}
