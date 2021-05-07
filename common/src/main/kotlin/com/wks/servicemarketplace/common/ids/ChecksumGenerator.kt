package com.wks.servicemarketplace.common.ids

interface ChecksumGenerator{
    fun generate(value: String): String
}
interface ChecksumValidator{
    fun validate(value: String): Boolean
}

class DefaultChecksumCalculator : ChecksumGenerator, ChecksumValidator{

    override fun generate(value: String): String {
        return value.replace(Regex("[^A-Za-z0-9]"),"")
            .map {
                if (it.isLetter()){
                    it.toByte().toInt()
                }else{
                    Integer.parseInt(it.toString())
                }
            }.mapIndexed { index, s ->
                s * (index +1)
            }.fold(0){acc, i -> acc + i}
            .let { it % 10 }
            .toString()
    }

    override fun validate(value: String): Boolean {
        if(value.isEmpty()) return false

        val test = value.subSequence(0,value.lastIndex).toString()
        val checkDigit = value[value.lastIndex].toString()

        return generate(test) == checkDigit
    }
}