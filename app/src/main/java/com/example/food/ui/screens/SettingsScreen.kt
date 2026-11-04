package com.example.food.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.food.data.local.User
import com.example.food.data.repository.PreferencesRepository
import com.example.food.ui.theme.PinkGradientEnd
import com.example.food.ui.theme.PurpleGradientEnd

@Composable
fun SettingsScreen(
    currentUser: User?,
    prefs: PreferencesRepository,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current

    var apiKey by remember { mutableStateOf(prefs.geminiApiKey) }
    var backendUrl by remember { mutableStateOf(prefs.customBackendUrl) }
    var apiMode by remember { mutableStateOf(prefs.apiMode) }
    var calorieGoalText by remember { mutableStateOf(prefs.dailyCalorieGoal.toString()) }

    Scaffold(
        containerColor = Color(0xFFFAF8FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Profile & Settings",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            // User Account Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    if (currentUser?.role == "ADMIN") PurpleGradientEnd.copy(alpha = 0.15f)
                                    else PinkGradientEnd.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.name?.take(1)?.uppercase() ?: "U",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = if (currentUser?.role == "ADMIN") PurpleGradientEnd else PinkGradientEnd
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.name ?: "Logged In User",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121)
                            )
                            Text(
                                text = currentUser?.email ?: "user@example.com",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }

                        // Role Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (currentUser?.role == "ADMIN") PurpleGradientEnd.copy(alpha = 0.15f)
                                    else Color(0xFFE8F5E9)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentUser?.role ?: "USER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUser?.role == "ADMIN") PurpleGradientEnd else Color(0xFF2E7D32)
                            )
                        }
                    }

                    Divider(color = Color(0xFFEEEEEE))

                    // Log Out Button
                    OutlinedButton(
                        onClick = {
                            onLogoutClick()
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Out of Account",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Mode Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "AI Backend Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (apiMode == "DIRECT_GEMINI"),
                            onClick = {
                                apiMode = "DIRECT_GEMINI"
                                prefs.apiMode = "DIRECT_GEMINI"
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = PinkGradientEnd)
                        )
                        Text(text = "Direct Gemini Vision API (Recommended)", fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (apiMode == "CUSTOM_BACKEND"),
                            onClick = {
                                apiMode = "CUSTOM_BACKEND"
                                prefs.apiMode = "CUSTOM_BACKEND"
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = PinkGradientEnd)
                        )
                        Text(text = "Custom Colab / Ngrok Backend", fontSize = 14.sp)
                    }
                }
            }

            // Gemini API Key Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = PinkGradientEnd,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini API Key",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                    }

                    Text(
                        text = "Get a free key from Google AI Studio (aistudio.google.com)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = {
                            apiKey = it
                            prefs.geminiApiKey = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Paste your Gemini API key here...") },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            // Custom Backend URL Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = PinkGradientEnd,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google Colab / Ngrok Backend URL",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                    }

                    Text(
                        text = "Enter your Colab API endpoint (e.g. https://xxxx.ngrok-free.app/analyze)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = backendUrl,
                        onValueChange = {
                            backendUrl = it
                            prefs.customBackendUrl = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://xxxx.ngrok-free.app/analyze") },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            // Calorie Goal Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFFF5722),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily Calorie Goal (kcal)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                    }

                    OutlinedTextField(
                        value = calorieGoalText,
                        onValueChange = {
                            calorieGoalText = it
                            it.toIntOrNull()?.let { goal -> prefs.dailyCalorieGoal = goal }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("2000") },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            // Save & Test Connection Button
            Button(
                onClick = {
                    Toast.makeText(context, "Settings Saved Successfully!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkGradientEnd)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Settings",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
