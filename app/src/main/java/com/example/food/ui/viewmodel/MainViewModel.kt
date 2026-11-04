package com.example.food.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.food.data.local.AppDatabase
import com.example.food.data.local.FoodRecord
import com.example.food.data.local.User
import com.example.food.data.remote.FoodAnalyzerService
import com.example.food.data.repository.PreferencesRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

sealed interface AnalyzingState {
    object Idle : AnalyzingState
    object Loading : AnalyzingState
    data class Success(val record: FoodRecord) : AnalyzingState
    data class Error(val message: String) : AnalyzingState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.foodRecordDao()
    private val userDao = db.userDao()
    val prefs = PreferencesRepository(application)
    private val analyzerService = FoodAnalyzerService(prefs)
    private val gson = Gson()

    // Current Authenticated User State
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // All registered users for Admin Dashboard
    val allUsers: StateFlow<List<User>> = userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userCount: StateFlow<Int> = userDao.getUserCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Analyzing / Scanner State
    private val _analyzingState = MutableStateFlow<AnalyzingState>(AnalyzingState.Idle)
    val analyzingState: StateFlow<AnalyzingState> = _analyzingState.asStateFlow()

    private val _selectedRecord = MutableStateFlow<FoodRecord?>(null)
    val selectedRecord: StateFlow<FoodRecord?> = _selectedRecord.asStateFlow()

