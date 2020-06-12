package com.wks.servicemarketplace.accountservice.core.usecase;

public interface UseCase<I, O> {
    O execute(I request) throws UseCaseException;
}
