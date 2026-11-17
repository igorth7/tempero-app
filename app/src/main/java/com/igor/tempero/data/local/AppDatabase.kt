package com.igor.tempero.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

/** Banco de dados do app com as 2 entidades. */
@Database(entities = [Receita::class, ItemCompra::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun receitaDao(): ReceitaDao
    abstract fun itemCompraDao(): ItemCompraDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton: garante que só exista UMA instância do banco no app inteiro
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tempero_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
