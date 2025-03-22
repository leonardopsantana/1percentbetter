

package com.onepercentbetter.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.onepercentbetter.core.database.OpbDatabase
import org.junit.After
import org.junit.Before

internal abstract class DatabaseTest {

    private lateinit var db: OpbDatabase
    protected lateinit var newsResourceDao: com.onepercentbetter.core.database.dao.NewsResourceDao
    protected lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        db = run {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Room.inMemoryDatabaseBuilder(
                context,
                OpbDatabase::class.java,
            ).build()
        }
        newsResourceDao = db.newsResourceDao()
        categoryDao = db.categoryDao()
    }

    @After
    fun teardown() = db.close()
}
