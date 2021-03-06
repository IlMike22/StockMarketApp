package com.plcoding.stockmarketapp.presentation.company_listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.plcoding.stockmarketapp.presentation.company_listings.components.CompanyItem
import com.plcoding.stockmarketapp.presentation.destinations.CompanyInfoScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun CompanyListingsScreen(
    navigator: DestinationsNavigator,
    viewModel: CompanyListingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = state.isRefreshing // maybe has to be viewModel.state.is..
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { newQuery ->
                viewModel.onEvent(
                    CompanyListingsEvent.OnSearchQueryChanged(newQuery)
                )
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = "Search..")
            },
            maxLines = 1,
            singleLine = true
        )

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.onEvent(CompanyListingsEvent.OnPullToRefresh)
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.companies.size) { index ->
                    val company = state.companies[index]
                    CompanyItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.navigate(CompanyInfoScreenDestination(symbol = company.symbol))
                            }
                            .padding(16.dp),
                        company = state.companies[index]
                    )
                    if (index < state.companies.size) {
                        Divider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}