package com.example.eventtrackerapp.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.Authentication.SignUpRequest
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedButton
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel
)
{

    var context = LocalContext.current
    var signUpRequest = authViewModel.signUpRequest


    Box(modifier = Modifier
        .padding(15.dp)
        .fillMaxSize())
    {

        IconButton(onClick = {}, modifier = Modifier
            .border(border = BorderStroke(1.3f.dp, color = MaterialTheme.colorScheme.primary),shape = RoundedCornerShape(12.dp)),) {
            Icon(Icons.Filled.KeyboardArrowLeft,"back", tint = MaterialTheme.colorScheme.primary)
        }
//        OutlinedButton(modifier = Modifier
//            .padding(top = 15.dp, start = 10.dp)
//            .size(width = 50.dp, height = 40.dp),
//            shape = RoundedCornerShape(20.dp),
//            onClick ={}) { }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            )
        {
            var fullName = remember { mutableStateOf("") }
            var email = remember { mutableStateOf(signUpRequest.email) }
            var password = remember { mutableStateOf(signUpRequest.password) }
            var passwordConfirm = remember { mutableStateOf(signUpRequest.repeatPassword) }
            var isObscure = remember { mutableStateOf(false) }
            var isObscureConfirm = remember { mutableStateOf(false) }

            Text(text = "Sign Up",
                fontWeight = FontWeight.W500,
                fontSize = 25.sp,
                modifier = Modifier.padding(top = 20.dp)
                )
            Spacer(modifier = Modifier.padding(15.dp))
            EventTrackerAppOutlinedTextField("Kullanıcı Adı", state = fullName,
                leadingIcon = {
                    Icon(Icons.Filled.Person,"full name")
                })

            Spacer(modifier = Modifier.padding(15.dp))

            EventTrackerAppOutlinedTextField("Email", state = email,
                leadingIcon = {
                    Icon(Icons.Filled.Email,"email")
                });

            Spacer(modifier = Modifier.padding(15.dp))

            EventTrackerAppOutlinedTextField("Şifre", state = password,
                leadingIcon = {
                    Icon(Icons.Filled.Lock,"password")
                },
                trailingIcon = {
                    val icon = if(isObscure.value){
                        painterResource(R.drawable.visibility_icon)}
                    else{
                        painterResource(R.drawable.visibility_off_icon)}

                    Icon(painter = icon,"visibility",
                        modifier = Modifier.clickable { isObscure.value = !isObscure.value })
                },
                isPassword = !isObscure.value);


            Spacer(modifier = Modifier.padding(15.dp))

            EventTrackerAppOutlinedTextField("Şifre tekrarı", state = passwordConfirm,
                leadingIcon = {
                    Icon(Icons.Filled.Lock,"confirm password")
            },
                trailingIcon = {
                        Icon(painter = painterResource(isObscureConfirm.value),"visibility",
                            modifier = Modifier.clickable { isObscureConfirm.value = !isObscureConfirm.value })
                    },
                isPassword = !isObscureConfirm.value);


            Spacer(modifier = Modifier.padding(20.dp));

            EventTrackerAppPrimaryButton("Sign Up")
            {
                authViewModel.signUpRequest = SignUpRequest(
                    email = email.value,
                    password = password.value
                )
                authViewModel.signUp(){
                    success,error ->
                    if (success)
                    {
                        navController.navigate("create_profile_screen")
                    }else{
                        Toast.makeText(context, error ?: "Bilinmeyen hata", Toast.LENGTH_LONG).show()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "Or")

            Spacer(modifier = Modifier.padding(8.dp))

            EventTrackerAppOutlinedButton("Login")
            {
                navController.navigate("login_screen")
            }

        }
    }

}

@Composable
private fun painterResource(isObscure:Boolean):Painter
{
    val icon = if(isObscure){
        painterResource(R.drawable.visibility_icon)}
    else{
        painterResource(R.drawable.visibility_off_icon)}

    return icon
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EventTrackerAppTheme {
//        SignUpScreen();
    }
}