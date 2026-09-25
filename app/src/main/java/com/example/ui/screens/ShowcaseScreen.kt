package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Destination
import com.example.model.SampleDestinations
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.IkatPatternDivider
import com.example.ui.components.UzbekStarEmblem
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldLight
import com.example.ui.theme.TurquoiseTile

data class EditorialArticle(
    val id: String,
    val title: String,
    val subtitle: String,
    val author: String,
    val readTime: String,
    val imageRes: Int,
    val quote: String,
    val body: String,
    val destinationRef: Destination
)

@Composable
fun ShowcaseScreen(
    onNavigateToDestination: (Destination) -> Unit,
    onNavigateToAr: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val articles = listOf(
        EditorialArticle(
            id = "registan_golden_hour",
            title = "The Architecture of Infinity: Samarkand's Majolica Secrets",
            subtitle = "How Timurid master mathematicians mapped celestial orbits into ceramic azure tiles.",
            author = "Silk Road Heritage Society",
            readTime = "4 min read",
            imageRes = R.drawable.img_registan_1787819168326,
            quote = "\"If you wish to know our might, look upon our architecture.\" — Amir Timur, 1395 AD",
            body = "Registan Square stands as the zenith of Islamic architecture. The blue dome of Tilya-Kori mosque uses 24-karat gold leaf relief (Kundal technique) to simulate a starry cosmos under the sun.",
            destinationRef = SampleDestinations.items[0]
        ),
        EditorialArticle(
            id = "bukhara_desert_beacon",
            title = "The Tower Genghis Khan Spared: Kalyan Minaret",
            subtitle = "Standing undefeated since 1127 AD, guide beacon for desert Silk Road caravans.",
            author = "Dr. Farrukh Yusupov",
            readTime = "3 min read",
            imageRes = R.drawable.img_bukhara_1787819199064,
            quote = "When Genghis Khan entered Bukhara, he gazed up at Kalyan Minaret and ordered it preserved while all else was destroyed.",
            body = "Built by master architect Bako with alabaster mortar reinforced with camel milk and egg white, this 45.6-meter masterpiece has endured nine centuries of earthquakes.",
            destinationRef = SampleDestinations.items[1]
        ),
        EditorialArticle(
            id = "khiva_frozen_oasis",
            title = "Ichan Kala: The Living Open-Air Museum of the Karakum Desert",
            subtitle = "Walk the intact mudbrick labyrinth where 250 historical households still reside.",
            author = "UNESCO Central Asia Bureau",
            readTime = "5 min read",
            imageRes = R.drawable.img_khiva_1787819215477,
            quote = "\"Entering Khiva is like stepping through a portal straight into the 16th century Caravanserais.\"",
            body = "Surrounded by 10-meter-high rammed-earth walls with four fortified gates, Khiva was the final stop for desert caravans before crossing the harsh desert to Persia.",
            destinationRef = SampleDestinations.items[2]
        )
    )

    val infiniteTransition = rememberInfiniteTransition(label = "StarAura")
    val starScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StarPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UzbekStarEmblem(
                                size = 36.dp,
                                primaryColor = if (isDark) NeonGold else SilkGold,
                                secondaryColor = TurquoiseTile
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "SILK ROAD EDITORIAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.8.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isDark) NeonGold else SilkGold
                                )
                                Text(
                                    text = "Curated Chronicles",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        GlassPillBadge(
                            text = "Heritage Issue",
                            accentColor = NeonGold,
                            textColor = if (isDark) NeonGold else SilkGold
                        )
                    }
                }
            }

            // Featured Article Hero
            item {
                Spacer(modifier = Modifier.height(6.dp))

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(26.dp),
                    elevation = 8.dp
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            Image(
                                painter = painterResource(id = articles[0].imageRes),
                                contentDescription = articles[0].title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0x99000C1F), Color(0xF0020A18))
                                        )
                                    )
                            )

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(14.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NeonGold)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "COVER STORY",
                                    color = Color(0xFF1A1C1E),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "SPECIAL ARCHITECTURAL ESSAY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 2.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SilkGoldLight
                                    )
                                )
                                Text(
                                    text = articles[0].title,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "By ${articles[0].author}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = SilkGold
                                )
                                Text(
                                    text = articles[0].readTime,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Pull Quote Card
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0x330047AB) else Color(0xFFF0F6FF))
                                    .border(1.dp, SilkGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FormatQuote,
                                    contentDescription = null,
                                    tint = NeonGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = articles[0].quote,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = articles[0].body,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            GoldGradientButton(
                                text = "Experience Registan in AR",
                                icon = Icons.Filled.CameraAlt,
                                onClick = { onNavigateToAr(articles[0].destinationRef) },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "cover_story_ar_btn"
                            )
                        }
                    }
                }
            }

            // Article Feed
            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "MORE SILK ROAD ESSAYS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) NeonGold else RegistanBlue
                    ),
                    modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
                )
            }

            items(articles.drop(1)) { article ->
                ArticleCardItem(
                    article = article,
                    isDark = isDark,
                    onExplore = { onNavigateToDestination(article.destinationRef) }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun ArticleCardItem(
    article: EditorialArticle,
    isDark: Boolean,
    onExplore: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onExplore() }
            .testTag("article_card_${article.id}"),
        shape = RoundedCornerShape(22.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = article.imageRes),
                contentDescription = article.title,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = article.author.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = SilkGold
                        )
                    )
                    Text(
                        text = article.readTime,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                    color = if (isDark) Color(0xFFB0C0D4) else Color(0xFF5A6B80),
                    maxLines = 2
                )
            }
        }
    }
}
