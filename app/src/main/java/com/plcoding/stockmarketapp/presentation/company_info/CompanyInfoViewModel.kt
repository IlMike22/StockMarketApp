package com.plcoding.stockmarketapp.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.data.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StockRepository
) : ViewModel() {
    var state by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(
                isLoading = true
            )


            val companyInfoResult = async { repository.getCompanyInfo(symbol) }
            val intraDayInfoResult = async { repository.getIntradayInfo(symbol) }

            when (val result = companyInfoResult.await()) {
                is Resource.Success -> {
                    state = state.copy(
                        companyInfo = result.data,
                        isLoading = false,
                        error = null
                    )
                }

                is Resource.Error -> {
                    state = state.copy(
                        companyInfo = null,
                        error = result.message,
                        isLoading = false
                    )
                }
                else -> Unit
            }

            when (val result = intraDayInfoResult.await()) {
                is Resource.Success -> {
                    state = state.copy(
                        stockInfos = result.data?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }

                is Resource.Error -> {
                    state = state.copy(
                        stockInfos = emptyList(),
                        error = result.message,
                        isLoading = false
                    )
                }
                else -> Unit
            }


            if (companyInfoResult is Resource.Success) {
                val companyInfo = companyInfoResult.data
            }
        }
    }
}