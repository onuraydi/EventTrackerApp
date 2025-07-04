package com.example.eventtrackerapp.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.eventtrackerapp.model.Event
import com.example.eventtrackerapp.model.EventWithTags
import com.example.eventtrackerapp.model.Tag
import com.example.eventtrackerapp.ui.theme.EventTrackerAppTheme
import com.example.eventtrackerapp.utils.EventTrackerAppAuthTextField
import com.example.eventtrackerapp.utils.EventTrackerAppPrimaryButton
import com.example.eventtrackerapp.viewmodel.CategoryViewModel
import com.example.eventtrackerapp.viewmodel.EventViewModel
import com.example.eventtrackerapp.viewmodel.TagViewModel



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
    // Kategori verisini ilk kez güvenli şekilde yüklüyoruz
    // Bu, Composable ilk oluşturulduğunda bir kez tetiklenecektir.
    LaunchedEffect(Unit) {
        categoryViewModel.getAllCategoryWithTags()
    }

    // ViewModel'den state'ler
    val categoryId = rememberSaveable { mutableStateOf(eventWithTag.event.categoryId) }
    val categoryWithTags by categoryViewModel.categoryWithTags.collectAsState()
    val category by categoryViewModel.category.collectAsStateWithLifecycle() // Bu state'in kullanım amacını gözden geçirmek faydalı olabilir, tüm kategoriler categoryWithTags içinde.

    // Başlangıçta etkinliğin mevcut tag'ları
    val initialTags = remember { mutableStateListOf<Tag>().apply { addAll(eventWithTag.tags) } }
    // Seçilen tag'lar, başlangıçta etkinliğin mevcut tag'ları ile yükleniyor
    val chosenTags = remember { mutableStateListOf<Tag>().apply { addAll(initialTags) } }

    val currentCategoryTags = remember(categoryId.value, categoryWithTags) {
        categoryWithTags.firstOrNull { it.category.id == categoryId.value }?.tags ?: emptyList()
    }

    val context = LocalContext.current

    // Kategori adı null gelirse uygulama çökmemesi için güvenli şekilde al
    val selectedCategoryName = rememberSaveable {
        mutableStateOf(categoryWithTags.firstOrNull { it.category.id == categoryId.value }?.category?.name ?: "")
    }

    // KategoriId değiştiğinde kategori adını ve seçili tagları güncelle
    LaunchedEffect(categoryId.value) {
        selectedCategoryName.value = categoryWithTags.firstOrNull { it.category.id == categoryId.value }?.category?.name ?: ""

        // Eğer seçilen kategori, etkinliğin başlangıçtaki kategorisi değilse,
        // chosenTags listesini temizle. Aksi takdirde, orijinal tagları geri yükle.
        if (categoryId.value != eventWithTag.event.categoryId) {
            chosenTags.clear()
        } else {
            // Eğer orijinal kategoriye geri dönülürse, başlangıçtaki tagları yeniden yükle
            chosenTags.clear()
            chosenTags.addAll(initialTags)
        }
        tagViewModel.resetTag() // ViewModel içindeki global tag state'ini sıfırla (eğer kullanılıyorsa)
    }

    val isExpanded = rememberSaveable { mutableStateOf(false) }

    // Yükleme ekranı göster (kategori verisi gelmeden önce)
    // Eğer categoryWithTags listesi boşsa ve hala yükleniyorsa, yükleme göster.
    // ViewModel'de bir isLoading state'i tutmak daha doğru bir yaklaşım olacaktır.
    if (category == null && categoryWithTags.isEmpty()) { // Hem category hem de categoryWithTags kontrolü eklendi
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

                val eventName = rememberSaveable { mutableStateOf(eventWithTag.event.name ?: "") }
                val nameError = rememberSaveable { mutableStateOf(false) }

                val eventDetail = rememberSaveable { mutableStateOf(eventWithTag.event.detail ?: "") }
                val detailError = rememberSaveable { mutableStateOf(false) }

                val selectedDate = rememberSaveable { mutableStateOf<Long?>(eventWithTag.event.date) }
                val dateError = rememberSaveable { mutableStateOf(false) }

                val showModal = rememberSaveable { mutableStateOf(false) }

                val eventDuration = rememberSaveable { mutableStateOf(eventWithTag.event.duration ?: "") }
                val durationError = rememberSaveable { mutableStateOf(false) }

                val eventLocation = rememberSaveable { mutableStateOf(eventWithTag.event.location ?: "") }
                val locationError = rememberSaveable { mutableStateOf(false) }

                val categoryError = rememberSaveable { mutableStateOf(false) }

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
                            if (nameError.value) {
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
                            if (detailError.value) {
                                Text("Bu alanı boş bırakamazsınız")
                            }
                        },
                        isSingleLine = false
                    )

                    //Event Date
                    Spacer(Modifier.padding(vertical = 8.dp))
                    ShowDateModal(Modifier, selectedDate, showModal, dateError, context)

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
                            if (durationError.value) {
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
                            if (locationError.value) {
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
                                if (categoryError.value) {
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
                                    text = { Text("${it.category.name}") },
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
                                    onClick = {
                                        chosenTags.removeAll { it.id == tag.id }
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