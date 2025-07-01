package com.example.eventtrackerapp.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.data.source.local.UserPreferences
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.TagViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph(
    navController: NavHostController,
    eventViewModel: EventViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    tagViewModel: TagViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    auth: FirebaseAuth,
    userPreferences: UserPreferences
){

    var startDestination by remember { mutableStateOf("splash_screen") }

    LaunchedEffect(true) {
        val hasSeenOnboarding = userPreferences.getHasSeenOnborading()
        val isProfileCompleted = userPreferences.getIsProfileCompleted()

        val isLoggedIn = auth.currentUser != null

        startDestination = when{
//            !hasSeenOnboarding -> "onboarding"
            !isLoggedIn -> "login_screen"
            !isProfileCompleted -> "create_profile_screen"
            else -> "home"
        }
    }
    if (startDestination != "splash_screen")
    {
        NavHost(navController=navController, startDestination = startDestination) {
//            Composable("onboarding"){
//                // Onboarding
//            }


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

                val eventId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0

                // Event'i çağır
                LaunchedEffect(eventId) {
                    eventViewModel.getEventById(eventId)
                }

                val event by eventViewModel.event.collectAsState()

                val category by categoryViewModel.category.collectAsState()

                LaunchedEffect(event?.categoryId) {
                    val categoryID = event?.categoryId ?: 0
                    if (categoryID != 0) {
                        categoryViewModel.getCategoryById(categoryID)
                    }
                }

                DetailScreen(event = event, navController = navController, category = category)
            }


            composable("explorer"){
                val eventList by eventViewModel.eventList.collectAsStateWithLifecycle()
                ExploreScreen(eventList,navController)
            }

            composable("profile") {
                ProfileScreen(navController = navController,authViewModel)
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

            composable("notification") {
                NotificationScreen(navController)
            }
            composable("create_profile_screen") {
                LaunchedEffect(Unit) {
                    categoryViewModel.getAllCategoryWithTags()
                }
                CreateProfileScreen(tagViewModel,categoryViewModel,userPreferences)
            }

            composable("sign_up") {
                SignUpScreen(navController,authViewModel,userPreferences)
            }

            composable("login_screen") {
                LoginScreen(navController, authViewModel)
            }

            composable("splash_screen"){
                SplashScreen(navController)
            }

            composable("participants_screen") {
                ParticipantsScreen(navController)
            }
        }
    }
}