package com.example.eventtrackerapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eventtrackerapp.R
import com.example.eventtrackerapp.model.OnBoardingModel
import kotlinx.coroutines.launch

@Composable
fun OnBoardingSceen(navController: NavHostController){

    val pages = listOf(
        OnBoardingModel(
            title = "İlk Sayfa",
            description = "ilk sayfadaki detaylar buraya yazılacak",
            ImageRes = R.drawable.ic_launcher_background
        ),
        OnBoardingModel(
            title = "ikinci sayfa",
            description = "İkinci sayfadaki detaylar buraya yazılacak",
            ImageRes = R.drawable.ic_launcher_background
        ),
        OnBoardingModel(
            title = "Üçüncü Sayfa",
            description = "üçüncü sayfadaki detaylar buraya yazılacak",
            ImageRes = R.drawable.ic_launcher_background
        ),
        OnBoardingModel(
            title = "dördüncü sayfa",
            description = "Dördüncü sayfadaki detaylar buuraya yazılacak",
            ImageRes = R.drawable.ic_launcher_background
        )
    )

    val pagerState = rememberPagerState(pageCount = {pages.size})
    val corouitineScope = rememberCoroutineScope()


    Column(Modifier
        .fillMaxSize()
        .padding(16.dp)) {


        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            page -> OnBoardingItem(pages[page])
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            Text("skip", style = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            ),
                modifier = Modifier.clickable {
                    val skipPage = pagerState.pageCount - 1
                    corouitineScope.launch { pagerState.animateScrollToPage(skipPage) }
                    navController.navigate("sign_up")
                }
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                repeat(pages.size){
                    index ->
                    val isSelected = pagerState.currentPage == index
                    Box(modifier = Modifier
                        .padding(4.dp)
                        .width(if (isSelected) 18.dp else 8.dp)
                        .height(if(isSelected) 18.dp else 8 .dp)
                        .border(
                            width = 1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = if (isSelected) Color(0xFF3B6C64) else Color(0xFFFFFFFF),
                            shape = CircleShape
                        )
                    )
                }
            }

            Text("Next", style = TextStyle(
                color = Color(0xFF333333),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
                modifier = Modifier.clickable {
                    if (pagerState.currentPage < 3){
                        val nextPage = pagerState.currentPage + 1
                        corouitineScope.launch { pagerState.animateScrollToPage(nextPage) }
                    }
                    else{
                        navController.navigate("sign_up")
                    }
                }
            )
        }
    }
}



@Composable
fun OnBoardingItem(page: OnBoardingModel)
{
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.ImageRes),null, Modifier
                .height(350.dp)
                .width(350.dp)
                .padding(bottom = 20.dp)
        )
        Text(
            text = page.title, style = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = page.description,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                textAlign = TextAlign.Center
            )
        )
    }
}