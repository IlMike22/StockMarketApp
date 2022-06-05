package com.plcoding.stockmarketapp.presentation.company_info

import com.plcoding.stockmarketapp.domain.model.CompanyInfo

sealed class CompanyInfoEvent {
    class LoadCompanyInfo(val info: CompanyInfo):CompanyInfoEvent()
}


