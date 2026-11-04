package com.example.food.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_records")
data class FoodRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodName: String,
    val calories: Int,
    val carbs: String,
    val protein: String,
    val fat: String,
    val fiber: String,
    val sugar: String,
    val vitamins: String = "0.0 mg",
    val quantity: String = "1 Portion (~150g)",
    val healthInsightsJson: String, // Stored as JSON string list
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
