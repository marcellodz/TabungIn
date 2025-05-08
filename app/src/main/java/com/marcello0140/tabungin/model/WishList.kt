package com.marcello0140.tabungin.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "wish_list")
data class WishList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val targetAmount: Int,
    val currentAmount: Int = 0,
    val createdAt: String,
)
