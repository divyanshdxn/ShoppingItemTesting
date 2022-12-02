package com.dxn.shoppinglisttesting.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ShoppingDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(item: ShoppingItem)

    @Delete
    fun deleteShoppingItem(shoppingItem: ShoppingItem)

    @Query("SELECT * FROM shopping_item")
    fun getAllItems(): LiveData<List<ShoppingItem>>

    @Query("SELECT SUM(price*amount) FROM shopping_item")
    fun getTotalPrice(): LiveData<Float>

}