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
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.views.AddEventScreen
import com.example.eventtrackerapp.views.HomeScreen


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)

    val viewModel: EventViewModel by viewModels<EventViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventTrackerAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
//                    bottomBar = {
//                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets, modifier = Modifier) {
//                            NavigationBarItem(
//                                selected = true,
//                                icon ={ Icon(Icons.Filled.Home,"Home") },
//                                label = {Text("Home")},
//                                onClick = {},
//                            )
//
//                            NavigationBarItem(
//                                selected = false,
//                                icon ={ Icon(Icons.Filled.Search,"Explore") },
//                                label = {Text("Explore")},
//                                onClick = {},
//                            )
//
//                            NavigationBarItem(
//                                selected = false,
//                                icon ={ Icon(Icons.Filled.Person,"Profile") },
//                                label = {Text("Profile")},
//                                onClick = {},
//                            )
//                        }
//                    }
                ) { contentPadding -> Box(Modifier.padding(contentPadding))
                    LaunchedEffect(Unit) {
                        viewModel.getAllEvents()
                    }
                    val eventList by viewModel.eventList.collectAsState()
                    //AddEventScreen()
                    //SplashScreen()
                    //DetailScreen()
                    HomeScreen(eventList)
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
