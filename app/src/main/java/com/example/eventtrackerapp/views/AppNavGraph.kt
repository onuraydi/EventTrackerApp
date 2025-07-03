package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.data.source.local.UserPreferences
import com.example.eventtrackerapp.model.Profile
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.CommentViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.ExploreViewModel
import com.example.eventtrackerapp.viewmodel.LikeViewModel
import com.example.eventtrackerapp.viewmodel.ParticipantsViewModel
import com.example.eventtrackerapp.viewmodel.ProfileViewModel
import com.example.eventtrackerapp.viewmodel.TagViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@SuppressLint("NewApi")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    eventViewModel: EventViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    tagViewModel: TagViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    commentViewModel: CommentViewModel = viewModel(),
    likeViewModel: LikeViewModel = viewModel(),
    participantsViewModel: ParticipantsViewModel = viewModel(),
    exploreViewModel: ExploreViewModel = viewModel(),
    auth: FirebaseAuth,
    userPreferences: UserPreferences,
){

    var startDestination by remember { mutableStateOf("splash_screen") }

    LaunchedEffect(true) {
        val hasSeenOnboarding = userPreferences.getHasSeenOnborading()
        val isProfileCompleted = userPreferences.getIsProfileCompleted()

        val isLoggedIn = auth.currentUser != null

        startDestination = when{
            !hasSeenOnboarding -> "onboarding_screen"
            !isLoggedIn -> "login_screen"
            !isProfileCompleted -> "create_profile_screen"
            else -> "home"
        }
    }
    if (startDestination != "splash_screen")
    {
        NavHost(navController=navController, startDestination = startDestination) {
            composable("onboarding_screen") {
                OnBoardingSceen(navController,userPreferences)
            }

            composable("create_profile_screen") {
                LaunchedEffect(Unit) {
                    categoryViewModel.getAllCategoryWithTags()
                }
                val categoryWithTags by categoryViewModel
                    .categoryWithTags.collectAsStateWithLifecycle(initialValue = emptyList())
                val uid = auth.currentUser?.uid
                val email = auth.currentUser?.email
                CreateProfileScreen(navController,tagViewModel,profileViewModel,userPreferences,categoryWithTags,uid,email)
            }

            composable("home") {backStackEntry ->
                val uid = auth.currentUser?.uid!!
                LaunchedEffect(Unit) {
                    profileViewModel.getById(uid)
                }

                val profile by profileViewModel.profile.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    val tagIds = profile.selectedTagList?.map { it.id } ?: emptyList()
                    eventViewModel.getEventBySelectedTag(tagIds)

                }
                println("selectedTagList"+profile.selectedTagList)
                val eventList by eventViewModel.eventWithTag.collectAsState()

                HomeScreen(eventList = eventList, navController = navController,commentViewModel,likeViewModel,uid) }


            composable("addEvent") {
                LaunchedEffect(Unit) {
                    categoryViewModel.getAllCategoryWithTags()
                }
                AddEventScreen(
                    navController = navController,
                    tagViewModel,
                    categoryViewModel,
                    eventViewModel
                )
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
                var uid = auth.currentUser?.uid!!

                var commentList = commentViewModel.getComments(eventId = event.id)
                DetailScreen(event = event, navController = navController, category = category, commentList,commentViewModel,likeViewModel,uid,participantsViewModel)
            }


            composable("explorer"){
                LaunchedEffect(Unit) {
                    eventViewModel.getAllEvents()
                }
                val eventList by eventViewModel.eventList.collectAsStateWithLifecycle()
                ExploreScreen(eventList,navController,exploreViewModel)
            }

            composable("profile") {
                var uid = auth.currentUser?.uid
                LaunchedEffect(Unit) {
                    profileViewModel.getById(uid!!)
                }

                val profile by profileViewModel.profile.collectAsStateWithLifecycle()


                ProfileScreen(navController = navController,authViewModel,profile)
            }

            composable("my_account") {
                var uid = auth.currentUser?.uid
                LaunchedEffect(Unit) {
                    profileViewModel.getById(uid!!)
                }
                val profile by profileViewModel.profile.collectAsStateWithLifecycle()
                MyAccountScreen(navController,profile,profileViewModel)
            }

            composable("preferences"){
                val uid = auth.currentUser?.uid
                LaunchedEffect(Unit){
                    profileViewModel.getById(uid ?: "")
                }

                val profile by profileViewModel.profile.collectAsStateWithLifecycle()

                PreferencesScreen(navController,profile,profileViewModel)
            }

            composable("my_events"){
                MyEventsScreen(navController)
            }

            composable("notification") {
                NotificationScreen(navController)
            }

            composable("sign_up") {
                SignUpScreen(navController,authViewModel,userPreferences)
            }

            composable("login_screen") {
                LoginScreen(navController, authViewModel, userPreferences)
            }

            composable("splash_screen"){
                SplashScreen(navController)
            }

            composable("participants_screen/{id}") {backStackEntry ->

                val eventId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                // TODO Event Id değeri gelmiyor o yüzden crash yiyoruz
                println("eventId:" + eventId)
                ParticipantsScreen(navController,participantsViewModel, eventId)
            }

            composable("edit_event_screen") {
                EditEventScreen(navController)
            }


        }
    }
}