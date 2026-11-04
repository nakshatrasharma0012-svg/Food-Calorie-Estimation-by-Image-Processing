package com.example.food.data.model

data class FoodAnalysisResult(
    val foodName: String = "Food Item",
    val calories: Int = 0,
    val carbs: String = "0.0 g",
    val protein: String = "0.0 g",
    val fat: String = "0.0 g",
    val fiber: String = "0.0 g",
    val sugar: String = "0.0 g",
    val vitamins: String = "0.0 mg",
    val quantity: String = "1 Portion (~150g)",
    val healthInsights: List<String> = emptyList()
)
