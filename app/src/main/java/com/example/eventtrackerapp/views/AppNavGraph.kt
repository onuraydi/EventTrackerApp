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
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.TagViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    eventViewModel: EventViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    tagViewModel: TagViewModel = viewModel(),
){

    NavHost(navController=navController, startDestination = "create_profile_screen") {

        composable("home") {
            LaunchedEffect(Unit) {
                eventViewModel.getAllEvents()
            }
            val eventList by eventViewModel.eventList.collectAsState()
            HomeScreen(eventList = eventList, navController = navController) }


        composable("addEvent") {
            LaunchedEffect(Unit) {
                categoryViewModel.getAllCategoryWithTags()
            }
            AddEventScreen(
                navController = navController,
                tagViewModel,
                categoryViewModel
            ){
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

        composable("my_account") {
            MyAccountScreen(navController)
        }

        composable("preferences"){
            PreferencesScreen(navController)
        }

        composable("my_events"){
            MyEventsScreen(navController)
        }

        composable("create_profile_screen") {
            LaunchedEffect(Unit) {
                categoryViewModel.getAllCategoryWithTags()
            }
            CreateProfileScreen(tagViewModel,categoryViewModel)
        }
    }
}