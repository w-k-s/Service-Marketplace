package com.wks.servicesmarketplace.jobservice.core.usecases

interface UseCase<I, O>{
    fun execute(request: I): O
}