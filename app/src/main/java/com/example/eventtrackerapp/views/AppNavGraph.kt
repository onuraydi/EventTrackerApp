package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.data.source.local.UserPreferences
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.CommentViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.ExploreViewModel
import com.example.eventtrackerapp.viewmodel.LikeViewModel
import com.example.eventtrackerapp.viewmodel.ParticipantsViewModel
import com.example.eventtrackerapp.viewmodel.PermissionViewModel
import com.example.eventtrackerapp.viewmodel.ProfileViewModel
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
                val categoryWithTags by categoryViewModel.categoryWithTags.observeAsState(emptyList())
                val uid = auth.currentUser?.uid
                val email = auth.currentUser?.email
                CreateProfileScreen(navController,categoryViewModel,profileViewModel,permissionViewModel,userPreferences,categoryWithTags,uid!!,email!!)
            }

            composable("home") {backStackEntry ->
                val uid = auth.currentUser?.uid!!

                //Yapay Zeka tarafından live data için önerilen kullanım şekli
                val profileLiveData = remember(uid) {
                    profileViewModel.getById(uid)
                }
                val profile by profileLiveData.observeAsState()

                if(profile!=null){

                    val eventForUser = remember(profile!!.selectedTagList) {
                        val tagIds = profile!!.selectedTagList.map { it.id } ?: emptyList()
                        eventViewModel.getEventsForUser(tagIds)
                    }
                    println("selectedTagList"+profile!!.selectedTagList)
                    val eventList by eventForUser.observeAsState()

                    if(eventList!=null){
                        HomeScreen(eventList = eventList!!, navController = navController,commentViewModel,likeViewModel,uid)
                    }
                }
            }



            composable("addEvent") {
                val uid = auth.currentUser?.uid

                AddEventScreen(
                    navController = navController,
                    categoryViewModel,
                    eventViewModel,
                    permissionViewModel,
                    uid ?: ""
                )
            }

            composable("detail/{id}") { backStackEntry ->

                val eventId = backStackEntry.arguments?.getString("id") ?: ""

                // Event'i çağır
                val eventLiveData = remember(eventId) {
                    eventViewModel.getEventWithRelationsById(eventId)
                }
                val eventWithTags by eventLiveData.observeAsState()

                //Category'i al
                val categoryLiveData = remember {
                    categoryViewModel.categoryWithTags
                }
                val category by categoryLiveData.observeAsState(emptyList())

                val uid = auth.currentUser?.uid!!

                // TODO bu metot belki değişebilir

                val detailCategory = category.filter { cat ->
                    cat.category.id == eventWithTags?.event?.categoryId
                }.map { it.category }.first()

                if(eventWithTags!=null){
                    val commentList = commentViewModel.getComments(eventId = eventWithTags!!.event.id)

                    DetailScreen(event = eventWithTags!!.event,
                        navController = navController,
                        category = detailCategory,
                        commentList = commentList.value!!,
                        commentViewModel = commentViewModel,
                        likeViewModel = likeViewModel,
                        profileId = uid,
                        participantsViewModel= participantsViewModel,
                    )
                }
            }


            composable("explorer"){

                val eventListLiveData = remember {
                    eventViewModel.allEventsWithRelations
                }

                val eventList by eventListLiveData.observeAsState()

                if(eventList!=null){
                    ExploreScreen(eventList!!,navController,exploreViewModel)
                }

            }

            composable("profile") {
                val uid = auth.currentUser?.uid

                if(uid!=null){

                    val profileLiveData = remember(uid) {
                        profileViewModel.getById(uid)
                    }
                    val profile by profileLiveData.observeAsState()

                    profile?.let {
                        ProfileScreen(navController = navController,authViewModel, it)
                    }
                }
            }

            composable("my_account") {
                val uid = auth.currentUser?.uid

                if(uid!=null){

                    val profileLiveData = remember(uid) {
                        profileViewModel.getById(uid)
                    }
                    val profile by profileLiveData.observeAsState()

                    profile?.let {profile->
                        MyAccountScreen(navController,profile,profileViewModel)
                    }
                }
            }

            composable("preferences"){
                val uid = auth.currentUser?.uid

                if(uid!=null){

                    val profileLiveData = remember(uid) {
                        profileViewModel.getById(uid)
                    }
                    val profile by profileLiveData.observeAsState()

                    val isDark = themeViewModel.isDarkTheme.collectAsState().value

                    profile?.let{profile->
                        PreferencesScreen(navController,profile,profileViewModel,isDark,themeViewModel)
                    }
                }
            }

            composable("my_events"){
                val uid = auth.currentUser?.uid!!
                val myEventsLiveData = remember(uid) {
                    eventViewModel.getEventsByOwnerId(uid)
                }
                val myEvents by myEventsLiveData.observeAsState()

                if(myEvents!=null){
                    MyEventsScreen(navController,myEvents!!){
                        eventViewModel.deleteEvent(it)
                    }
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
//                val eventWithTags by eventViewModel.allEventsWithRelations.observeAsState(emptyList())

                val uid = auth.currentUser?.uid
                EditEventScreen(navController,eventId,eventViewModel,categoryViewModel,uid!!)
            }
        }
    }
}