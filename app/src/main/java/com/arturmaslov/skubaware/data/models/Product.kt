package com.arturmaslov.skubaware.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Product(

	@field:PrimaryKey(autoGenerate = true)
	val id: Int? = null,

	@field:SerializedName("IMG_URL")
	val imgUrl: String? = null,

	@field:SerializedName("QUANTITY")
	val quantity: Int? = null,

	@field:SerializedName("SKN")
	val skn: String? = null,

	@field:SerializedName("BRAND")
	val brand: String? = null,

	@field:SerializedName("NAME")
	val name: String? = null,

	@field:SerializedName("BUYER_CODE")
	val buyerCode: Int? = null
)
