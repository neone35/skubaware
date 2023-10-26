package com.arturmaslov.skubaware.data.models

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity
data class Product(

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
