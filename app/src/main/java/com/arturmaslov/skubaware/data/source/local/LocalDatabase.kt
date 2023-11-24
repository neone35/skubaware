package com.arturmaslov.skubaware.data.source.local

import androidx.room.*
import com.arturmaslov.skubaware.data.models.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
    ], version = 6
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    // The associated DAOs for the database
    abstract val productDao: ProductDao?
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM productEntity")
    fun getProducts(): List<ProductEntity>?

    // returns row id of inserted item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: ProductEntity): Long

    // returns number of rows affected
    @Query("DELETE FROM productEntity")
    fun deleteProducts(): Int

}