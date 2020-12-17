package com.wks.servicesmarketplace.orderservice.core.repositories

import org.javamoney.moneta.FastMoney
import javax.money.MonetaryAmount
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class MonetaryAmountConverter : AttributeConverter<MonetaryAmount, String> {
    override fun convertToDatabaseColumn(attribute: MonetaryAmount?) = attribute?.let { attribute.toString() }

    override fun convertToEntityAttribute(dbData: String?): MonetaryAmount = FastMoney.parse(dbData)
}