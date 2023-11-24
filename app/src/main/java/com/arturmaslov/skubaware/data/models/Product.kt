package com.arturmaslov.skubaware.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ProductDto(

    @SerializedName("IMG_URL")
    val imgUrl: String? = null,

    @SerializedName("QUANTITY")
    val quantity: String? = null,

    @SerializedName("SKN")
    val skn: String? = null,

    @SerializedName("BRAND")
    val brand: String? = null,

    @SerializedName("NAME")
    val name: String? = null,

    @SerializedName("BUYER_CODE")
    val buyerCode: String? = null
)

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val imgUrl: String? = null,
    val quantity: String? = null,
    val skn: Int? = null,
    val brand: String? = null,
    val name: String? = null,
    val buyerCode: Int? = null
)

data class Product(
    val id: Int? = null,
    val imgUrl: String? = null,
    val quantity: String? = null,
    val skn: Int? = null,
    val brand: String? = null,
    val name: String? = null,
    val buyerCode: Int? = null
)

fun ProductDto.toDomainModel(): Product {
    val noLetterSkn = this.skn
        ?.replace(Regex("[A-Za-z]"), "")
        ?.toInt()

    return Product(
        id = null,
        imgUrl = imgUrl,
        quantity = quantity,
        skn = noLetterSkn,
        brand = brand,
        name = name,
        buyerCode = buyerCode?.toInt()
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        imgUrl = imgUrl,
        quantity = quantity,
        skn = skn,
        brand = brand,
        name = name,
        buyerCode = buyerCode
    )
}

fun ProductEntity.toDomainModel(): Product {
    return Product(
        id = id,
        imgUrl = imgUrl,
        quantity = quantity,
        skn = skn,
        brand = brand,
        name = name,
        buyerCode = buyerCode
    )
}