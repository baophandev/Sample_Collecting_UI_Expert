package com.application.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.application.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> PagingLayout(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    pagingItems: LazyPagingItems<T>,
    itemKey: ((index: Int) -> Any)? = null,
    itemsContent: @Composable (LazyItemScope.(item: T) -> Unit),
    noItemContent: (@Composable (LazyItemScope.() -> Unit))? = null
) {
    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = refreshState,
        modifier = modifier.fillMaxSize(),
        contentAlignment = contentAlignment,
        isRefreshing = pagingItems.loadState.refresh is LoadState.Loading,
        onRefresh = pagingItems::refresh
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = when (pagingItems.loadState.refresh) {
                is LoadState.NotLoading -> Arrangement.Top
                else -> Arrangement.Center
            }
        ) {
            if (pagingItems.loadState.hasError)
                item {
                    TitleText(
                        text = stringResource(id = R.string.unknown_error),
                        textSize = 30.sp,
                        color = Color.Red
                    )
                }
            else if (pagingItems.itemCount == 0)
                noItemContent?.let {
                    item { noItemContent() }
                }
            else items(
                count = pagingItems.itemCount,
                key = itemKey
            ) { index -> pagingItems[index]?.let { itemsContent(it) } }
        }
    }
}