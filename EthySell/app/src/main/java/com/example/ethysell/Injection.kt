package com.example.ethysell

import android.content.Context
import com.example.ethysell.db.ProductDatabase
import com.example.ethysell.repository.ProductRepository

object Injection {

    private fun provideDatabase(context: Context): ProductDatabase {
        return ProductDatabase.getInstance(context)
    }

    fun provideProductRepository(context: Context): ProductRepository {
        return ProductRepository(provideDatabase(context))
    }
}