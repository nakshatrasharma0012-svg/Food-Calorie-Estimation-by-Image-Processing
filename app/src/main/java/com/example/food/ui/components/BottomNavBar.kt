package com.example.food.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.food.ui.theme.PinkPrimary

enum class NavScreen(val route: String, val title: String, val icon: ImageVector) {
    HOME("home", "Home", Icons.Default.Home),
    HISTORY("history", "History", Icons.Default.History),
    FAVORITES("favorites", "Favorites", Icons.Default.Favorite),
    ADMIN("admin", "Admin", Icons.Default.AdminPanelSettings),
    PROFILE("profile", "Profile", Icons.Default.Person)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    isAdmin: Boolean = false,
    onNavigate: (NavScreen) -> Unit
) {
    val screensToDisplay = if (isAdmin) {
        NavScreen.entries.toTypedArray()
    } else {
        NavScreen.entries.filter { it != NavScreen.ADMIN }.toTypedArray()
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        screensToDisplay.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PinkPrimary,
                    selectedTextColor = PinkPrimary,
                    indicatorColor = PinkPrimary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
