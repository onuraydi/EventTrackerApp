package com.example.eventtrackerapp.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.eventtrackerapp.viewmodel.EventViewModel

@Composable
fun AppNavGraph(navController: NavHostController,eventViewModel: EventViewModel = viewModel()){

    NavHost(navController=navController, startDestination = "home") {

        composable("home") {
            LaunchedEffect(Unit) {
                eventViewModel.getAllEvents()
            }
            val eventList by eventViewModel.eventList.collectAsState()
            HomeScreen(eventList = eventList, navController = navController) }


        composable("addEvent") {
            AddEventScreen(navController = navController){
                eventViewModel.addEvent(it)
                navController.popBackStack()
            }
        }

        composable("detail/{id}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("id")
            LaunchedEffect(Unit) {
                eventViewModel.getEventById(eventId?.toIntOrNull() ?: 0)
            }
            val event by eventViewModel.event.collectAsState()
            DetailScreen(event,navController) }

        composable("explorer"){
            val eventList by eventViewModel.eventList.collectAsStateWithLifecycle()
            ExploreScreen(eventList,navController)
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}