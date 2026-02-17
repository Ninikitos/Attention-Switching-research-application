package com.nilin.lettermatrix

data class RoundData(
    val session_id: String,
    val current_round: Int,
    val total_rounds: Int,
    val is_active: Boolean,
    val mobile_matrix: List<String>,
    val target_letters: List<String>,
    val is_completed: Boolean
)