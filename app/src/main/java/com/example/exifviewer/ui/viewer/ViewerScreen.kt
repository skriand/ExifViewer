package com.example.exifviewer.ui.viewer

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.exifviewer.R
import com.example.exifviewer.ui.ViewModelProvider
import com.example.exifviewer.ui.navigation.NavigationDestination

object ViewerDestination : NavigationDestination {
    override val route = "viewer"
    override val titleResourceId: Int = R.string.app_name
    val uriArg = "uri"
    val fullRoute = "$route/{$uriArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerScreen(
    navigateBack: () -> Boolean,
    navigateTo: (String) -> Unit,
    uriArg: String,
    modifier: Modifier = Modifier,
    viewModel: ViewerViewModel = viewModel(factory = ViewModelProvider.Factory),
) {
    viewModel.onImageChosen(
        Uri.parse(uriArg)
    )
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val lazyListState = rememberLazyListState()
    val firstItemTranslationY by remember {
        derivedStateOf {
            when {
                lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty()
                        && lazyListState.firstVisibleItemIndex == 0 ->
                    lazyListState.firstVisibleItemScrollOffset * .6f

                else -> 0f
            }
        }
    }
    BackHandler(enabled = true, onBack = { navigateBack() })
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.image),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(id = R.string.close)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigateTo(Uri.encode(viewModel.uiState.value.imgSource!!.toString())) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(id = R.string.edit_exif),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(innerPadding),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    Box {
                        AsyncImage(
                            model = viewModel.uiState.value.imgSource,
                            contentDescription = stringResource(R.string.image),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    MaterialTheme.shapes.extraLarge
                                )
                                .clip(MaterialTheme.shapes.extraLarge)
                                .graphicsLayer {
                                    translationY = firstItemTranslationY
                                }
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.LightGray,
                                    MaterialTheme.shapes.extraLarge
                                )
                                .height(5.dp)
                                .width(50.dp)
                        )
                    }
                }
                items(items = viewModel.uiState.value.exifTags.toList()) {
                    Row {
                        Text(
                            text = it.first,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.0.dp))
                        Text(text = it.second)
                    }
                }
            }
        }
    }
}