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
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.eventtrackerapp.data.source.local.UserPreferences
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.views.AppNavGraph
import com.example.eventtrackerapp.views.HomeScreen
import com.google.firebase.auth.FirebaseAuth


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
                ) { contentPadding ->
                    Box(Modifier.padding(contentPadding)){
                    }
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    val context = LocalContext.current
                    AppNavGraph(navController = navController, auth = FirebaseAuth.getInstance(), userPreferences = UserPreferences(context))
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
