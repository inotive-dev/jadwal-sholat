package com.display.sholat.data.db

import androidx.room.*
import com.display.sholat.data.entity.SlideShow

@Dao
sealed interface SlideShowDao {

    @Query("SELECT * FROM slideShow")
    suspend fun getAll(): List<SlideShow>

    @Query("SELECT * FROM slideShow where isShowInIqomah=0")
    suspend fun getAllWith(): List<SlideShow>

    @Query("SELECT * FROM slideShow where isShowInIqomah=1")
    suspend fun getWithIqomah(): SlideShow?

    @Query("DELETE FROM slideShow where id in (:list)")
    suspend fun deletes(list: Array<Int>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserts(list: List<SlideShow>)

    @Update
    suspend fun updates(list: Array<SlideShow>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(slideShow: SlideShow) : Long

    @Update
    suspend fun update(slideShow: SlideShow)

    @Delete
    suspend fun delete(slideShow: SlideShow)

}