package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.data.source.local.UserPreferences
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.CommentViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.ExploreViewModel
import com.example.eventtrackerapp.viewmodel.LikeViewModel
import com.example.eventtrackerapp.viewmodel.ParticipantsViewModel
import com.example.eventtrackerapp.viewmodel.PermissionViewModel
import com.example.eventtrackerapp.viewmodel.ProfileViewModel
import com.example.eventtrackerapp.viewmodel.StorageViewModel
import com.example.eventtrackerapp.viewmodel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("NewApi")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    eventViewModel: EventViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel(),
    likeViewModel: LikeViewModel = hiltViewModel(),
    participantsViewModel: ParticipantsViewModel = hiltViewModel(),
    exploreViewModel: ExploreViewModel = hiltViewModel(),
    permissionViewModel:PermissionViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    storageViewModel: StorageViewModel = hiltViewModel(),
    auth: FirebaseAuth,
    userPreferences: UserPreferences,
){

    var startDestination by remember { mutableStateOf("splash_screen") }

    LaunchedEffect(true) {
        val hasSeenOnboarding = userPreferences.getHasSeenOnborading()
//        val isProfileCompleted = userPreferences.getIsProfileCompleted()

        val isLoggedIn = auth.currentUser != null

        startDestination = when{
            !hasSeenOnboarding -> "onboarding_screen"
            !isLoggedIn -> "login_screen"
//            !isProfileCompleted -> "create_profile_screen"
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
                val categoryWithTags by categoryViewModel.getAllCategoryWithTags().collectAsState(emptyList())
                val uid = auth.currentUser?.uid
                val email = auth.currentUser?.email
                CreateProfileScreen(navController,categoryViewModel,profileViewModel,permissionViewModel,storageViewModel,userPreferences,categoryWithTags,uid!!,email!!)
            }

            composable("home") {backStackEntry ->
                val uid = auth.currentUser?.uid

                if(uid==null){
                    HomeScreen(
                        eventList = emptyList(),
                        navController = navController,
                        isLoading = true,
                        commentViewModel = commentViewModel,
                        likeViewModel = likeViewModel,
                        profileId = "",
                    )
                    return@composable
                }

                val profile by profileViewModel.getById(uid).collectAsState(null)

                if(profile==null){
                    HomeScreen(
                        eventList = emptyList(),
                        navController = navController,
                        isLoading = true,
                        commentViewModel = commentViewModel,
                        likeViewModel = likeViewModel,
                        profileId = uid,
                    )
                    return@composable
                }

                // TODO Sakın Silme !!!
                val categoryWithTags by categoryViewModel.getAllCategoryWithTags().collectAsState(emptyList())
                val selectedTag by categoryViewModel.selectedTag.collectAsStateWithLifecycle()
                val chosenTags by categoryViewModel.chosenTags.collectAsStateWithLifecycle()

                val eventList by eventViewModel.getEventsForUser(profile!!.selectedTagList.map { it.id }).collectAsState(
                    emptyList()
                )
                val isLoading = eventList.isEmpty() || profile == null

                HomeScreen(
                    eventList = eventList,
                    navController = navController,
                    isLoading = isLoading,
                    commentViewModel = commentViewModel,
                    likeViewModel = likeViewModel,
                    profileId = uid,
                )
            }



            composable("addEvent") {
                val uid = auth.currentUser?.uid

                AddEventScreen(
                    navController = navController,
                    categoryViewModel,
                    eventViewModel,
                    permissionViewModel,
                    storageViewModel,
                    uid ?: ""
                )
            }

            composable("detail/{id}") { backStackEntry ->

                val eventId = backStackEntry.arguments?.getString("id") ?: ""
                val uid = auth.currentUser?.uid ?: return@composable // güvenli kontrol

                val eventWithTags by eventViewModel.getEventWithRelationsById(eventId).collectAsState(null)
                val categoryWithTags by categoryViewModel.getAllCategoryWithTags().collectAsState(emptyList())
                val commentList by commentViewModel.getComments(eventId).collectAsState(emptyList())

                val isLoading = eventWithTags == null || categoryWithTags.isEmpty()

                val detailCategory = eventWithTags?.event?.categoryId?.let { catId ->
                    categoryWithTags.firstOrNull { it.category.id == catId }?.category
                }

                DetailScreen(
                    event = eventWithTags?.event ?: Event(), // Boş event loading için
                    navController = navController,
                    isLoading = isLoading || detailCategory == null || eventWithTags?.event?.id.isNullOrBlank(),
                    category = detailCategory ?: Category(),
                    commentList = commentList,
                    commentViewModel = commentViewModel,
                    likeViewModel = likeViewModel,
                    profileId = uid,
                    participantsViewModel = participantsViewModel,
                )
            }


            composable("explorer"){

                LaunchedEffect(Unit) {
                    eventViewModel.getAllEventsWithRelations()
                }
                val eventList by eventViewModel.allEventsWithRelations.collectAsStateWithLifecycle()

                ExploreScreen(eventList,navController,exploreViewModel)

            }

            composable("profile") {
                val uid = auth.currentUser?.uid

                if(uid!=null){

                    val profile by profileViewModel.getById(uid).collectAsState(null)

                    ProfileScreen(
                        navController,
                        authViewModel,
                        profile = profile ?: Profile(),
                        isLoading = profile == null
                    )
                }
            }

            composable("my_account") {
                val uid = auth.currentUser?.uid

                if(uid!=null){

                    val profile by profileViewModel.getById(uid).collectAsState(null)

                    profile?.let {
                        MyAccountScreen(navController,profile!!,profileViewModel,permissionViewModel,storageViewModel)
                    }
                }
            }

            composable("preferences"){
                val uid = auth.currentUser?.uid

                if(uid!=null){

                    val profile by profileViewModel.getById(uid).collectAsState(null)

                    val isDark = themeViewModel.isDarkTheme.collectAsState().value

                    profile?.let{profile->
                        PreferencesScreen(navController,profile,profileViewModel,isDark,themeViewModel)
                    }

                }
            }

            composable("my_events"){
                val uid = auth.currentUser?.uid!!

                LaunchedEffect(Unit) {
                    eventViewModel.getEventsByOwnerId(uid)
                }
                val myEvents by eventViewModel.eventsForOwner.collectAsStateWithLifecycle()

                MyEventsScreen(navController,myEvents){
                    eventViewModel.deleteEvent(it)
                }
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

                val eventId = backStackEntry.arguments?.getString("id")?.toString() ?: ""
                // TODO Event Id değeri gelmiyor o yüzden crash yiyoruz
                println("eventId:" + eventId)
                ParticipantsScreen(navController,participantsViewModel, eventId)
            }

            composable("edit_event_screen/{id}") {backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("id").toString() ?: ""
//                LaunchedEffect(eventId) {
//                    eventViewModel.getEventWithRelationsById(eventId)
//                }
//                val eventWithTags by eventViewModel.getEventWithRelationsById(eventId).collectAsState(null)

                val uid = auth.currentUser?.uid
                EditEventScreen(navController,eventId,eventViewModel,categoryViewModel,uid!!)
            }
        }
    }
}