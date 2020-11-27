package com.wks.servicemarketplace.serviceproviderservice.core.usecase

import com.wks.servicemarketplace.serviceproviderservice.core.exceptions.CoreRuntimeException

interface UseCase<I, O> {
    @Throws(CoreRuntimeException::class)
    fun execute(input: I): O
}