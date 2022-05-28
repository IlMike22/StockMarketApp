package com.plcoding.stockmarketapp.presentation.company_listings

sealed class CompanyListingsEvent {
    data class OnSearchQueryChanged(val query: String) : CompanyListingsEvent()
    object OnPullToRefresh : CompanyListingsEvent()
}
