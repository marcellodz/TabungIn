package com.marcello0140.tabungin.data

import com.marcello0140.tabungin.model.TabunganHistory
import com.marcello0140.tabungin.model.WishList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WishListRepository {

    val dummyWishList = WishList(
        id = 1,
        name = "Beli iPhone 15",
        targetAmount = 15000000,
        currentAmount = 7000000,
        createdAt = "01 Mei 2025",
        history = listOf(
            TabunganHistory(tanggal = "02 Mei 2025", nominal = 2000000, isPenambahan = true),
            TabunganHistory(tanggal = "04 Mei 2025", nominal = 5000000, isPenambahan = true),
            TabunganHistory(tanggal = "06 Mei 2025", nominal = 1000000, isPenambahan = false) ,
            TabunganHistory(tanggal = "06 Mei 2025", nominal = 1000000, isPenambahan = false),
            TabunganHistory(tanggal = "06 Mei 2025", nominal = 1000000, isPenambahan = false),
            TabunganHistory(tanggal = "06 Mei 2025", nominal = 1000000, isPenambahan = false),
            TabunganHistory(tanggal = "06 Mei 2025", nominal = 1000000, isPenambahan = false)// contoh pengurangan
        )
    )



    fun getAllWishList(): Flow<List<WishList>> = flowOf(listOf(dummyWishList))



}
