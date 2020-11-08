package com.wks.servicemarketplace.customerservice.core.usecase;

public interface UseCase<I, O> {
    O execute(I request) throws Exception;
}
