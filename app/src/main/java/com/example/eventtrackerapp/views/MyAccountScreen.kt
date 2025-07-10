package com.example.eventtrackerapp.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCbrt
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Profile
import com.example.eventtrackerapp.common.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.common.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.common.EventTrackerTopAppBar
import com.example.eventtrackerapp.viewmodel.ProfileViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAccountScreen(
    navController:NavController,
    profile:Profile,
    profileViewModel: ProfileViewModel
) {
    Scaffold(modifier = Modifier
        .fillMaxSize(),
        topBar = {
            EventTrackerTopAppBar(
                title = "My Account",
                modifier = Modifier,
                showBackButton = true,
                onBackClick =
                {
                    navController.popBackStack()
                },
            )
        }
    ) { innnerPadding ->

        Box(
            modifier = Modifier
                .padding(innnerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val fullNameState = rememberSaveable { mutableStateOf(profile.fullName!!) }
            val userNameState = rememberSaveable { mutableStateOf(profile.userName!!) }
            val emailState = rememberSaveable { mutableStateOf(profile.email!!) }
            val passwordState = rememberSaveable { mutableStateOf("") }  // burası düzeltilececk
            val gender = rememberSaveable { mutableStateOf(profile.gender!!) }
            val isExpanded = rememberSaveable { mutableStateOf(false) }
            val profilePhotoState = rememberSaveable { mutableStateOf(profile.photo) }

            val fullNameError = rememberSaveable { mutableStateOf(false)}
            var userNameError = rememberSaveable { mutableStateOf(false)}
            val emailError = rememberSaveable { mutableStateOf(false)}
            val genderError = rememberSaveable { mutableStateOf(false)}

            Column(
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 15.dp)
                )

                Box(
                    modifier = Modifier
                        .size(80.dp, 80.dp)
                        .border(
                            border = BorderStroke(2.dp, Color.Black),
                            shape = CircleShape
                        )
                        .clickable
                        {
                            // TODO
                        }
                ) {
                    if(profilePhotoState.value != "")
                    {
                        val imageFile = profilePhotoState.value?.let { File(it) }
                        if(imageFile!=null && imageFile.exists())
                        {
                            AsyncImage(
                                model = imageFile,
                                modifier = Modifier
                                    .fillMaxSize(0.8f)
                                    .align(Alignment.Center)
                                    .padding(start = 5.dp),
                                contentDescription = "PhotoAdd",
                            )
                        }else
                        {
                            Image(
                                painter = painterResource(R.drawable.ic_launcher_background),
                                modifier = Modifier
                                    .fillMaxSize(0.8f)
                                    .align(Alignment.Center)
                                    .padding(start = 5.dp),
                                contentDescription = "PhotoAdd",
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier
                    .padding(vertical = 5.dp)
                )

                Text(
                    text = "Update Profile Photo",
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { }
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 7.dp)
                )


                EventTrackerAppOutlinedTextField(
                    txt = "Full Name",
                    state = fullNameState,
                    onValueChange =
                    {
                        fullNameState.value = it
                        fullNameError.value = it.isBlank()
                    },
                    isError = fullNameError.value
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                )

                EventTrackerAppOutlinedTextField(
                    txt = "Username",
                    state = userNameState,
                    onValueChange =
                    {
                        userNameState.value = it
                        userNameError.value = it.isBlank()
                    },
                    isError = userNameError.value
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                )


                ExposedDropdownMenuBox(
                    expanded = isExpanded.value,
                    onExpandedChange = { isExpanded.value = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor(),
                        value = gender.value,
                        onValueChange =
                        {
                            gender.value = it
                            genderError.value = it.isBlank()
                        },
                        placeholder =
                        {
                            Text(
                                text = "Gender"
                            )
                        },
                        readOnly = true,
                        trailingIcon =
                        {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
                        },
                        isError = genderError.value
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded.value,
                        onDismissRequest = { isExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text =
                            {
                                Text(
                                    text = "Male"
                                )
                            },
                            onClick =
                            {
                                gender.value = "Male"
                                isExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text =
                            {
                                Text(
                                    text = "Female"
                                )
                            },
                            onClick =
                            {
                                gender.value = "Female"
                                isExpanded.value = false
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                )

                EventTrackerAppOutlinedTextField(
                    txt = "email",
                    state = emailState,
                    isReadOnly = true,
                    trailingIcon =
                    {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Edit Email")
                        Modifier.clickable {
                            // TODO
                        }
                    },
                    onValueChange =
                    {
                        emailState.value = it
                        emailError.value = it.isBlank()
                    },
                    isError = emailError.value
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                )


                EventTrackerAppOutlinedTextField(
                    txt = "Şifre",
                    state = passwordState,
                    isPassword = true,
                    isReadOnly = true,
                    trailingIcon =
                    {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Edit Password")
                            Modifier.clickable {
                                // TODO
                            }
                    },
                    onValueChange =
                    {
                        passwordState.value = it
                    },
                    isError = false
                )
            }

            Spacer(
                modifier = Modifier
                    .padding(vertical = 20.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                EventTrackerAppPrimaryButton("Complete")
                {
                    if(fullNameState.value.isBlank() || userNameState.value.isBlank() || gender.value.isBlank())
                    {
                        fullNameError.value = fullNameState.value.isBlank()
                        userNameError.value = userNameState.value.isBlank()
                        genderError.value = gender.value.isBlank()
                        return@EventTrackerAppPrimaryButton
                    }else {
                        val updatedProfile = Profile(
                            id = profile.id,
                            email = profile.email,
                            fullName = fullNameState.value,
                            userName = userNameState.value,
                            gender = gender.value,
                            photo = profilePhotoState.value,
                            selectedCategoryList = profile.selectedCategoryList,
                            addedEvents = profile.addedEvents,
                            selectedTagList = profile.selectedTagList
                        )
                        profileViewModel.updateProfile(updatedProfile)
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}