    val allRecords: StateFlow<List<FoodRecord>> = dao.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRecords: StateFlow<List<FoodRecord>> = dao.getFavoriteRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _todayStartMs: Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todayCalories: StateFlow<Int> = dao.getTodayTotalCalories(_todayStartMs)
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalScansCount: StateFlow<Int> = allRecords
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalSystemCalories: StateFlow<Int> = allRecords
        .map { records -> records.sumOf { it.calories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        seedDefaultUsersAndCheckLogin()
    }

    private fun seedDefaultUsersAndCheckLogin() {
        viewModelScope.launch {
            // Seed default Admin & User if database has no users yet
            userDao.getAllUsers().first().let { existingUsers ->
                if (existingUsers.isEmpty()) {
                    val defaultAdmin = User(
                        name = "Admin Manager",
                        email = "admin@food.com",
                        password = "admin123",
                        role = "ADMIN"
                    )
                    val defaultUser = User(
                        name = "John Doe",
                        email = "user@food.com",
                        password = "user123",
                        role = "USER"
                    )
                    userDao.insertUser(defaultAdmin)
                    userDao.insertUser(defaultUser)
                }
            }

            // Restore logged-in user if saved
            if (prefs.isLoggedIn && prefs.loggedInUserEmail.isNotBlank()) {
                val user = userDao.getUserByEmail(prefs.loggedInUserEmail)
                if (user != null) {
                    _currentUser.value = user
                } else {
                    prefs.logout()
                }
            }
        }
    }

    // AUTHENTICATION FUNCTIONS
    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cleanEmail = email.trim().lowercase()
            val cleanPass = pass.trim()

            if (cleanEmail.isEmpty() || cleanPass.isEmpty()) {
                onResult(false, "Please fill in both email and password")
                return@launch
            }

            val user = userDao.getUserByEmail(cleanEmail)
            if (user == null) {
                onResult(false, "No account found with this email. Please register!")
                return@launch
            }

            if (user.password != cleanPass) {
                onResult(false, "Incorrect password. Please try again.")
                return@launch
            }

            _currentUser.value = user
            prefs.isLoggedIn = true
            prefs.loggedInUserEmail = user.email

            val roleLabel = if (user.role == "ADMIN") "Admin Dashboard Access" else "User Mode"
            onResult(true, "Login Successful! Welcome back, ${user.name} ($roleLabel)")
        }
    }

    fun register(
        name: String,
        email: String,
        pass: String,
        confirmPass: String,
        role: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanName = name.trim()
            val cleanEmail = email.trim().lowercase()
            val cleanPass = pass.trim()
            val cleanConfirm = confirmPass.trim()

            if (cleanName.isEmpty()) {
                onResult(false, "Please enter your full name")
                return@launch
            }
            if (cleanEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                onResult(false, "Please enter a valid email address (e.g. name@example.com)")
                return@launch
            }
            if (cleanPass.length < 6) {
                onResult(false, "Password must be at least 6 characters long")
                return@launch
            }
            if (cleanPass != cleanConfirm) {
                onResult(false, "Passwords do not match. Please re-enter passwords.")
                return@launch
            }

            val existing = userDao.getUserByEmail(cleanEmail)
            if (existing != null) {
                onResult(false, "Account with this email already exists! Please log in instead.")
                return@launch
            }

            val newUser = User(
                name = cleanName,
                email = cleanEmail,
                password = cleanPass,
                role = if (role.uppercase() == "ADMIN") "ADMIN" else "USER"
            )

            val insertedId = userDao.insertUser(newUser)
            val createdUser = newUser.copy(id = insertedId)

            _currentUser.value = createdUser
            prefs.isLoggedIn = true
            prefs.loggedInUserEmail = createdUser.email

            onResult(true, "Registration Successful! Account created for ${createdUser.name}")
        }
    }

    fun logout() {
        prefs.logout()
        _currentUser.value = null
    }

    // ADMIN MANAGEMENT FUNCTIONS
    fun toggleUserRole(user: User, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val newRole = if (user.role == "ADMIN") "USER" else "ADMIN"
            userDao.updateUserRole(user.id, newRole)
            if (_currentUser.value?.id == user.id) {
                _currentUser.value = _currentUser.value?.copy(role = newRole)
            }
            onResult("Role updated to $newRole for user ${user.name}")
        }
    }

    fun deleteUser(user: User, onResult: (String) -> Unit) {
        viewModelScope.launch {
            if (user.id == _currentUser.value?.id) {
                onResult("Cannot delete your own active Admin account!")
                return@launch
            }
            userDao.deleteUser(user)
            onResult("User '${user.name}' has been deleted successfully")
        }
    }

    fun addUserByAdmin(
        name: String,
        email: String,
        pass: String,
        role: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanName = name.trim()
            val cleanEmail = email.trim().lowercase()
            val cleanPass = pass.trim()

            if (cleanName.isEmpty()) {
                onResult(false, "Name is required")
                return@launch
            }
            if (cleanEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                onResult(false, "Valid email address is required")
                return@launch
            }
            if (cleanPass.length < 6) {
                onResult(false, "Password must be at least 6 characters")
                return@launch
            }

            val existing = userDao.getUserByEmail(cleanEmail)
            if (existing != null) {
                onResult(false, "A user with email $cleanEmail already exists!")
                return@launch
            }

            val newUser = User(
                name = cleanName,
                email = cleanEmail,
                password = cleanPass,
                role = role
            )
            userDao.insertUser(newUser)
            onResult(true, "User '$cleanName' ($role) added successfully!")
        }
    }

    // FOOD RECORD ANALYSIS
    fun analyzeBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _analyzingState.value = AnalyzingState.Loading
            try {
                val imagePath = saveBitmapToInternalStorage(bitmap)
                val result = analyzerService.analyzeFood(bitmap)

                val newRecord = FoodRecord(
                    foodName = result.foodName,
                    calories = result.calories,
                    carbs = result.carbs,
                    protein = result.protein,
                    fat = result.fat,
                    fiber = result.fiber,
                    sugar = result.sugar,
                    vitamins = result.vitamins,
                    quantity = result.quantity,
                    healthInsightsJson = gson.toJson(result.healthInsights),
                    imagePath = imagePath,
                    timestamp = System.currentTimeMillis(),
                    isFavorite = false
                )

                val insertedId = dao.insertRecord(newRecord)
                val savedRecord = newRecord.copy(id = insertedId)

                _selectedRecord.value = savedRecord
                _analyzingState.value = AnalyzingState.Success(savedRecord)
            } catch (e: Exception) {
                e.printStackTrace()
                _analyzingState.value = AnalyzingState.Error(e.message ?: "Failed to analyze food")
            }
        }
    }

    fun setSelectedRecord(record: FoodRecord?) {
        _selectedRecord.value = record
    }

    fun toggleFavorite(record: FoodRecord) {
        viewModelScope.launch {
            val updated = record.copy(isFavorite = !record.isFavorite)
            dao.updateRecord(updated)
            if (_selectedRecord.value?.id == record.id) {
                _selectedRecord.value = updated
            }
        }
    }

    fun deleteRecord(record: FoodRecord) {
        viewModelScope.launch {
            dao.deleteRecord(record)
            if (_selectedRecord.value?.id == record.id) {
                _selectedRecord.value = null
            }
            try {
                val file = File(record.imagePath)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetAnalyzingState() {
        _analyzingState.value = AnalyzingState.Idle
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): String {
        val context = getApplication<Application>()
        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        val file = File(imagesDir, "scan_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file.absolutePath
    }
}
