package com.wks.servicesmarketplace.jobservice.core.usecases

interface UseCase<I: UseCaseRequest, O: UseCaseResponse>{
    fun execute(request: I): O
}