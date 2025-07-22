package com.example.eventtrackerapp.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Profile
import java.io.File
import com.example.eventtrackerapp.common.BottomNavBar
import com.example.eventtrackerapp.common.EventTrackerExtendedFloatingActionButton
import com.example.eventtrackerapp.common.SelectableImageBox


@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel:AuthViewModel,
    profile:Profile
    )
{
    Scaffold(Modifier
        .fillMaxSize(),
        bottomBar = { BottomNavBar(navController = navController)}
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            ) {
            //Profil Fotoğrafı
            SelectableImageBox(
                boxWidth = 140.dp,
                boxHeight = 140.dp,
                imagePath = profile.photo,
                modifier = Modifier,
                placeHolder = painterResource(R.drawable.ic_launcher_foreground),
                shape = CircleShape,
                borderStroke = BorderStroke(1.dp,MaterialTheme.colorScheme.primaryContainer)
            )

                Spacer(Modifier.padding(10.dp))

                Text(profile.fullName, fontSize = 20.sp)
                Text(profile.email, fontSize = 14.sp, fontWeight = FontWeight.Light)

            // Hesabım

            // MY Account

            EventTrackerExtendedFloatingActionButton(
                text = "My Account",
                onClick =
                {
                    navController.navigate("my_account")
                    {
                        launchSingleTop = true
                    }
                },
                leadingIcon = Icons.Filled.Person,
                leadingIconDescription = "Person"
            )

            // My Preferences

            EventTrackerExtendedFloatingActionButton(
                text = "My Preferences",
                onClick =
                {
                    navController.navigate("preferences")
                    {
                        launchSingleTop = true
                    }
                },
                leadingIcon = Icons.Filled.Build,
                leadingIconDescription = "Person"
            )

            // My Events

            EventTrackerExtendedFloatingActionButton(
                text = "My Events",
                onClick =
                {
                    navController.navigate("my_events")
                    {
                        launchSingleTop = true
                    }
                },
                leadingIcon = Icons.Filled.Star,
                leadingIconDescription = "Person"
            )

            // Log Out

            EventTrackerExtendedFloatingActionButton(
                text = "Log Out",
                textColor = Color.Red,
                onClick =
                {
                    authViewModel.logOut()
                    navController.navigate("login_screen")
                    {
                        popUpTo("profile")
                        {
                            inclusive = true
                        }
                    }
                },
                leadingIcon = Icons.AutoMirrored.Filled.ExitToApp,
                leadingIconDescription = "Person",
                tint = Color.Red
            )
        }
    }
}