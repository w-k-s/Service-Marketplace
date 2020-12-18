package com.wks.servicesmarketplace.orderservice.core.repositories

import org.javamoney.moneta.FastMoney
import java.security.Principal
import java.util.*
import javax.money.MonetaryAmount
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class MonetaryAmountConverter : AttributeConverter<MonetaryAmount, String> {
    override fun convertToDatabaseColumn(attribute: MonetaryAmount?) = attribute?.toString()
    override fun convertToEntityAttribute(dbData: String?) = dbData?.let { FastMoney.parse(it) }
}

@Converter
class UUIDConverter : AttributeConverter<UUID, String> {
    override fun convertToDatabaseColumn(attribute: UUID?) = attribute?.toString()
    override fun convertToEntityAttribute(dbData: String?) = dbData?.let { UUID.fromString(it) }
}

@Converter
class PrincipalConverter : AttributeConverter<Principal, String> {
    override fun convertToDatabaseColumn(attribute: Principal?) = attribute?.name
    override fun convertToEntityAttribute(dbData: String?) = Principal { dbData }
}