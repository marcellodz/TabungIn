package com.marcello0140.tabungin.model

data class WishList(
    val id: Int,
    val name: String,
    val targetAmount: Int,
    val currentAmount: Int = 0
)

