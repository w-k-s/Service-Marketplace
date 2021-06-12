package com.wks.servicesmarketplace.orderservice.core

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.LongNode
import com.fasterxml.jackson.databind.node.NumericNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.javamoney.moneta.FastMoney
import org.javamoney.moneta.function.MonetaryQueries
import java.math.BigDecimal
import javax.money.Monetary
import javax.money.MonetaryAmount

class MonetaryAmountSerializer @JvmOverloads constructor(t: Class<MonetaryAmount?>? = null) : StdSerializer<MonetaryAmount?>(t) {
    override fun serialize(value: MonetaryAmount?, jgen: JsonGenerator, provider: SerializerProvider?) {
        value?.let {
            jgen.writeStartObject()
            jgen.writeStringField("currency", value.currency.currencyCode)
            jgen.writeNumberField("amountMinorUnits", value.query(MonetaryQueries.convertMinorPart()))
            jgen.writeNumberField("denomination", BigDecimal.TEN.pow(value.currency.defaultFractionDigits))
            jgen.writeEndObject()
        } ?: jgen.writeNull()
    }
}

class MonetaryAmountDeserializer @JvmOverloads constructor(vc: Class<MonetaryAmount>? = null) : StdDeserializer<MonetaryAmount?>(vc) {
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): MonetaryAmount? {
        val node: JsonNode = jp.codec.readTree(jp)
        return when {
            node.isNull -> null
            else -> {
                val currency = Monetary.getCurrency(node.get("currency").asText())
                val amountMinorUnits = (node.get("amountMinorUnits") as NumericNode).numberValue().toLong()
                FastMoney.ofMinor(currency, amountMinorUnits)
            }
        }
    }
}