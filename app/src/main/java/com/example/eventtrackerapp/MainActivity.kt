package com.example.eventtrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.views.AppNavGraph
import com.example.eventtrackerapp.views.HomeScreen


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)

    val viewModel: EventViewModel by viewModels<EventViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            EventTrackerAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
//                    bottomBar = {
//                        val isSelectedHome = rememberSaveable { mutableStateOf(true)}
//                        val isSelectedExplore = rememberSaveable { mutableStateOf(false)}
//                        val isSelectedProfile = rememberSaveable { mutableStateOf(false)}
//                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets, modifier = Modifier) {
//                            NavigationBarItem(
//                                selected = isSelectedHome.value,
//                                icon ={ Icon(Icons.Filled.Home,"Home") },
//                                label = {Text("Home")},
//                                onClick = {
//                                    isSelectedHome.value = true
//                                    isSelectedExplore.value = false
//                                    isSelectedProfile.value = false
//                                    navController.navigate("home"){
//                                        launchSingleTop = true
//                                    }
//                                },
//                            )
//
//                            NavigationBarItem(
//                                selected = isSelectedExplore.value,
//                                icon ={ Icon(Icons.Filled.Search,"Explore") },
//                                label = {Text("Explore")},
//                                onClick = {
//                                    isSelectedHome.value = false
//                                    isSelectedExplore.value = true
//                                    isSelectedProfile.value = false
//                                    navController.navigate("explorer"){
//                                        launchSingleTop = true
//                                    }
//                                },
//                            )
//
//                            NavigationBarItem(
//                                selected = isSelectedProfile.value,
//                                icon ={ Icon(Icons.Filled.Person,"Profile") },
//                                label = {Text("Profile")},
//                                onClick = {
//                                    isSelectedHome.value = false
//                                    isSelectedExplore.value = false
//                                    isSelectedProfile.value = true
//                                    navController.navigate("profile"){
//                                        launchSingleTop = true
//                                    }
//                                },
//                            )
//                        }
//                    }
                ) { contentPadding ->
                    Box(Modifier.padding(contentPadding))
//                    val eventList by viewModel.eventList.collectAsState()
                    //AddEventScreen()
                    //SplashScreen()
                    //DetailScreen()
                    AppNavGraph(navController = navController)


                    //HomeScreen(eventList,navController)
                    //MyEvents()
                    //CreateProfileScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
