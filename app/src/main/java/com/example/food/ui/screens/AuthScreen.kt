package com.example.food.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.food.ui.theme.PinkGradientEnd
import com.example.food.ui.theme.PinkGradientStart
import com.example.food.ui.theme.PurpleGradientEnd

@Composable
fun AuthScreen(
    onLoginClick: (String, String, (Boolean, String) -> Unit) -> Unit,
    onRegisterClick: (String, String, String, String, String, (Boolean, String) -> Unit) -> Unit
) {
    var isLoginTab by remember { mutableStateOf(true) }

    // Form inputs
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerConfirmPassword by remember { mutableStateOf("") }
    var registerPasswordVisible by remember { mutableStateOf(false) }
    var registerRole by remember { mutableStateOf("USER") } // "USER" or "ADMIN"

    // Valid Message state (Banner / Alert)
    var isErrorMsg by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Validation state
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateEmail(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) {
            emailError = "Email is required"
            return false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            emailError = "Enter a valid email (e.g. user@example.com)"
            return false
        }
        emailError = null
        return true
    }

    fun validatePassword(pass: String): Boolean {
        if (pass.isEmpty()) {
            passwordError = "Password is required"
            return false
        } else if (pass.length < 6) {
            passwordError = "Password must be at least 6 characters"
            return false
        }
        passwordError = null
        return true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF8FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Header Banner with Gradient & App Brand
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PinkGradientStart, PinkGradientEnd, PurpleGradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "FoodCalorie AI",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isLoginTab) "Welcome Back! Log in to continue" else "Create account to track smart nutrition",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Card Form Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Tab Toggle (Sign In / Sign Up)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1EEF2))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isLoginTab) Color.White else Color.Transparent)
                                .clickable {
                                    isLoginTab = true
                                    statusMessage = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign In",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLoginTab) PinkGradientEnd else Color.Gray
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isLoginTab) Color.White else Color.Transparent)
                                .clickable {
                                    isLoginTab = false
                                    statusMessage = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign Up",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isLoginTab) PinkGradientEnd else Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Status Message Banner (Valid feedback)
                    statusMessage?.let { msg ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isErrorMsg) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isErrorMsg) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isErrorMsg) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = msg,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isErrorMsg) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                                )
                            }
                        }
                    }

                    if (isLoginTab) {
                        // LOGIN FORM
                        Text(
                            text = "Email Address",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = {
                                loginEmail = it
                                validateEmail(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("admin@food.com or user@food.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            isError = emailError != null,
                            supportingText = emailError?.let { { Text(it, color = Color.Red) } },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Password",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = {
                                loginPassword = it
                                validatePassword(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = passwordError != null,
                            supportingText = passwordError?.let { { Text(it, color = Color.Red) } },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Quick Demo Autofill Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2F7))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "⚡ Quick Demo Login Accounts:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PinkGradientEnd
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Admin fill
                                    Button(
                                        onClick = {
                                            loginEmail = "admin@food.com"
                                            loginPassword = "admin123"
                                            emailError = null
                                            passwordError = null
                                            statusMessage = "Filled Demo Admin credentials (admin@food.com / admin123)"
                                            isErrorMsg = false
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                                    ) {
                                        Text("Admin Demo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // User fill
                                    Button(
                                        onClick = {
                                            loginEmail = "user@food.com"
                                            loginPassword = "user123"
                                            emailError = null
                                            passwordError = null
                                            statusMessage = "Filled Demo User credentials (user@food.com / user123)"
                                            isErrorMsg = false
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                                    ) {
                                        Text("User Demo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // LOGIN SUBMIT BUTTON
                        Button(
                            onClick = {
                                val validEm = validateEmail(loginEmail)
                                val validPass = validatePassword(loginPassword)
                                if (validEm && validPass) {
                                    onLoginClick(loginEmail, loginPassword) { success, msg ->
                                        isErrorMsg = !success
                                        statusMessage = msg
                                    }
                                } else {
                                    isErrorMsg = true
                                    statusMessage = "Please resolve the validation errors above"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkGradientEnd)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                    } else {
                        // REGISTRATION FORM
                        Text(
                            text = "Full Name",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = registerName,
                            onValueChange = { registerName = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g. Sarah Jenkins") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Email Address",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = registerEmail,
                            onValueChange = {
                                registerEmail = it
                                validateEmail(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("sarah@example.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            isError = emailError != null,
                            supportingText = emailError?.let { { Text(it, color = Color.Red) } },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Password",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = registerPassword,
                            onValueChange = {
                                registerPassword = it
                                validatePassword(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Min. 6 characters") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { registerPasswordVisible = !registerPasswordVisible }) {
                                    Icon(
                                        imageVector = if (registerPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = passwordError != null,
                            supportingText = passwordError?.let { { Text(it, color = Color.Red) } },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Confirm Password",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = registerConfirmPassword,
                            onValueChange = { registerConfirmPassword = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Re-enter password") },
                            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Role Selector
                        Text(
                            text = "Account Type / Role",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // User Chip
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (registerRole == "USER") PinkGradientEnd.copy(alpha = 0.15f) else Color(0xFFF5F5F5))
                                    .border(
                                        width = if (registerRole == "USER") 2.dp else 1.dp,
                                        color = if (registerRole == "USER") PinkGradientEnd else Color.LightGray,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { registerRole = "USER" },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (registerRole == "USER") PinkGradientEnd else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "User",
                                        fontWeight = FontWeight.Bold,
                                        color = if (registerRole == "USER") PinkGradientEnd else Color.Gray
                                    )
                                }
                            }

                            // Admin Chip
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (registerRole == "ADMIN") PurpleGradientEnd.copy(alpha = 0.15f) else Color(0xFFF5F5F5))
                                    .border(
                                        width = if (registerRole == "ADMIN") 2.dp else 1.dp,
                                        color = if (registerRole == "ADMIN") PurpleGradientEnd else Color.LightGray,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { registerRole = "ADMIN" },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = if (registerRole == "ADMIN") PurpleGradientEnd else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Admin",
                                        fontWeight = FontWeight.Bold,
                                        color = if (registerRole == "ADMIN") PurpleGradientEnd else Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // REGISTER SUBMIT BUTTON
                        Button(
                            onClick = {
                                val validEm = validateEmail(registerEmail)
                                val validPass = validatePassword(registerPassword)

                                if (validEm && validPass) {
                                    onRegisterClick(
                                        registerName,
                                        registerEmail,
                                        registerPassword,
                                        registerConfirmPassword,
                                        registerRole
                                    ) { success, msg ->
                                        isErrorMsg = !success
                                        statusMessage = msg
                                    }
                                } else {
                                    isErrorMsg = true
                                    statusMessage = "Please resolve the form validation errors"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkGradientEnd)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HowToReg, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Register Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer info message
            Text(
                text = "FoodCalorie AI • Secure & Private Nutrition System",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
