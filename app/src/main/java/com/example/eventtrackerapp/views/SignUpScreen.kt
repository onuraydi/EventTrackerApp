package com.example.eventtrackerapp.views

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eventtrackerapp.Authentication.AuthViewModel
import com.example.eventtrackerapp.Authentication.SignUpRequest
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.common.EventTrackerAppOutlinedButton
import com.example.eventtrackerapp.common.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.common.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.data.source.local.UserPreferences

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userPreferences: UserPreferences
)
{

    val context = LocalContext.current
    val signUpRequest = authViewModel.signUpRequest

    // Burada top-bar, navbar vb. yapıları kullanmadığımız için scaffold iskeletini kullanmaya ihtiyaç duymadık

    Box(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val fullName = remember { mutableStateOf("") }
            val email = remember { mutableStateOf(signUpRequest.email) }
            val password = remember { mutableStateOf(signUpRequest.password) }
            val passwordConfirm = remember { mutableStateOf(signUpRequest.repeatPassword) }
            val isObscure = remember { mutableStateOf(false) }
            val isObscureConfirm = remember { mutableStateOf(false) }

            val fullNameError = rememberSaveable { mutableStateOf(false) }
            val emailError = rememberSaveable { mutableStateOf(false) }
            val passwordError = rememberSaveable { mutableStateOf(false) }
            val passwordConfirmError = rememberSaveable { mutableStateOf(false) }

            Text(
                text = "Sign Up",
                fontWeight = FontWeight.W500,
                fontSize = 25.sp,
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(
                modifier = Modifier
                    .padding(15.dp)
            )

            EventTrackerAppOutlinedTextField(
                txt = "Kullanıcı Adı",
                state = fullName,
                onValueChange =
                {
                    fullName.value = it
                    fullNameError.value = it.isBlank()
                },
                isError = fullNameError.value,
                leadingIcon =
                {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "full name"
                    )
                },
            )

            Spacer(modifier = Modifier.padding(15.dp))

            EventTrackerAppOutlinedTextField(
                txt = "Email",
                state = email,
                onValueChange =
                {
                    email.value = it
                    emailError.value = it.isBlank()
                },
                isError = emailError.value,
                leadingIcon =
                {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "email"
                    )
                },
            )

            Spacer(modifier = Modifier.padding(15.dp))

            EventTrackerAppOutlinedTextField(
                txt = "Şifre",
                state = password,
                onValueChange =
                {
                    password.value = it
                    passwordError.value = it.isBlank()
                },
                isError = passwordError.value,
                leadingIcon =
                {
                    Icon(
                       imageVector = Icons.Filled.Lock,
                       contentDescription = "password"
                    )
                },
                trailingIcon =
                {
                    val icon = if(isObscure.value)
                    {
                        painterResource(R.drawable.visibility_icon)
                    } else
                    {
                        painterResource(R.drawable.visibility_off_icon)
                    }

                    Icon(
                        painter = icon,
                        contentDescription = "visibility",
                        modifier = Modifier
                            .clickable
                            {
                                isObscure.value = !isObscure.value
                            }
                    )
                },
                isPassword = !isObscure.value,
            )

            Spacer(modifier = Modifier.padding(15.dp))

            EventTrackerAppOutlinedTextField(
                txt = "Şifre tekrarı",
                state = passwordConfirm,
                onValueChange =
                {
                    passwordConfirm.value = it
                    passwordConfirmError.value = it.isBlank()
                },
                isError = passwordConfirmError.value,
                leadingIcon =
                {
                    Icon(
                       imageVector = Icons.Filled.Lock,
                        contentDescription = "confirm password"
                    )
                },
                trailingIcon =
                {
                    Icon(
                        painter = painterResource(isObscureConfirm.value),
                        contentDescription = "visibility",
                        modifier = Modifier
                            .clickable
                            {
                                isObscureConfirm.value = !isObscureConfirm.value
                            }
                    )
                },
                isPassword = !isObscureConfirm.value,
            )


            Spacer(modifier = Modifier.padding(20.dp))

            EventTrackerAppPrimaryButton("Sign Up")
            {
                if (fullName.value.isBlank() || email.value.isBlank() || password.value.isBlank() || passwordConfirm.value.isBlank())
                {
                    fullNameError.value = fullName.value.isBlank()
                    emailError.value = email.value.isBlank()
                    passwordError.value = password.value.isBlank()
                    passwordConfirmError.value = passwordConfirm.value.isBlank()
                    return@EventTrackerAppPrimaryButton
                } else {
                    authViewModel.signUpRequest = SignUpRequest(
                        email = email.value,
                        password = password.value,
                        repeatPassword = passwordConfirm.value
                    )
                    authViewModel.signUp { success, error ->
                        if (success)
                        {
                            navController.navigate("create_profile_screen")
                        } else {
                            Toast.makeText(context, error ?: "Bilinmeyen hata", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "Or")

            Spacer(modifier = Modifier.padding(8.dp))

            EventTrackerAppPrimaryButton("Login")
            {
                navController.popBackStack()
            }

        }
    }
}

@Composable
private fun painterResource(isObscure:Boolean):Painter
{
    val icon = if(isObscure)
    {
        painterResource(R.drawable.visibility_icon)
    } else{
        painterResource(R.drawable.visibility_off_icon)
    }
    return icon
}