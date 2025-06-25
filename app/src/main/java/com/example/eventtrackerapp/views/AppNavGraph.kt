package com.example.eventtrackerapp.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.viewmodel.EventViewModel

@Composable
fun AppNavGraph(navController: NavHostController,eventViewModel: EventViewModel = viewModel()){

    NavHost(navController=navController, startDestination = "home") {
        composable("home") {
            val eventList by eventViewModel.eventList.collectAsState()
            HomeScreen(eventList = eventList, navController = navController) }
        composable("detail") { DetailScreen(navController) }
    }
}