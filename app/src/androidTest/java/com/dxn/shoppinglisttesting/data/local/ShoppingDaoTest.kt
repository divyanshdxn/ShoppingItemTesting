package com.dxn.shoppinglisttesting.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.dxn.shoppinglisttesting.utils.MainCoroutineRule
import com.dxn.shoppinglisttesting.utils.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)

@RunWith(AndroidJUnit4::class)
@SmallTest
class ShoppingDaoTest {

    // Run tasks synchronously
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    // Sets the main coroutines dispatcher to a TestCoroutineScope for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: ShoppingItemDatabase
    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShoppingItemDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.shoppingDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertShoppingItem_populates_database() = runTest {
        val item = ShoppingItem("aname", 45, 3f, "url", 1)
        dao.insert(item)
        val allShoppingItems = dao.getAllItems().getOrAwaitValue {
            // After observing, advance the clock to avoid the delay calls.
            advanceUntilIdle()
        }
        assertThat(allShoppingItems).contains(item)
    }

    @Test
    fun deleteShoppingItem_removes_item() = runTest {
        val item = ShoppingItem("aname", 45, 3f, "url", 1)
        dao.insert(item)
        val allShoppingItems = dao.getAllItems().getOrAwaitValue {
            // After observing, advance the clock to avoid the delay calls.
            advanceUntilIdle()
        }
        assertThat(allShoppingItems).contains(item)

        dao.deleteShoppingItem(item)
        val allShoppingItemsAfterDelete = dao.getAllItems().getOrAwaitValue {
            // After observing, advance the clock to avoid the delay calls.
            advanceUntilIdle()
        }
        assertThat(allShoppingItemsAfterDelete).doesNotContain(item)
    }

    @Test
    fun observeTotalPriceSum() = runTest {
        val item1 = ShoppingItem("aname", 2, 10f, "url", 1)
        val item2 = ShoppingItem("aname", 4, 5.5f, "url", 2)
        val item3 = ShoppingItem("aname", 0, 100f, "url", 3)
        dao.insert(item1)
        dao.insert(item2)
        dao.insert(item3)

        val totalPrice = dao.getTotalPrice().getOrAwaitValue {
            // After observing, advance the clock to avoid the delay calls.
            advanceUntilIdle()
        }

        assertThat(totalPrice).isEqualTo(2 * 10f + 4 * 5.5f + 0 * 100f)
    }

}