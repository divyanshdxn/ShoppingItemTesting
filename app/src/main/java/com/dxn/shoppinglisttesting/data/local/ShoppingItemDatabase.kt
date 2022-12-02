package com.dxn.shoppinglisttesting.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ShoppingItem::class],
    version = 1
)
abstract class ShoppingItemDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao

    companion object {
        private var INSTANCE: ShoppingItemDatabase? = null

        fun getInstance(context: Context): ShoppingItemDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = Room.databaseBuilder(context, ShoppingItemDatabase::class.java, "shopping_item_database").build()
                return INSTANCE!!
            }
        }
    }

}