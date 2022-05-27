package com.plcoding.stockmarketapp.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.IStockRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel @Inject constructor(
    val repository: IStockRepository
) : ViewModel() {
    var state by mutableStateOf(CompanyListingsState())
    private var searchJob: Job? = null

    fun onEvent(event: CompanyListingsEvent) {
        when (event) {
            is CompanyListingsEvent.OnPullToRefresh -> {
                getCompanyListings(isLoadFromRemote = true)
            }
            is CompanyListingsEvent.OnSearchClick -> {
                state = state.copy(
                    searchQuery = event.query
                )

                searchJob?.cancel() // avoid too much api request with a small delay

                searchJob = viewModelScope.launch {
                    delay(500)
                    getCompanyListings()
                }
            }
        }
    }

    private fun getCompanyListings(
        query: String = state.searchQuery.lowercase(),
        isLoadFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            repository.getCompanyListings(
                fetchFromRemote = isLoadFromRemote,
                query = query
            )
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { listings ->
                                state = state.copy(
                                    companies = listings
                                )
                            }
                        }
                        is Resource.Loading -> {
                            state = state.copy(
                                isLoading = true
                            )
                        }
                        is Resource.Error -> Unit // no error handling at the moment...
                    }
                }
        }
    }
}