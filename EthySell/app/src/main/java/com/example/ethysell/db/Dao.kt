package com.example.ethysell.db

import androidx.lifecycle.LiveData
import androidx.room.*



@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<DataBaseData>)


    @Query("SELECT * FROM data_table")
    fun getAllProducts(): List<DataBaseData>


    @Query("SELECT * FROM data_table WHERE name LIKE :query OR ethicalScore LIKE :query OR type LIKE :query")
    suspend fun getSearchedProduct(query: String): List<DataBaseData>

    @Query("SELECT * FROM data_table WHERE id= :itemId")
    fun getProduct(itemId: Int):DataBaseData

    @Update
    suspend fun updateProductComment(product: DataBaseData)

    @Query("DELETE FROM data_table WHERE id= :itemId")
    suspend fun deleteItem(itemId: Int)

    @Query("DELETE FROM data_table")
    suspend fun clearAll()
}