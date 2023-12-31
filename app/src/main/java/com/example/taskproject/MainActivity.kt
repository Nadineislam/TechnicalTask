package com.example.taskproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.taskproject.core.utils.Resource
import com.example.taskproject.showing_items_feature.data.remote.dto.CombinedResponse
import com.example.taskproject.showing_items_feature.data.remote.dto.ItemsResponseItem
import com.example.taskproject.showing_items_feature.presentation.viewmodel.ItemsViewModel
import com.example.taskproject.ui.theme.TaskProjectTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@OptIn(ExperimentalCoilApi::class)
class MainActivity : ComponentActivity() {
    private val viewModel: ItemsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskProjectTheme {
                ItemsApp(viewModel = viewModel)
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun ItemsApp(viewModel: ItemsViewModel) {
    val items = viewModel.items.collectAsStateWithLifecycle()

    when (val resource = items.value) {
        is Resource.Success -> {
            val combinedResponses = resource.data
            ItemsList(items = combinedResponses ?: emptyList())
        }

        is Resource.Error -> {
            val message = resource.message ?: "Error fetching items"
            Text(text = message)
        }

        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ItemsList(items: List<CombinedResponse>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(15.dp),
    ) {
        items(items) { combinedResponse ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                ItemDetails(
                    item = combinedResponse.item,
                    formattedDate = combinedResponse.formattedDate
                )
            }
        }
    }
}

@Composable
fun ItemDetails(
    item: ItemsResponseItem?,
    formattedDate: String?,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(9.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .padding(2.dp)
    ) {
        Box(modifier = Modifier) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                val painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = item?.owner?.avatar_url)
                        .apply {
                        }
                        .build()
                )
                Image(
                    painter = painter,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "avatar"
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.1f),
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Black.copy(alpha = 0.9f),
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = item?.name ?: "",
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Owner: ${item?.owner?.login}",
                        modifier = Modifier.padding(top = 4.dp),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Text(
                        text = "Created: $formattedDate",
                        modifier = Modifier.padding(top = 4.dp),
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )

                }
            }
        }
    }
}
