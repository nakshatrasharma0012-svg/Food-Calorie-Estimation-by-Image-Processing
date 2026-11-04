package com.example.food

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.food.ui.components.BottomNavBar
import com.example.food.ui.components.NavScreen
import com.example.food.ui.screens.*
import com.example.food.ui.theme.FoodTheme
import com.example.food.ui.viewmodel.AnalyzingState
import com.example.food.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FoodTheme {
                MainAppScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(NavScreen.HOME) }
    var inCameraMode by remember { mutableStateOf(false) }

    var lastCapturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Collect States
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val analyzingState by viewModel.analyzingState.collectAsState()
    val selectedRecord by viewModel.selectedRecord.collectAsState()
    val allRecords by viewModel.allRecords.collectAsState()
    val favoriteRecords by viewModel.favoriteRecords.collectAsState()
    val todayCalories by viewModel.todayCalories.collectAsState()
    val totalScansCount by viewModel.totalScansCount.collectAsState()
    val totalSystemCalories by viewModel.totalSystemCalories.collectAsState()

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                lastCapturedBitmap = bitmap
                viewModel.analyzeBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            inCameraMode = true
        } else {
            Toast.makeText(context, "Camera permission required to scan food", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to check & request camera permission
    fun openCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            inCameraMode = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // 1. Splash Screen
    if (showSplash) {
        SplashScreen(
            onStartClick = { showSplash = false }
        )
        return
    }

    // 2. Authentication Screen (Registration and Login Page)
    if (currentUser == null) {
        AuthScreen(
            onLoginClick = { email, pass, callback ->
                viewModel.login(email, pass, callback)
            },
            onRegisterClick = { name, email, pass, confirmPass, role, callback ->
                viewModel.register(name, email, pass, confirmPass, role, callback)
            }
        )
        return
    }

    // 3. Camera Mode
    if (inCameraMode) {
        CameraScreen(
            onBackClick = { inCameraMode = false },
            onGalleryClick = {
                inCameraMode = false
                galleryLauncher.launch("image/*")
            },
            onImageCaptured = { bitmap ->
                inCameraMode = false
                lastCapturedBitmap = bitmap
                viewModel.analyzeBitmap(bitmap)
            }
        )
        return
    }

    // 4. Analyzing / Loading Overlay State
    if (analyzingState is AnalyzingState.Loading) {
        AnalyzingOverlay(capturedBitmap = lastCapturedBitmap)
        return
    }

    // 5. Analysis Result Screen
    if (analyzingState is AnalyzingState.Success || selectedRecord != null) {
        val recordToShow = (analyzingState as? AnalyzingState.Success)?.record ?: selectedRecord
        recordToShow?.let { record ->
            ResultScreen(
                record = record,
                onBackClick = {
                    viewModel.resetAnalyzingState()
                    viewModel.setSelectedRecord(null)
                },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onScanAnotherClick = {
                    viewModel.resetAnalyzingState()
                    viewModel.setSelectedRecord(null)
                    openCamera()
                }
            )
            return
        }
    }

    // Error alert
    if (analyzingState is AnalyzingState.Error) {
        val errMsg = (analyzingState as AnalyzingState.Error).message
        LaunchedEffect(errMsg) {
            Toast.makeText(context, errMsg, Toast.LENGTH_LONG).show()
            viewModel.resetAnalyzingState()
        }
    }

    // 6. Main App Navigation Container with BottomNavBar
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentTab.route,
                isAdmin = (currentUser?.role == "ADMIN"),
                onNavigate = { screen -> currentTab = screen }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                NavScreen.HOME -> HomeScreen(
                    currentUser = currentUser,
                    todayCalories = todayCalories,
                    dailyGoal = viewModel.prefs.dailyCalorieGoal,
                    recentScans = allRecords.take(5),
                    onTakePhotoClick = { openCamera() },
                    onChooseGalleryClick = { galleryLauncher.launch("image/*") },
                    onRecordClick = { record -> viewModel.setSelectedRecord(record) },
                    onOpenAdminDashboard = { currentTab = NavScreen.ADMIN }
                )
                NavScreen.HISTORY -> HistoryScreen(
                    records = allRecords,
                    onRecordClick = { record -> viewModel.setSelectedRecord(record) },
                    onDeleteClick = { record -> viewModel.deleteRecord(record) }
                )
                NavScreen.FAVORITES -> FavoritesScreen(
                    favorites = favoriteRecords,
                    onRecordClick = { record -> viewModel.setSelectedRecord(record) },
                    onRemoveFavorite = { record -> viewModel.toggleFavorite(record) }
                )
                NavScreen.ADMIN -> AdminDashboardScreen(
                    currentUser = currentUser,
                    users = allUsers,
                    foodRecords = allRecords,
                    totalScans = totalScansCount,
                    totalCalories = totalSystemCalories,
                    onToggleRole = { user, callback -> viewModel.toggleUserRole(user, callback) },
                    onDeleteUser = { user, callback -> viewModel.deleteUser(user, callback) },
                    onAddUser = { name, email, pass, role, callback -> viewModel.addUserByAdmin(name, email, pass, role, callback) },
                    onDeleteRecord = { record -> viewModel.deleteRecord(record) }
                )
                NavScreen.PROFILE -> SettingsScreen(
                    currentUser = currentUser,
                    prefs = viewModel.prefs,
                    onLogoutClick = {
                        viewModel.logout()
                        currentTab = NavScreen.HOME
                    }
                )
            }
        }
    }
}
