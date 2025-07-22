package com.example.eventtrackerapp.views

import android.net.Uri
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.roommodels.EventWithTags
import com.example.eventtrackerapp.utils.BottomNavBar
import com.example.eventtrackerapp.viewmodel.ExploreViewModel
import java.io.File

@SuppressLint("SuspiciousIndentation")
@Composable
fun ExploreScreen(
    eventList:List<EventWithTags>,
    navController:NavController,
    exploreViewModel: ExploreViewModel = viewModel()
){

    val searchResult by exploreViewModel.searchList.collectAsStateWithLifecycle()
    val historyList by exploreViewModel.historyList.collectAsStateWithLifecycle()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar =
            {
                BottomNavBar(navController = navController)
            }

        ) { innerPadding ->

            val query = rememberSaveable { mutableStateOf("") }
            val active = rememberSaveable { mutableStateOf(false) }
            val displayedEvents = if(query.value.trim().isEmpty()) eventList else searchResult

            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {

                SimpleSearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    query = query.value,
                    onQueryChange =
                    {
                        query.value = it
                        exploreViewModel.searchEvents(it)
                    },
                    active = active.value,
                    onActiveChange =
                    {
                        active.value = it
                    },
                    onSearch = {
                        if(query.value.trim().isNotEmpty()){
                            query.value = it.trim()
                            exploreViewModel.searchEvents(query.value) //arama yap
                            exploreViewModel.insertHistory(query.value) //geçmişe ekle
                            active.value = false
                        }
                        active.value = false
                    },
                    placeHolder = { Text("Search") },
                    leadingIcon =
                    {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon =
                    {
                        if (active.value) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                modifier = Modifier.clickable
                                {
                                    if (query.value.isNotEmpty())
                                    {
                                        query.value = ""
                                    } else
                                    {
                                        active.value = false
                                    }
                                },
                            )
                        }
                    },
                    searchResult = searchResult,
                    onHistoryClick =
                    {
                        query.value = it
                        exploreViewModel.searchEvents(it)
                        exploreViewModel.insertHistory(it)
                        active.value = false
                    },
                    searchHistoryList = historyList.map{it.keyword},
                    navController = navController,
                    deleteItem =
                    {
                        exploreViewModel.deleteHistoryItem(it)
                    }
                )

                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 8.dp, end = 8.dp),
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    content =
                    {
                        items(displayedEvents)
                        {
                            MyImage(it,navController)
                        }
                    }
                )
            }
        }
    }

@Composable
fun MyImage(event:Event,navController: NavController){
    SelectableImageBox(
        boxWidth = 160.dp,
        boxHeight = 200.dp,
        imagePath = event.image,
        modifier = Modifier,
        placeHolder = painterResource(R.drawable.ic_launcher_background),
        shape = RectangleShape,
        onClick = {
            navController.navigate("detail/${event.id}")
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    query:String, //içerisine yazılacak yazı
    onQueryChange: (String) -> Unit, //içerisine yazılacak yazı değişince tetiklenecek
    onSearch: (String) ->Unit, // search edince olacak olay
    active:Boolean, //search in aktif olup olmaması
    onActiveChange: (Boolean) -> Unit, //aktifliğin değişmesi
    modifier: Modifier = Modifier,
    placeHolder: @Composable (()->Unit)? = null,
    leadingIcon: @Composable (()->Unit)? = null,
    trailingIcon: @Composable (()->Unit)? = null,
    searchResult: List<EventWithTags>,
    navController: NavController,
    searchHistoryList: List<String>,
    onHistoryClick: (String)->Unit,
    deleteItem: (String)-> Unit
){
    //ekranın genişlemesini tutan state
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = active,// Bu değer search in aktif olup olmadığını gösterir
        onActiveChange = onActiveChange,
        placeholder = placeHolder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    ){
        LazyColumn(
            contentPadding = PaddingValues(4.dp)
        ) {
            if(query.isEmpty()){
                items(searchHistoryList){historyItem->
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
                                onHistoryClick(historyItem)
                            }
                    ){
                        Icon(
                            painterResource(R.drawable.history_icon),
                            "HistoryItem",
                            Modifier.weight(1f)
                            )
                        Text(historyItem, modifier = Modifier.weight(4f).padding(start = 4.dp))
                        Icon(
                            Icons.Default.Clear,
                            "Clear",
                            Modifier
                                .weight(1f)
                                .clickable {
                                    deleteItem(historyItem)
                                }
                        )
                    }
                }
            }
            else{
                items(searchResult){eventWithTag->
                    Row(
                        Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
                                onSearch(eventWithTag.event.name ?: "")
                                navController.navigate("detail/${eventWithTag.event.id}")
                            }
                    ){
                        Icon(painterResource(R.drawable.baseline_event_24),"HistoryItem")
                        Text(eventWithTag.event.name ?: "", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
    }
}