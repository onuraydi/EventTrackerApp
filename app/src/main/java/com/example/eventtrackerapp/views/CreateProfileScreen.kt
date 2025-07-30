package com.example.eventtrackerapp.views

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.common.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.common.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.common.PermissionHelper
import com.example.eventtrackerapp.common.SelectableImageBox
import com.example.eventtrackerapp.data.source.local.UserPreferences
import com.example.eventtrackerapp.model.roommodels.Category
import com.example.eventtrackerapp.model.roommodels.CategoryWithTag
import com.example.eventtrackerapp.model.roommodels.Profile
import com.example.eventtrackerapp.model.roommodels.Tag
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.PermissionViewModel
import com.example.eventtrackerapp.viewmodel.ProfileViewModel
import com.example.eventtrackerapp.viewmodel.StorageViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateProfileScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel,
    profileViewModel: ProfileViewModel,
    permissionViewModel: PermissionViewModel,
    storageViewModel: StorageViewModel,
    userPreferences: UserPreferences,
    categoryWithTags:List<CategoryWithTag>,
    uid:String,
    email:String
) {
    val selectedTag by categoryViewModel.selectedTag.collectAsStateWithLifecycle()
    val chosenTags by categoryViewModel.chosenTags.collectAsStateWithLifecycle()

    val selectedCompleteTagList = remember{ mutableStateListOf<Tag>() }
    val selectedCompleteCategoryList = remember { mutableStateListOf<Category>() }
    val isShow = rememberSaveable { mutableStateOf(false) }

    //Media Permission
    val context = LocalContext.current
    val permission = permissionViewModel.getPermissionName()

    val uriData = rememberSaveable { mutableStateOf<Uri?>(null) }
    val galleryData = rememberSaveable { mutableStateOf("") }
    val imagePath = storageViewModel.imagePath.collectAsStateWithLifecycle()
    val isUploading = storageViewModel.isUploading.collectAsStateWithLifecycle()

    // Form state variables
    val fullNameState = rememberSaveable { mutableStateOf("") }
    val fullNameError = rememberSaveable{mutableStateOf(false)}
    val userNameState = rememberSaveable { mutableStateOf("") }
    val userNameError = rememberSaveable{mutableStateOf(false)}
    val gender = rememberSaveable { mutableStateOf("") }
    val genderError = rememberSaveable{ mutableStateOf(false) }
    val isExpanded = rememberSaveable { mutableStateOf(false) }

    // Fotoğraf yükleme tamamlandığında profili oluştur
    LaunchedEffect(imagePath.value) {
        if (imagePath.value != null && isUploading.value == false) {
            // Tüm gerekli alanlar dolu mu kontrol et
            if (fullNameState.value.isNotBlank() && 
                userNameState.value.isNotBlank() && 
                gender.value.isNotBlank() && 
                selectedCompleteTagList.isNotEmpty()) {
                
                android.util.Log.d("CreateProfileScreen", "Profil oluşturuluyor: ${fullNameState.value}")
                
                val profile = Profile(
                    id = uid,
                    email = email,
                    fullName = fullNameState.value,
                    userName = userNameState.value,
                    gender = gender.value,
                    selectedCategoryList = selectedCompleteCategoryList,
                    selectedTagList = selectedCompleteTagList,
                    photo = imagePath.value!!
                )
                profileViewModel.upsertProfile(profile)
                navController.navigate("home"){
                    popUpTo("create_profile_screen"){
                        inclusive = true
                    }
                }
            } else {
                android.util.Log.d("CreateProfileScreen", "Form eksik: fullName=${fullNameState.value.isNotBlank()}, userName=${userNameState.value.isNotBlank()}, gender=${gender.value.isNotBlank()}, tags=${selectedCompleteTagList.isNotEmpty()}")
            }
        }
    }

    //galeriye gidip fotoğraf seçmemizi sağlacyacak
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result->
        if(result.resultCode == Activity.RESULT_OK && result.data != null){
            //kullanıcı fotoğrafı seçtiyse ve gelen intent boş değilse
            uriData.value = result.data?.data
            //kullanıcının seçtiği resmi viewModel
            // tarafında doldurup burada imageUri ile aldık
            if(uriData.value!=null){
                val localPath = uriData.value?.let { PermissionHelper.saveImageToInternalStorage(context,it) }
                if(localPath!=null){
                    galleryData.value = localPath
                }
            }
        }
    }

    //izin olaylarını tutacak
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted->
        if(granted){
            PermissionHelper.goToGallery(imagePickerLauncher)
        }else{
            Toast.makeText(context,"İzin kalıcı olarak reddedildi. Lütfen ayarlardan izin verin",Toast.LENGTH_LONG).show()
        }
    }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                    title = { Text(text = "Complete Your Profile", color = Color.White) },

                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {


                Column(
                    modifier = Modifier
                        .padding(bottom = 80.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.padding(vertical = 15.dp))

                    //Profil Fotoğrafı
                    SelectableImageBox(
                        boxWidth = 80.dp,
                        boxHeight = 80.dp,
                        imagePath = galleryData.value,
                        modifier = Modifier,
                        placeHolder = painterResource(R.drawable.profile_photo_add_icon),
                        shape = CircleShape,
                        onClick = {
                            PermissionHelper.requestPermission(
                                context= context,
                                permission = permission,
                                viewModel = permissionViewModel,
                                imagePickerLauncher = imagePickerLauncher,
                                permissionLauncher = permissionLauncher,
                            )
                        }
                    )

                    Spacer(Modifier.padding(vertical = 5.dp))

                    Text(
                        text = "Add Profile Photo",
                    )

                    Spacer(Modifier.padding(vertical = 7.dp))

                    EventTrackerAppOutlinedTextField(
                        txt = "Fullname",
                        state = fullNameState,
                        onValueChange = {
                            fullNameState.value = it
                            fullNameError.value = fullNameState.value.isBlank()
                        },
                        isError = fullNameError.value
                    )

                    Spacer(Modifier.padding(vertical = 12.dp))

                    EventTrackerAppOutlinedTextField(
                        txt = "Username",
                        state = userNameState,
                        onValueChange = {
                            userNameState.value = it
                            userNameError.value = userNameState.value.isBlank()
                        },
                        isError = userNameError.value
                    )
                    Spacer(Modifier.padding(vertical = 12.dp))

                    ExposedDropdownMenuBox(
                        expanded = isExpanded.value,
                        onExpandedChange = {
                            isExpanded.value = it
                        }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(),
                            value = gender.value,
                            onValueChange = {
                                genderError.value = gender.value.isBlank()
                            },
                            placeholder = { Text("Gender") },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
                            },
                            isError = genderError.value
                        )

                        ExposedDropdownMenu(
                            expanded = isExpanded.value,
                            onDismissRequest = { isExpanded.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Male") },
                                onClick = {
                                    gender.value = "Male"
                                    isExpanded.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Female") },
                                onClick = {
                                    gender.value = "Female"
                                    isExpanded.value = false
                                }
                            )
                        }
                    }

                    Spacer(Modifier.padding(vertical = 18.dp))

                    Text(
                        "Please select category",
                        modifier = Modifier.fillMaxWidth(0.9f),
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.padding(vertical = 3.dp))

                    //TODO BU KATEGORİ ALANINDA SELECTED NASIL HALLOLUR
                    //TODO HATA: Kullanıcı kategoriyi seçtikten sonra etiketleri seçmeden başka kategori seçebiliyor
                    if(categoryWithTags.isEmpty()){
                        CircularProgressIndicator()
                    }else{
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                            items(categoryWithTags) {
                                val selected = rememberSaveable { mutableStateOf(false) }

                                FilterChip(
                                    modifier = Modifier.padding(end = 8.dp),
                                    selected = selected.value,
                                    label = { Text(it.category.name) },
                                    onClick = {
                                        selected.value = !selected.value
                                        isShow.value = selected.value

                                        if(selected.value){
                                            categoryViewModel.updateSelectedCategoryTags(it) //selectedTags ı doldurur
                                            selectedCompleteCategoryList.add(it.category) //categoryWithTag eklendi
                                        }else{
                                            // Kategori kaldırıldı → hem chosenTags hem selectedCompleteTagList içinden temizle
                                            categoryViewModel.resetChosenTagForCategory(it.category.id)

                                            selectedCompleteCategoryList.removeAll{category-> category.id == it.category.id}

                                            // Compose'daki selectedCompleteTagList içinden bu kategoriye ait tag’leri çıkar
                                            selectedCompleteTagList.removeAll{tag-> tag.categoryId == it.category.id }
                                        }
                                    },
                                    trailingIcon = if (selected.value) {
                                        {
                                            Icon(
                                                Icons.Filled.Done,
                                                "Done",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        }
                                    } else {
                                        null
                                    }

                                )
                            }
                        }
                    }

                    Spacer(Modifier.padding(vertical = 12.dp))

                    if(isShow.value){
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                            items(selectedTag) {tag->

                                val isSelected = chosenTags.any { it.id == tag.id }

                                FilterChip(
                                    modifier = Modifier.padding(end = 8.dp),
                                    selected = isSelected,
                                    label = { Text(tag.name) },
                                    onClick = {
                                        categoryViewModel.toggleTag(tag)
                                        if(!selectedCompleteTagList.any{it.id == tag.id}){
                                            selectedCompleteTagList.add(tag)
                                        }else if(isSelected){
                                            selectedCompleteTagList.removeAll{it.id == tag.id}
                                        }
                                    },
                                    trailingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                Icons.Filled.Done,
                                                "Done",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        }
                                    } else {
                                        null
                                    }

                                )
                            }
                        }
                    }

                    Spacer(Modifier.padding(vertical = 5.dp))

                    Box(
                        Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentHeight()
                            .defaultMinSize(minHeight = 80.dp)
                            .heightIn(max = 150.dp)
                            .verticalScroll(rememberScrollState())
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                    ) {
                        FlowRow(
                            Modifier.padding(5.dp),
                            maxItemsInEachRow = 4,
                        ) {
                            selectedCompleteTagList.forEach {tag->
                                FilterChip(
                                    modifier = Modifier.padding(end = 3.dp),
                                    label = { Text(tag.name, fontSize = 12.sp, maxLines = 1) },
                                    selected = true,
                                    onClick = {
                                        categoryViewModel.removeChosenTag(tag)
                                        selectedCompleteTagList.remove(tag)
                                    },
                                    trailingIcon = {
                                        Icon(Icons.Default.Clear, "Clear")
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.padding(vertical = 20.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 15.dp)
                ){
                    EventTrackerAppPrimaryButton(
                        text = if (isUploading.value) "Yükleniyor..." else "Tamamla",
                        enabled = !isUploading.value
                    ) {
                        android.util.Log.d("CreateProfileScreen", "Tamamla butonuna tıklandı")
                        android.util.Log.d("CreateProfileScreen", "uriData: ${uriData.value}, imagePath: ${imagePath.value}, isUploading: ${isUploading.value}")
                        
                        if(fullNameState.value.isBlank() || userNameState.value.isBlank() || gender.value.isBlank() || selectedCompleteTagList.size == 0){
                            fullNameError.value = fullNameState.value.isBlank()
                            userNameError.value = userNameState.value.isBlank()
                            genderError.value = gender.value.isBlank()
                            android.util.Log.d("CreateProfileScreen", "Form validasyonu başarısız")
                            return@EventTrackerAppPrimaryButton
                        }else{
                            // Eğer fotoğraf seçilmişse ve henüz yüklenmemişse
                            if(uriData.value != null && imagePath.value == null && !isUploading.value){
                                android.util.Log.d("CreateProfileScreen", "Fotoğraf yükleniyor...")
                                storageViewModel.setImageToStorage(uriData.value!!, uid)
                            } else if(uriData.value == null) {
                                // Fotoğraf seçilmemiş, direkt profil oluştur
                                android.util.Log.d("CreateProfileScreen", "Fotoğraf olmadan profil oluşturuluyor")
                                val profile = Profile(
                                    id = uid,
                                    email = email,
                                    fullName = fullNameState.value,
                                    userName = userNameState.value,
                                    gender = gender.value,
                                    selectedCategoryList = selectedCompleteCategoryList,
                                    selectedTagList = selectedCompleteTagList,
                                    photo = ""
                                )
                                profileViewModel.upsertProfile(profile)
                                navController.navigate("home"){
                                    popUpTo("create_profile_screen"){
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
}

//@Preview(showBackground = true)
//@Composable
//fun Preview(){
//    EventTrackerAppTheme {
////        CreateProfileScreen()
//    }
//}