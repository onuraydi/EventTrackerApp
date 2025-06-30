package com.example.eventtrackerapp.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedButton
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
){
    val loginRequest by authViewModel.loginRequest

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val userEmail = remember { mutableStateOf(loginRequest.email) }
        val userPassword = remember { mutableStateOf(loginRequest.password) }
        val isObscure = remember { mutableStateOf(false) }

        Text(text = "LOG IN", fontSize = 25.sp, fontWeight = FontWeight.W500)
        Spacer(Modifier.padding(vertical = 20.dp))

        EventTrackerAppOutlinedTextField(
            "Email",
            state = userEmail,
            leadingIcon = { Icon(Icons.Filled.MailOutline, "UserEmail") },
        )

        Spacer(Modifier.padding(vertical = 15.dp))

        EventTrackerAppOutlinedTextField(
            "Password",
            userPassword,
            isPassword = !isObscure.value,
            leadingIcon = { Icon(Icons.Filled.Lock, "UserPassword") },
            trailingIcon = {
                val icon = if(isObscure.value){
                    painterResource(R.drawable.visibility_icon)
                }else{
                    painterResource(R.drawable.visibility_off_icon)
                }
                IconButton(onClick = {isObscure.value = !isObscure.value}){
                    Icon(painter = icon, "visibility")
                }
            }
        )

        Spacer(Modifier.padding(18.dp))

        EventTrackerAppPrimaryButton("Login") {

            authViewModel.login(userEmail.value,userPassword.value){
                boolean,string->
                if(boolean){
                    navController.navigate("profile"){
                        launchSingleTop = true
                    }
                }else{
                    Toast.makeText(context,string,Toast.LENGTH_LONG).show()
                }
            }
        }

        Spacer(Modifier.padding(8.dp))

        Text("or", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(0.7f))

        Spacer(Modifier.padding(vertical = 8.dp))

        EventTrackerAppOutlinedButton("Sign Up") {
            println("Sign up edildi")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen(){
    EventTrackerAppTheme {
//        LoginScreen()
    }
}