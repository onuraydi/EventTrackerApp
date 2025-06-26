package com.example.eventtrackerapp.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.BottomNavBar

@Composable
fun ExploreScreen(
    eventList:List<Event>,
    navController:NavController
){
    EventTrackerAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {BottomNavBar(navController = navController)}
        ) { innerPadding ->
            val query = rememberSaveable { mutableStateOf("") }
            val active = rememberSaveable { mutableStateOf(false) }
            val searchList = remember { mutableStateListOf<String?>() }

            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                SimpleSearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    query = query.value,
                    onQueryChange = {
                        query.value = it
                    },
                    active = active.value,
                    onActiveChange = {
                        active.value = it
                    },
                    onSearch = {
                        searchList.add(query.value.trim())
                        active.value = false
                    },
                    placeHolder = { Text("Search") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, "Search")
                    },
                    trailingIcon = {
                        if (active.value) {
                            if (searchList.isNotEmpty()) {
                                Icon(
                                    Icons.Default.Clear, "Clear",
                                    modifier = Modifier.clickable {
                                        if (query.value.isNotEmpty()) {
                                            query.value = ""
                                        } else {
                                            active.value = false
                                        }
                                    },
                                )
                            }
                        }
                    },
                    searchResult = searchList.filterNotNull()
                )
                LazyVerticalStaggeredGrid(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 8.dp, end = 8.dp),
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    content = {
                        items(eventList) {
                            MyImage(it,navController)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MyImage(event:Event,navController: NavController){
    val randomHeights = remember { (150..300).random().dp }
    Image(
        modifier = Modifier
            .fillMaxWidth()
            .height(randomHeights)
            .border(
                border = BorderStroke(
                    1.dp,
                    Color.Black
                )
            )
            .clickable {
                navController.navigate("detail")
            },
        painter = painterResource(event.image),
        contentDescription = "",
        contentScale = ContentScale.Crop,
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
    searchResult: List<String>
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
        searchResult.forEach {
            Row {
                Icon(
                    painterResource(R.drawable.history_icon),"History"
                )
                Text(it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen(){
    EventTrackerAppTheme {
        //ExploreScreen()
    }
}
