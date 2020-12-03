package com.wks.servicesmarketplace.orderservice.core.usecases

interface UseCase<I, O>{
    fun execute(request: I): O
}