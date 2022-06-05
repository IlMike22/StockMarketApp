package com.plcoding.stockmarketapp.presentation.company_info

import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val companyInfo:CompanyInfo? = null,
    val stockInfos: List<IntradayInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)