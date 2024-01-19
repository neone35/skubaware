package com.arturmaslov.skubaware.data.usecase

import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.source.MainRepository

class GetLocalProductsUseCase(
    private val mainRepo: MainRepository
) {
    suspend fun execute(): List<Product>? {
        return mainRepo.getLocalProducts()
    }
}