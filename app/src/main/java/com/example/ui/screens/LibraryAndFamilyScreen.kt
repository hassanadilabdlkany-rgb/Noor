package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NawawiHadith
import com.example.data.model.ProphetStory
import com.example.data.repository.IslamicDataProvider
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.viewmodel.UiState

@Composable
fun LibraryAndFamilyScreen(
    uiState: UiState,
    onSetSubTab: (Int) -> Unit,
    onAnswerQuiz: (Int) -> Unit,
    onNextQuizQuestion: () -> Unit,
    onRestartQuiz: () -> Unit,
    onCalculateZakat: (Double, Double, Double, Double, Double) -> Unit,
    onUpdateKhatmah: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val subTabs = listOf(
        "الأربعون النووية",
        "قصص وسيرة",
        "مسابقة إسلامية",
        "حاسبة الزكاة",
        "خطة الختمة",
        "عن التطبيق"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("library_family_screen")
    ) {
        // Sub-tabs row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(subTabs.indices.toList()) { index ->
                val isSelected = uiState.librarySubTab == index
                FilterChip(
                    selected = isSelected,
                    onClick = { onSetSubTab(index) },
                    label = { Text(subTabs[index], fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        when (uiState.librarySubTab) {
            0 -> NawawiHadithSection(hadiths = IslamicDataProvider.nawawiHadiths)
            1 -> ProphetStoriesSection(stories = IslamicDataProvider.prophetStories)
            2 -> IslamicQuizSection(
                uiState = uiState,
                onAnswer = onAnswerQuiz,
                onNext = onNextQuizQuestion,
                onRestart = onRestartQuiz
            )
            3 -> ZakatCalculatorSection(
                zakatState = uiState.zakatState,
                onCalculate = onCalculateZakat
            )
            4 -> KhatmahPlanSection(
                khatmahPlan = uiState.khatmahPlan,
                onUpdate = onUpdateKhatmah
            )
            5 -> AboutAppSection()
        }
    }
}

@Composable
fun NawawiHadithSection(hadiths: List<NawawiHadith>) {
    var expandedHadithNumber by remember { mutableStateOf<Int?>(1) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "الأربعون النووية للإمام النووي - نصوص وشروح وفوائد",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(hadiths) { hadith ->
            val isExpanded = expandedHadithNumber == hadith.number
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedHadithNumber = if (isExpanded) null else hadith.number
                    }
                    .testTag("hadith_card_${hadith.number}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${hadith.number}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = hadith.titleArabic,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = hadith.hadithText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "📖 الشرح والإيضاح:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = hadith.explanation,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "💡 من الفوائد والدروس المستفادة:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            hadith.benefits.forEach { benefit ->
                                Text(
                                    text = "• $benefit",
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProphetStoriesSection(stories: List<ProphetStory>) {
    var expandedStoryId by remember { mutableStateOf<Int?>(1) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "قصص الأنبياء وسيرة خير الأنام ﷺ",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(stories) { story ->
            val isExpanded = expandedStoryId == story.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedStoryId = if (isExpanded) null else story.id },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = story.prophetName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = story.title,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = story.summary,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = story.fullStory,
                                fontSize = 13.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "🌟 الدروس والعبر:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            story.lessons.forEach { lesson ->
                                Text(
                                    text = "• $lesson",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IslamicQuizSection(
    uiState: UiState,
    onAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onRestart: () -> Unit
) {
    val questions = IslamicDataProvider.quizQuestions
    val currentQuestion = questions.getOrNull(uiState.currentQuizIndex)

    if (uiState.isQuizFinished || currentQuestion == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🎉 مبروك إتمام المسابقة!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Text("نتيجتك النهائية: ${uiState.quizScore} من ${questions.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onRestart,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("إعادة المسابقة من جديد", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "السؤال ${uiState.currentQuizIndex + 1} من ${questions.size}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "النقاط: ${uiState.quizScore}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = currentQuestion.question,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        currentQuestion.options.forEachIndexed { index, option ->
                            val isSelected = uiState.selectedQuizAnswer == index
                            val isAnswered = uiState.selectedQuizAnswer != null
                            val isCorrectOption = index == currentQuestion.correctIndex

                            val buttonColor = when {
                                !isAnswered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                isCorrectOption -> Color(0xFFDCFCE7)
                                isSelected -> Color(0xFFFEE2E2)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            }

                            Surface(
                                onClick = { if (!isAnswered) onAnswer(index) },
                                shape = RoundedCornerShape(12.dp),
                                color = buttonColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected || (isAnswered && isCorrectOption)) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isAnswered) {
                                        if (isCorrectOption) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "صحيح", tint = Color(0xFF15803D))
                                        } else if (isSelected) {
                                            Icon(Icons.Default.Cancel, contentDescription = "خاطئ", tint = Color(0xFFB91C1C))
                                        }
                                    }
                                }
                            }
                        }

                        if (uiState.selectedQuizAnswer != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "💡 معلومة: ${currentQuestion.explanation}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onNext,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (uiState.currentQuizIndex + 1 < questions.size) "السؤال التالي" else "إنهاء والاطلاع على النتيجة")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZakatCalculatorSection(
    zakatState: com.example.ui.viewmodel.ZakatState,
    onCalculate: (Double, Double, Double, Double, Double) -> Unit
) {
    var cashText by remember { mutableStateOf("0") }
    var goldGramsText by remember { mutableStateOf("0") }
    var silverGramsText by remember { mutableStateOf("0") }
    var businessText by remember { mutableStateOf("0") }
    var debtsText by remember { mutableStateOf("0") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "حاسبة الزكاة الشرعية الذكية",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "نصاب الذهب: 85 جرام عيار 24 • المقدار الواجب: ربع العشر (2.5%) بعد مرور حول هجري كامل.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = cashText,
                onValueChange = { cashText = it },
                label = { Text("الأموال النقدية والمدخرات البنكية ($/ريال)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = goldGramsText,
                onValueChange = { goldGramsText = it },
                label = { Text("وزن الذهب المعد للادخار والتجارة (بالجرام)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = silverGramsText,
                onValueChange = { silverGramsText = it },
                label = { Text("وزن الفضة (بالجرام)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = businessText,
                onValueChange = { businessText = it },
                label = { Text("قيمة عروض التجارة والبضائع المعدة للبيع") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = debtsText,
                onValueChange = { debtsText = it },
                label = { Text("الديون المستحقة عليك حالاً (تُخصم)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        item {
            Button(
                onClick = {
                    val c = cashText.toDoubleOrNull() ?: 0.0
                    val g = goldGramsText.toDoubleOrNull() ?: 0.0
                    val s = silverGramsText.toDoubleOrNull() ?: 0.0
                    val b = businessText.toDoubleOrNull() ?: 0.0
                    val d = debtsText.toDoubleOrNull() ?: 0.0
                    onCalculate(c, g, s, b, d)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("احسب الزكاة الواجبة الآن", fontWeight = FontWeight.Bold)
            }
        }

        if (zakatState.calculatedZakat > 0 || zakatState.cash > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (zakatState.isNisabReached) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (zakatState.isNisabReached) "✓ بلغ المال النصاب الشرعي وجبت الزكاة" else "لم يبلغ المال النصاب الشرعي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (zakatState.isNisabReached) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "مقدار الزكاة الواجب إخراجها: ${String.format("%.2f", zakatState.calculatedZakat)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (zakatState.isNisabReached) Color(0xFF15803D) else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KhatmahPlanSection(
    khatmahPlan: com.example.data.local.KhatmahPlanEntity,
    onUpdate: (Int, Int) -> Unit
) {
    var selectedJuz by remember { mutableStateOf(khatmahPlan.currentJuz) }
    var completedPages by remember { mutableStateOf(khatmahPlan.completedPages) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "مخطط ختم القرآن الكريم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "خطة 30 يوماً (جزء يومياً بمعدل 4 صفحات دبر كل صلاة مكتوبة)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val percent = (completedPages.toFloat() / 604f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { percent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تم إنجاز: $completedPages / 604 صفحة (${(percent * 100).toInt()}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("الجزء الحالي: الجزء $selectedJuz", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Slider(
                        value = selectedJuz.toFloat(),
                        onValueChange = {
                            selectedJuz = it.toInt()
                            completedPages = (selectedJuz * 20).coerceAtMost(604)
                            onUpdate(selectedJuz, completedPages)
                        },
                        valueRange = 1f..30f,
                        steps = 29
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (selectedJuz > 1) {
                                    selectedJuz -= 1
                                    completedPages = (selectedJuz * 20).coerceAtMost(604)
                                    onUpdate(selectedJuz, completedPages)
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("الجزء السابق")
                        }
                        Button(
                            onClick = {
                                if (selectedJuz < 30) {
                                    selectedJuz += 1
                                    completedPages = (selectedJuz * 20).coerceAtMost(604)
                                    onUpdate(selectedJuz, completedPages)
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("الجزء التالي")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AboutAppSection() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "🌙 تطبيق نور الإسلامي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "رفيقك الإيماني الشامل في رحلتك اليومية مع القرآن والسنة",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "نور الإسلامي ليس مجرد تطبيق، بل منظومة إسلامية متكاملة صُممت لتكون رفيقك في كل لحظة من يومك؛ من صلاتك الأولى فجرًا إلى أذكار نومك مساءً. يجمع التطبيق بين أصالة المحتوى الشرعي المعتمد وأحدث ما توصلت إليه التقنية، ليقدّم تجربة روحية سلسة، آمنة، وخالية من أي تشتيت.",
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "👨‍💻 معلومات المطور والتواصل",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• المطور: حسن عادل عبد الغني",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "• واتساب / هاتف: +967 737295689",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• البريد الإلكتروني: geygjh1290@gmail.com",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "«نور الإسلامي — نورٌ يهديك في دنياك، وزادٌ يرافقك إلى آخرتك»",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
