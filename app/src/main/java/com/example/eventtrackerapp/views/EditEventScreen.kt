package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.simulateHotReload
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.Category
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.EventWithTags
import com.example.eventtrackerapp.model.Tag
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppAuthTextField
import com.example.eventtrackerapp.utils.EventTrackerAppOutlinedTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.TagViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@SuppressLint("NewApi")
@ExperimentalLayoutApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavHostController,
    eventWithTag: EventWithTags,
    eventViewModel: EventViewModel,
    categoryViewModel: CategoryViewModel,
    tagViewModel: TagViewModel,
    ownerId: String
) {
    // Kategori verisini güvenli şekilde yüklüyoruz
    LaunchedEffect(Unit) {
        categoryViewModel.getAllCategoryWithTags()
    }

    // Tag'leri sıfırla
//    LaunchedEffect(tagViewModel) {
//        tagViewModel.resetTag()
//    }

    // ViewModel'den state'ler
    val categoryId = rememberSaveable{ mutableStateOf(eventWithTag.event.categoryId) }
    val categoryWithTags by categoryViewModel.categoryWithTags.collectAsState()
    val selectedTag by tagViewModel.selectedTag.collectAsStateWithLifecycle()
    val initialTags = eventWithTag.tags
    val chosenTags = remember { mutableStateListOf<Tag>().apply { addAll(initialTags) } }
    val category by categoryViewModel.category.collectAsStateWithLifecycle()
    val events by eventViewModel.allEventsWithTags.collectAsState(initial = emptyList())
    val currentCategoryTags = categoryWithTags.firstOrNull { it.category.id == categoryId.value }?.tags ?: emptyList()

    val context = LocalContext.current

    // Kategori adı null gelirse uygulama çökmemesi için güvenli şekilde al
    val selectedCategoryName = rememberSaveable {
        mutableStateOf(categoryWithTags.firstOrNull { it.category.id == categoryId.value }?.category?.name ?: "")
    }

    // KategoriId değiştiğinde kategori adını güncelle
    LaunchedEffect(categoryId.value) {
        selectedCategoryName.value = categoryWithTags.firstOrNull { it.category.id == categoryId.value }?.category?.name ?: ""
        // Kategori değişince seçili tagları sıfırla
        tagViewModel.resetTag()
    }

    val isExpanded = rememberSaveable{ mutableStateOf(false)}

    // Yükleme ekranı göster (kategori verisi gelmeden önce)
    if (category == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    EventTrackerAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Edit Event", fontSize = 25.sp) },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            "GoBack",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clickable { navController.popBackStack() }
                        )
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {

                val eventName = rememberSaveable { mutableStateOf(eventWithTag.event.name ?:"") }
                val nameError = rememberSaveable{mutableStateOf(false)}

                val eventDetail = rememberSaveable { mutableStateOf(eventWithTag.event.detail ?:"") }
                val detailError = rememberSaveable{mutableStateOf(false)}

                val selectedDate = rememberSaveable { mutableStateOf<Long?>(eventWithTag.event.date) }
                val dateError = rememberSaveable{mutableStateOf(false)}

                val showModal = rememberSaveable { mutableStateOf(false) }

                val eventDuration = rememberSaveable { mutableStateOf(eventWithTag.event.duration ?:"") }
                val durationError = rememberSaveable{mutableStateOf(false)}

                val eventLocation = rememberSaveable { mutableStateOf(eventWithTag.event.location ?:"") }
                val locationError = rememberSaveable{mutableStateOf(false)}

                //TODO burası daha sonra category ile doldurulacak
                val category = rememberSaveable { mutableStateOf(eventWithTag.event.categoryId ) }
                val categoryError = rememberSaveable{mutableStateOf(false)}

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.padding(vertical = 12.dp))

                    //Profil Fotoğrafı
                    Icon(
                        painter = painterResource(R.drawable.image_icon),
                        contentDescription = "Add Image",
                        modifier = Modifier
                            .size(180.dp, 160.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                            .clickable {
                                /*TODO(Buraya fotoğraf yükleme gelecek)*/
                            }
                    )
                    Spacer(Modifier.padding(vertical = 5.dp))
                    Text("you are updated event photo")

                    //Event Name
                    Spacer(Modifier.padding(vertical = 12.dp))
                    EventTrackerAppAuthTextField(
                        txt = "Event Name",
                        state = eventName,
                        onValueChange = {
                            eventName.value = it
                            nameError.value = eventName.value.isBlank()
                        },
                        isError = nameError.value,
                        supportingText = {
                            if(nameError.value){
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        }
                    )

                    //Event Detail
                    Spacer(Modifier.padding(vertical = 8.dp))
                    EventTrackerAppAuthTextField(
                        modifier = Modifier.heightIn(min = 120.dp, max = 200.dp),
                        txt = "Event Detail",
                        state = eventDetail,
                        onValueChange = {
                            eventDetail.value = it
                            detailError.value = eventDetail.value.isBlank()
                        },
                        isError = detailError.value,
                        supportingText = {
                            if(detailError.value){
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        },
                        isSingleLine = false
                    )

                    //Event Date
                    Spacer(Modifier.padding(vertical = 8.dp))
                    ShowDateModal(Modifier, selectedDate, showModal, dateError,context)

                    //Event Duration
                    Spacer(Modifier.padding(vertical = 8.dp))
                    EventTrackerAppAuthTextField(
                        txt = "Event Duration",
                        state = eventDuration,
                        onValueChange = {
                            eventDuration.value = it
                            durationError.value = eventDuration.value.isBlank()
                        },
                        isError = durationError.value,
                        supportingText = {
                            if(durationError.value){
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        }
                    )

                    //Event Location
                    Spacer(Modifier.padding(vertical = 8.dp))
                    EventTrackerAppAuthTextField(
                        txt = "Event Location",
                        state = eventLocation,
                        onValueChange = {
                            eventLocation.value = it
                            locationError.value = eventLocation.value.isBlank()
                        },
                        isError = locationError.value,
                        supportingText = {
                            if(locationError.value){
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        }
                    )

                    //Select Category
                    Spacer(Modifier.padding(vertical = 8.dp))
                    ExposedDropdownMenuBox(
                        expanded = isExpanded.value,
                        onExpandedChange = {
                            isExpanded.value = it
                        }
                    ) {

                        OutlinedTextField(
                            modifier = Modifier.menuAnchor(),
                            value = selectedCategoryName.value.toString(),
                            onValueChange = {},
                            readOnly = true,
                            placeholder = {
                                Text("Event Category")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(isExpanded.value)
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.category_icon),
                                    contentDescription = "Category"
                                )
                            },
                            isError = categoryError.value,
                            supportingText = {
                                if(categoryError.value){
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
                            categoryWithTags.forEach{
                                DropdownMenuItem(
                                    text = {Text("${it.category.name}")},
                                    onClick = {
                                        selectedCategoryName.value = it.category.name ?: ""
                                        categoryError.value = selectedCategoryName.value.isBlank()
                                        isExpanded.value = false
                                        categoryId.value = it.category.id
                                        tagViewModel.updateSelectedCategoryTags(it)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.padding(vertical = 12.dp))
                    /*TODO(BURADA KULLANILAN ROW VE BOX REFACTOR EDİLMELİ)*/
                    Text("Update event tag")
                    LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                        items(currentCategoryTags) { tag ->
                            val isSelected = chosenTags.any { it.id == tag.id }

                            FilterChip(
                                modifier = Modifier.padding(end = 8.dp),
                                selected = isSelected,
                                label = { Text(tag.name ?: "") },
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

                    Spacer(Modifier.padding(vertical = 5.dp))

                    Box(
                        Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentHeight()
                            .defaultMinSize(minHeight = 80.dp)
                            .heightIn(max = 150.dp)
                            .verticalScroll(rememberScrollState())
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                    ){
                        FlowRow(
                            modifier = Modifier.padding(5.dp),
                            maxItemsInEachRow = 4
                        ) {
                            chosenTags.forEach {tag->
                                FilterChip(
                                    modifier = Modifier.padding(end = 3.dp),
                                    selected = true,
                                    label = {Text(tag.name?:"", fontSize = 12.sp, maxLines = 1)},
                                    onClick = {
                                        chosenTags.removeAll { it.id == tag.id }
                                    },
                                    trailingIcon = {
                                        Icon(Icons.Default.Clear,"Clear")
                                    }
                                )
                            }
                        }
                    }

                }

                Spacer(Modifier.padding(vertical = 20.dp))

                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 15.dp)
                ) {
                    EventTrackerAppPrimaryButton(
                        text = "Update Event",
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
                                val event = Event(
                                    id = eventWithTag.event.id,
                                    ownerId = ownerId,
                                    name = eventName.value,
                                    detail = eventDetail.value,
                                    image = R.drawable.ic_launcher_background,
                                    date = selectedDate.value,
                                    duration = eventDuration.value,
                                    location = eventLocation.value,
                                    likeCount = 0,
                                    categoryId = categoryId.value,
                                )
                                eventViewModel.updateEventWithTags(event = event, tags = chosenTags)
                                navController.popBackStack()
                            }
                        })
                }
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    context: Context,
    onDateSelected: (Long?)->Unit,
    onDismiss: ()-> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    val currentDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    if(selectedDate!=null && selectedDate >= currentDate){
                        onDateSelected(selectedDate)
                        onDismiss()
                    }else{
                        Toast.makeText(context,"Geçersiz tarih seçimi", Toast.LENGTH_LONG).show()
                    }
                }
            ){
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    ){
        DatePicker(state = datePickerState)
    }
}















//
//
//    val categoryWithTags by categoryViewModel.categoryWithTags.collectAsState()
//    val selectedTag by tagViewModel.selectedTag.collectAsStateWithLifecycle()
//    val chosenTags by tagViewModel.chosenTags.collectAsStateWithLifecycle()
//
//    var eventWithTags = eventViewModel.getEventWithTagByEventId(eventId = event.id);
//
//    Scaffold(Modifier
//        .fillMaxSize(),
//        topBar = {
//            TopAppBar(
//                title = { Text(text="Edit Event", fontSize = 25.sp)},
//                navigationIcon = {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,null,
//                        modifier = Modifier.padding(start = 8.dp)
//                            .clickable { navController.popBackStack() }
//                    )
//                }
//            )
//        }) { innerPadding ->
//        Box(
//            Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//        ){
//            val eventName = rememberSaveable { mutableStateOf(eventWithTags.event.name ?: "") }
//            val eventDetail = rememberSaveable { mutableStateOf(eventWithTags.event.detail ?: "") }
//            val selectedDate = rememberSaveable { mutableStateOf<Long?>(eventWithTags.event.date) }
//            val showModal = rememberSaveable { mutableStateOf(false)}
//            val eventDuration = rememberSaveable { mutableStateOf(eventWithTags.event.duration ?: "") }
//            val eventLocation = rememberSaveable { mutableStateOf(eventWithTags.event.location ?: "") }
//            val tagIsSelected = rememberSaveable { mutableStateOf(false) }
//            val category = rememberSaveable { mutableStateOf(eventWithTags.event.categoryId) }
//            val selectedTagList = remember { mutableStateListOf<Tag>(eventWithTags.tags) }
//            val isExpanded = rememberSaveable { mutableStateOf(false) }
//
//            val categories = arrayListOf(
//                "Yazılım",
//                "Yapay Zeka",
//                "Back-End",
//                "Front-End",
//                "Teknoloji",
//                "Örnek",
//                "Örnek2",
//                "Örnek3",
//                "Örnek4"
//            )
//
//            Column(
//                Modifier
//                    .fillMaxSize()
//                    .padding(bottom = 90.dp)
//                    .verticalScroll(rememberScrollState()),
//                verticalArrangement = Arrangement.Top,
//                horizontalAlignment = Alignment.CenterHorizontally){
//
//                Spacer(Modifier.padding(12.dp))
//
//                Icon(
//                    painter = painterResource(R.drawable.image_icon),null,
//                    modifier = Modifier
//                        .size(180.dp, 160.dp)
//                        .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
//                        .clickable {
//
//                        }
//                )
//
//                Spacer(Modifier.padding(vertical = 5.dp))
//                Text("you are updated event photo")
//
//                Spacer(Modifier.padding(vertical = 12.dp))
//                EventTrackerAppOutlinedTextField(
//                    txt = "Event Name",
//                    state = eventName,
//                    leadingIcon = {
//                        Icon(Icons.Default.Edit,null)
//                    },
//                )
//
//                Spacer(Modifier.padding(vertical = 12.dp))
//
//                EventTrackerAppOutlinedTextField(
//                    txt = "Event Detail",
//                    state = eventDetail,
//                    leadingIcon = {
//                        Icon(Icons.Default.Info,null)
//                    },
//                )
//
//                Spacer(Modifier.padding(vertical = 12.dp))
//
//                //ShowDateModal(Modifier,selectedDate, showModal)
//
//                Spacer(Modifier.padding(vertical = 12.dp))
//
//                EventTrackerAppOutlinedTextField(
//                    txt = "Event Duration",
//                    state = eventDuration,
//                    leadingIcon = {
//                        Icon(
//                            painter = painterResource(R.drawable.clock_icon),
//                            contentDescription = null
//                        )
//                    }
//                )
//
//                Spacer(Modifier.padding(vertical = 12.dp))
//
//                EventTrackerAppOutlinedTextField(
//                    txt = "Event Location",
//                    state = eventLocation,
//                    leadingIcon = {
//                        Icon(Icons.Default.LocationOn,null)
//                    }
//                )
//
//                Spacer(Modifier.padding(vertical = 12.dp))
//
//                ExposedDropdownMenuBox(
//                    expanded = isExpanded.value,
//                    onExpandedChange = {
//                        isExpanded.value = it
//                    }
//                ) {
//                    OutlinedTextField(
//                        modifier = Modifier.menuAnchor(),
//                        value = category.value,
//                        onValueChange = {},
//                        readOnly = true,
//                        placeholder = {
//                            Text("Event Category")
//                        },
//                        trailingIcon = {
//                            ExposedDropdownMenuDefaults.TrailingIcon(isExpanded.value)
//                        },
//                        leadingIcon = {
//                            Icon(
//                                painter = painterResource(R.drawable.category_icon),
//                                contentDescription = null
//                            )
//                        }
//                    )
//
//                    ExposedDropdownMenu(
//                        expanded = isExpanded.value,
//                        onDismissRequest = {isExpanded.value = false}
//                    ) {
//                        categories.forEach{
//                            DropdownMenuItem(
//                                text = { Text(it)},
//                                onClick = {
//                                    isExpanded.value = false
//                                    category.value = it
//                                }
//                            )
//                        }
//                    }
//                }
//
//                Spacer(Modifier.padding(vertical = 12.dp))
//
//                LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)){
//                    items(categories){
//                        FilterChip(
//                            modifier = Modifier.padding(end = 8.dp),
//                            selected = tagIsSelected.value,
//                            label = {
//                                Text(it)
//                            },
//                            onClick = {
//                                tagIsSelected.value = !tagIsSelected.value
//                                if(!selectedTagList.contains(it)){
//                                    selectedTagList.add(it)
//                                }else{
//                                    selectedTagList.remove(it)
//                                }
//                            },
//                            trailingIcon = if(tagIsSelected.value) {
//                                {
//                                    Icon(
//                                        Icons.Default.Done, null,
//                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
//                                    )
//                                }
//                            } else {
//                                null
//                            }
//                        )
//                    }
//                }
//            }
//
//            Spacer(Modifier.padding(vertical = 20.dp))
//
//            Box(
//                Modifier.align(Alignment.BottomCenter)
//                    .padding(bottom = 15.dp)
//            ) {
//                EventTrackerAppPrimaryButton(text = "Update Event", onClick = {})
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun DatePickerModal(
//    onDateSelected:(Long?) -> Unit,
//    onDismiss:()-> Unit
//) {
//    val datePickerState = rememberDatePickerState()
//
//    DatePickerDialog(
//        onDismissRequest = onDismiss,
//        confirmButton =  {
//            TextButton(
//                onClick = {
//                    onDateSelected(datePickerState.selectedDateMillis)
//                    onDismiss()
//                }
//            ) {
//                Text("Ok")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cancel")
//            }
//        }
//    ) {
//        DatePicker(state = datePickerState)
//    }
//}
