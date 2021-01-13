package com.wks.servicemarketplace.serviceproviderservice.core.usecase


interface UseCase<I, O> {
    fun execute(input: I): O
}