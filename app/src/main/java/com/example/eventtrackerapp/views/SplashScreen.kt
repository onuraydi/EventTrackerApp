package com.example.eventtrackerapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController)
{
    LaunchedEffect(Unit) {
        delay(1500)

        val auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            //oturum açıksa
            navController.navigate("profile"){
                popUpTo("splash_screen"){
                    inclusive = true
                }
            }
        }else{
            navController.navigate("login"){
                popUpTo("splash_screen"){
                    inclusive = true
                }
            }
        }
    }

    Scaffold(Modifier.fillMaxSize()) {innerPadding ->
        Box(Modifier.padding(innerPadding)){
            Column(Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painterResource(R.drawable.ic_launcher_background),null)
                Spacer(Modifier.padding(20.dp))
                Text("Event Tracker Application")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    EventTrackerAppTheme {
//        SplashScreen();
    }
}