package com.arturmaslov.skubaware.data.source.local

import androidx.room.*
import com.arturmaslov.skubaware.data.models.Product

@Database(
    entities = [
        Product::class,
    ], version = 3
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    // The associated DAOs for the database
    abstract val productDao: ProductDao?
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getProducts(): List<Product?>?

    // returns row id of inserted item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product): Long

    // returns number of rows affected
    @Query("DELETE FROM product")
    fun deleteProducts(): Int

}