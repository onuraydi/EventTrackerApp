package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.common.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.common.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.common.EventTrackerTopAppBar
import com.example.eventtrackerapp.common.PermissionHelper
import com.example.eventtrackerapp.common.SelectableImageBox
import com.example.eventtrackerapp.model.roommodels.Event
import com.example.eventtrackerapp.model.roommodels.Tag
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.PermissionViewModel
import com.example.eventtrackerapp.viewmodel.StorageViewModel


@SuppressLint("NewApi", "AutoboxingStateCreation")
@ExperimentalLayoutApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavHostController,
    eventId:String,
    eventViewModel: EventViewModel,
    categoryViewModel: CategoryViewModel,
    permissionViewModel: PermissionViewModel,
    storageViewModel: StorageViewModel,
    ownerId: String
) {
    val context = LocalContext.current

    val categoryWithTags by categoryViewModel.getAllCategoryWithTags().collectAsState(emptyList())

    val eventWithTags = eventViewModel.getEventWithRelationsById(eventId).collectAsState(null)

    // ViewModel'den state'ler
    val categoryId = rememberSaveable { mutableStateOf<String?>(null) }

    val selectedCategoryName = rememberSaveable { mutableStateOf("") }
    val chosenTags = remember { mutableStateListOf<Tag>() }

    //Event States
    val eventName = rememberSaveable { mutableStateOf("") }
    val nameError = rememberSaveable { mutableStateOf(false) }

    val eventDetail = rememberSaveable { mutableStateOf("") }
    val detailError = rememberSaveable { mutableStateOf(false) }

    val selectedDate = rememberSaveable { mutableStateOf<Long?>(null) }
    val dateError = rememberSaveable { mutableStateOf(false) }

    val showModal = rememberSaveable { mutableStateOf(false) }

    val eventDuration = rememberSaveable { mutableStateOf("") }
    val durationError = rememberSaveable { mutableStateOf(false) }

    val eventLocation = rememberSaveable { mutableStateOf("") }
    val locationError = rememberSaveable { mutableStateOf(false) }

    val categoryError = rememberSaveable { mutableStateOf(false) }

    val eventImage = rememberSaveable { mutableStateOf<String?>(null) }

    //Media Permission
    val permission = permissionViewModel.getPermissionName()
    val uriData = rememberSaveable { mutableStateOf<Uri?>(null) }
    val isUploading = storageViewModel.isUploading.collectAsStateWithLifecycle(false)
    val imagePath = storageViewModel.imagePath.collectAsStateWithLifecycle()

    // Event verisi yüklendiğinde state'leri güncelle
    LaunchedEffect(eventWithTags.value) {
        eventWithTags.value?.event?.let { event ->
            eventName.value = event.name ?: ""
            eventDetail.value = event.detail ?: ""
            selectedDate.value = event.date
            eventDuration.value = event.duration ?: ""
            eventLocation.value = event.location ?: ""
            eventImage.value = event.image
            categoryId.value = event.categoryId
            
            // Storage state'lerini temizle
            uriData.value = null
            storageViewModel.clearImagePath()
        }
    }

    LaunchedEffect(imagePath.value, isUploading.value) {
        if(imagePath.value != null && isUploading.value == false){
            // Tüm gerekli alanlar dolu mu kontrol et
            if (eventName.value.isNotBlank() &&
                eventDetail.value.isNotBlank() &&
                eventDuration.value.isNotBlank() &&
                eventLocation.value.isNotBlank() &&
                selectedCategoryName.value.isNotBlank() &&
                selectedDate.value != null &&
                chosenTags.isNotEmpty()) {
                val event = Event(
                    id = eventId,
                    ownerId = ownerId,
                    name = eventName.value,
                    detail = eventDetail.value,
                    image = imagePath.value!!,
                    date = selectedDate.value!!,
                    duration = eventDuration.value,
                    location = eventLocation.value,
                    likeCount = eventWithTags.value?.event?.likeCount ?: 0,
                    categoryId = categoryId.value!!,
                )
                eventViewModel.updateEvent(event, chosenTags)
                navController.popBackStack()
            } else {
                android.util.Log.d("EditEventScreen", "Form eksik")
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result->
        if(result.resultCode == Activity.RESULT_OK && result.data!=null){
            uriData.value = result.data?.data
            if(uriData.value!=null){
                val localPath = PermissionHelper.saveImageToInternalStorage(context,uriData.value!!)
                eventImage.value = localPath
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted->
        if(granted){
            PermissionHelper.goToGallery(imagePickerLauncher)
        }else{
            Toast.makeText(context,"Fotoğraf yüklemek için galeri iznine ihtiyaç var.",Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(categoryWithTags, eventWithTags.value) {
        if (categoryWithTags.isNotEmpty() && eventWithTags.value != null) {
            val selectedCategory = categoryWithTags.firstOrNull { it.category.id == eventWithTags.value?.event?.categoryId}
            if (selectedCategory != null) {
                selectedCategoryName.value = selectedCategory.category.name
                categoryId.value = selectedCategory.category.id
                chosenTags.clear()
                eventWithTags.value?.tags?.let { chosenTags.addAll(it) }
            }
        }
    }

    // KategoriId veya categoryWithTags değişince, seçili kategoriye ait tag'ları güncelle
    val currentCategoryTags = remember(categoryId.value, categoryWithTags) {
        categoryWithTags.firstOrNull { it.category.id == categoryId.value }?.tags ?: emptyList()
    }

    // KategoriId veya categoryWithTags değişince, seçili kategori adı ve tag'ları güncelle
    LaunchedEffect(categoryId.value, categoryWithTags, eventWithTags.value) {
        val selected = categoryWithTags.firstOrNull { it.category.id == categoryId.value }
        selectedCategoryName.value = selected?.category?.name ?: ""
        if (categoryId.value != eventWithTags.value?.event?.categoryId) {
            chosenTags.clear()
        } else {
            chosenTags.clear()
            eventWithTags.value?.tags?.let { chosenTags.addAll(it) }
        }
        categoryViewModel.resetTag()
    }

    val isExpanded = rememberSaveable { mutableStateOf(false) }

    if (categoryWithTags.isEmpty() || eventWithTags.value == null)
    {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar =
        {
            EventTrackerTopAppBar(
                title = "Edit Event",
                modifier = Modifier,
                showBackButton = true,
                onBackClick =
                {
                    navController.popBackStack()
                },
            )
        }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(
                    Modifier
                        .padding(vertical = 12.dp)
                )

                SelectableImageBox(
                    boxWidth = 180.dp,
                    boxHeight = 160.dp,
                    imagePath = eventImage.value,
                    modifier = Modifier,
                    placeHolder = painterResource(R.drawable.image_icon),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        PermissionHelper.requestPermission(
                            context = context,
                            permission = permission,
                            viewModel = permissionViewModel,
                            permissionLauncher = permissionLauncher,
                            imagePickerLauncher = imagePickerLauncher
                        )
                    }
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                )

                Text(
                    text = "you are updated event photo"
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                )

                EventTrackerAppOutlinedTextField(
                    txt = "Event Name",
                    state = eventName,
                    onValueChange =
                    {
                        eventName.value = it
                        nameError.value = eventName.value.isBlank()
                    },
                    isError = nameError.value,
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                EventTrackerAppOutlinedTextField(
                    modifier = Modifier
                        .heightIn(min = 120.dp, max = 200.dp),
                    txt = "Event Detail",
                    state = eventDetail,
                    onValueChange =
                    {
                        eventDetail.value = it
                        detailError.value = eventDetail.value.isBlank()
                    },
                    isError = detailError.value,
                    isSingleLine = false
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                ShowDateModal(
                    modifier = Modifier,
                    dateState = selectedDate,
                    modalState = showModal,
                    dateErrorState = dateError,
                    context = context
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                EventTrackerAppOutlinedTextField(
                    txt = "Event Duration",
                    state = eventDuration,
                    onValueChange =
                    {
                        eventDuration.value = it
                        durationError.value = eventDuration.value.isBlank()
                    },
                    isError = durationError.value,
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                EventTrackerAppOutlinedTextField(
                    txt = "Event Location",
                    state = eventLocation,
                    onValueChange =
                    {
                        eventLocation.value = it
                        locationError.value = eventLocation.value.isBlank()
                    },
                    isError = locationError.value,
                )

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = isExpanded.value,
                    onExpandedChange =
                    {
                        isExpanded.value = it
                    }
                ) {

                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
                        value = selectedCategoryName.value,
                        onValueChange = {},
                        readOnly = true,
                        placeholder =
                        {
                            Text("Event Category")
                        },
                        trailingIcon =
                        {
                            ExposedDropdownMenuDefaults.TrailingIcon(isExpanded.value)
                        },
                        leadingIcon =
                        {
                            Icon(
                                painter = painterResource(R.drawable.category_icon),
                                contentDescription = "Category"
                            )
                        },
                        isError = categoryError.value,
                        supportingText =
                        {
                            if (categoryError.value)
                            {
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        }
                    )

                        ExposedDropdownMenu(
                            expanded = isExpanded.value,
                            onDismissRequest = {
                                isExpanded.value = false
                            }
                        )
                        {
                            categoryWithTags.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.category.name) },
                                    onClick = {
                                        selectedCategoryName.value = it.category.name
                                        categoryError.value = selectedCategoryName.value.isBlank()
                                        isExpanded.value = false
                                        categoryId.value = it.category.id
                                        categoryViewModel.updateSelectedCategoryTags(it)
                                    }
                                )
                            }
                        }
                    }

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                )

                Text(
                    text = "Update event tag"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(currentCategoryTags)
                    { tag ->
                        val isSelected = chosenTags.any { it.id == tag.id }

                            FilterChip(
                                modifier = Modifier.padding(end = 8.dp),
                                selected = isSelected,
                                label = { Text(tag.name) },
                                onClick = {
                                    if (chosenTags.any { it.id == tag.id }) {
                                        chosenTags.removeAll { it.id == tag.id }
                                    } else {
                                        chosenTags.add(tag)
                                    }
                                },
                                trailingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            Icons.Default.Done,
                                            "Done",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    }

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                )

                Box(
                    Modifier
                        .fillMaxWidth(fraction = .9f)
                        .wrapContentHeight()
                        .defaultMinSize(minHeight = 80.dp)
                        .heightIn(max = 150.dp)
                        .verticalScroll(rememberScrollState())
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    FlowRow(
                        modifier = Modifier.padding(5.dp),
                        maxItemsInEachRow = 4
                    ) {
                        chosenTags.forEach { tag ->
                            FilterChip(
                                modifier = Modifier.padding(end = 3.dp),
                                selected = true,
                                label = { Text(tag.name ?: "", fontSize = 12.sp, maxLines = 1) },
                                onClick =
                                {
                                    chosenTags.removeAll { it.id == tag.id }
                                },
                                trailingIcon =
                                {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear"
                                    )
                                }
                            )
                        }
                    }
                }

            }

            Spacer(
                modifier = Modifier
                    .padding(vertical = 20.dp)
            )

                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 15.dp)
                ) {
                    EventTrackerAppPrimaryButton(
                        text = "Update Event",
                        enabled = !isUploading.value,
                        onClick = {
                            if (
                                eventName.value.isBlank() ||
                                eventDetail.value.isBlank() ||
                                eventDuration.value.isBlank() ||
                                eventLocation.value.isBlank() ||
                                selectedCategoryName.value.isBlank() ||
                                selectedDate.value == null ||
                                chosenTags.isEmpty()
                            ) {
                                nameError.value = eventName.value.isBlank()
                                detailError.value = eventDetail.value.isBlank()
                                durationError.value = eventDuration.value.isBlank()
                                locationError.value = eventLocation.value.isBlank()
                                dateError.value = selectedDate.value == null
                                categoryError.value = selectedCategoryName.value.isBlank()
                                return@EventTrackerAppPrimaryButton
                            } else {
                                // Eğer fotoğraf seçilmişse ve henüz yüklenmemişse
                                if(uriData.value != null && imagePath.value == null && !isUploading.value){
                                    android.util.Log.d("EditEventScreen", "Fotoğraf yükleniyor...")
                                    storageViewModel.setImageToStorage(uriData.value!!, ownerId)
                                } else {
                                    // Fotoğraf seçilmemiş veya yeni resim yüklenmiş
                                    val finalImagePath = if(imagePath.value != null) imagePath.value!! else eventImage.value!!
                                    android.util.Log.d("EditEventScreen", "Event güncelleniyor - Resim: $finalImagePath")
                                    val event = Event(
                                        id = eventId,
                                        ownerId = ownerId,
                                        name = eventName.value,
                                        detail = eventDetail.value,
                                        image = finalImagePath,
                                        date = selectedDate.value!!,
                                        duration = eventDuration.value,
                                        location = eventLocation.value,
                                        likeCount = eventWithTags.value?.event?.likeCount ?: 0,
                                        categoryId = categoryId.value!!,
                                    )
                                    eventViewModel.updateEvent(event, chosenTags)
                                    navController.popBackStack()
                                }
                            }
                        })
                }
            }
        }
    }