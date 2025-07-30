package com.example.eventtrackerapp.views

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.common.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.common.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.common.EventTrackerTopAppBar
import com.example.eventtrackerapp.common.PermissionHelper
import com.example.eventtrackerapp.common.SelectableImageBox
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.viewmodel.PermissionViewModel
import com.example.eventtrackerapp.viewmodel.ProfileViewModel
import com.example.eventtrackerapp.viewmodel.StorageViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MyAccountScreen(
    navController:NavController,
    profile: Profile,
    profileViewModel: ProfileViewModel,
    permissionViewModel: PermissionViewModel,
    storageViewModel: StorageViewModel
) {
    //Media Permission
    val context = LocalContext.current
    val permission = permissionViewModel.getPermissionName()
    val profilePhotoState = rememberSaveable { mutableStateOf(profile.photo) }

    val uriData = rememberSaveable { mutableStateOf<Uri?>(null) }
    val isUploading = storageViewModel.isUploading.collectAsStateWithLifecycle()
    val imagePath = storageViewModel.profileImagePath.collectAsStateWithLifecycle()

    //TextField states
    val fullNameState = rememberSaveable { mutableStateOf(profile.fullName) }
    val userNameState = rememberSaveable { mutableStateOf(profile.userName) }
    val emailState = rememberSaveable { mutableStateOf(profile.email) }
    val passwordState = rememberSaveable { mutableStateOf("") }  // burası düzeltilececk
    val gender = rememberSaveable { mutableStateOf(profile.gender) }
    val isExpanded = rememberSaveable { mutableStateOf(false) }

    val fullNameError = rememberSaveable { mutableStateOf(false)}
    var userNameError = rememberSaveable { mutableStateOf(false)}
    val emailError = rememberSaveable { mutableStateOf(false)}
    val genderError = rememberSaveable { mutableStateOf(false)}

    // Fotoğraf yükleme tamamlandığında profili düzenle
    LaunchedEffect(imagePath.value) {
        if (imagePath.value != null && isUploading.value == false) {
            // Tüm gerekli alanlar dolu mu kontrol et
            if (fullNameState.value.isNotBlank() &&
                userNameState.value.isNotBlank() &&
                gender.value.isNotBlank() &&
                profile.selectedTagList.isNotEmpty()) {

                android.util.Log.d("CreateProfileScreen", "Profil oluşturuluyor: ${fullNameState.value}")

                val profile = Profile(
                    id = profile.id,
                    email = emailState.value,
                    fullName = fullNameState.value,
                    userName = userNameState.value,
                    gender = gender.value,
                    selectedCategoryList = profile.selectedCategoryList,
                    selectedTagList = profile.selectedTagList,
                    photo = imagePath.value!!
                )
                profileViewModel.updateProfile(profile)
//                navController.popBackStack()
            } else {
                android.util.Log.d("CreateProfileScreen", "Form eksik: fullName=${fullNameState.value.isNotBlank()}, userName=${userNameState.value.isNotBlank()}, gender=${gender.value.isNotBlank()}, tags=${profile.selectedTagList.isNotEmpty()}")
            }
        }
    }


    //galeriye gidip fotoğraf seçmemizi sağlayacak
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result->
        if(result.resultCode == Activity.RESULT_OK && result.data != null){
            uriData.value = result.data?.data
            if(uriData.value!=null){
                val localPath = uriData.value?.let { PermissionHelper.saveImageToInternalStorage(context,it)}
                if (localPath != null) {
                    profilePhotoState.value = localPath
                }
            }
        }
    }

    //izin olaylarını ele alan launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){granted->
        if(granted){
            //kullanıcı izin verdi
            PermissionHelper.goToGallery(imagePickerLauncher)
        }else{
            Toast.makeText(context,"İzin kalıcı olarak reddedildi.Lütfen ayarlardan izin verin.",Toast.LENGTH_LONG).show()
        }
    }

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

                SelectableImageBox(
                    boxWidth = 80.dp,
                    boxHeight = 80.dp,
                    imagePath = profilePhotoState.value,
                    modifier = Modifier,
                    placeHolder = painterResource(R.drawable.profile_photo_add_icon),
                    shape = CircleShape,
                    onClick = {
                        PermissionHelper.requestPermission(
                            context,
                            permission= permission,
                            viewModel = permissionViewModel,
                            permissionLauncher = permissionLauncher,
                            imagePickerLauncher = imagePickerLauncher
                        )
                    }
                )

                Spacer(modifier = Modifier
                    .padding(vertical = 5.dp)
                )

                Text(
                    text = "Update Profile Photo",
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        //TODO resim yükleme
                    }
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
                EventTrackerAppPrimaryButton(
                    text = if (isUploading.value) "Yükleniyor..." else "Tamamla",
                    enabled = !isUploading.value
                )
                {
                    // Eğer fotoğraf seçilmişse ve henüz yüklenmemişse
                    if(uriData.value != null && imagePath.value == null && !isUploading.value){
                        android.util.Log.d("CreateProfileScreen", "Fotoğraf yükleniyor...")
                        storageViewModel.setProfileImageToStorage(uriData.value!!, profile.id)
                    } else if(uriData.value == null) {
                        // Fotoğraf seçilmemiş, direkt profil oluştur
                        android.util.Log.d("CreateProfileScreen", "Fotoğraf olmadan profil oluşturuluyor")
                        val profile = Profile(
                            id = profile.id,
                            email = profile.email,
                            fullName = fullNameState.value,
                            userName = userNameState.value,
                            gender = gender.value,
                            selectedCategoryList = profile.selectedCategoryList,
                            selectedTagList = profile.selectedTagList,
                            addedEventIds = profile.addedEventIds,
                            photo = ""
                        )
                        profileViewModel.updateProfile(profile)
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}