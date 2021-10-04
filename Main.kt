package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.math.pow

fun main() {
    /***
     *  List of base symbols
     */
    val utf = mutableListOf<Char>()
    for (value in '0'..'9') utf.add(value)
    for (value in 'a'..'z') utf.add(value)

    // First level, definition of bases
    while (true) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        val paramBase = readLine()!!.split(" ")

        // Parameter "exit" check, if true process finished, and check options is integer value
        if ("/exit" in paramBase) break
        else if (paramBase.size >= 3) println("Many options, please input two numbers (source base) (target base)")
        else if (paramBase[0].toIntOrNull() == null || paramBase[1].toIntOrNull() == null) {
            println("Incorrect value, please enter an integer value ")
            continue
        } else if (paramBase[0].toIntOrNull() == null && paramBase[1].toIntOrNull() == null) {
            println("Incorrect value, please enter an integer value ")
            continue
        }

        // Second level, convert source base to target base
        while (true) {
            var tempNumber: BigInteger
            var tempNumberDecimal: String
            val sourceBase = paramBase[0].toBigInteger()
            val targetBase = paramBase[1].toBigInteger()
            var sourceNumber: String

            print("Enter number in base ${paramBase[0]} to convert to base ${paramBase[1]} (To go back type /back) ")
            val paramNumber = readLine()!!.split(" ")

            // Parameter "back" check, if true return to first level
            if ("/back" in paramNumber) break
            else if (paramNumber.size >= 2) {
                println("More params, please input one number")
                continue
            }

            sourceNumber = paramNumber[0]


            if ('.' in sourceNumber) {

                val leftSide = sourceNumber.substringBefore('.')
                var rightSide = sourceNumber.substringAfter('.')
                var result: String

                if (targetBase == 10.toBigInteger()) {
                    val tmpLeftSide = toDecimal(leftSide, sourceBase, utf) ?: continue
                    tempNumberDecimal = toTargetBase(tmpLeftSide, targetBase, utf)
                    val tmpRightSide = doublePartToTargetBase(rightSide,targetBase, utf)
                    result = tempNumberDecimal + "." + tmpRightSide
                    println("Conversion result: $result")
                    continue
                }
                else {
                    val tmpLeftSide = toDecimal(leftSide, sourceBase, utf) ?: continue
                    tempNumberDecimal = toTargetBase(tmpLeftSide, targetBase, utf)

                    val tempRightSideToDecimal = toDecimalForDouble(rightSide, sourceBase, utf)
                    val tmpRightSide = doublePartToTargetBase(tempRightSideToDecimal.toString(),targetBase, utf)
                    result = "$tempNumberDecimal.${tmpRightSide}"
                    println("Conversion result: $result")
                    continue
                }
            }
            else {
                if (targetBase == 10.toBigInteger()) {

                    val resultDecimalNumber = toDecimal(sourceNumber, sourceBase, utf) ?: continue

                    println("Conversion result: $resultDecimalNumber")
                    continue
                }

                tempNumber = toDecimal(sourceNumber, sourceBase, utf)!!
                println("Conversion result: ${toTargetBase(tempNumber, targetBase, utf)}")
            }

        }
    }

}

// Convert number to decimal base
fun toDecimal(sourceNumber: String, sourceBase: BigInteger, utf: MutableList<Char>): BigInteger? {

    val range = utf.subList(0, sourceBase.toInt())
    var tempNumber = 0.toBigInteger()
    var counter = 1.toBigInteger()

    for (character in sourceNumber) {
        if (character !in range) {
            println("Number in base: $sourceBase, cannot contain character $character")
            return null
        }
    }

    // Convert char format to integer format (example: sourceNumber = a, in utf: 'a' == 10)
    for (character in sourceNumber.reversed()) {

        for (i in range.indices) {
            if (character == utf[i]) {
                tempNumber += i.toBigInteger() * counter
                counter *= sourceBase
            }
        }
    }

    return tempNumber
}

// Convert number to decimal base
fun toDecimalForDouble(sourceNumber: String, sourceBase: BigInteger, utf: MutableList<Char>): BigDecimal? {

    val range = utf.subList(0, sourceBase.toInt())
    var tempNumber = 0.toBigDecimal()
    var counter = 1

    for (character in sourceNumber) {
        if (character !in range) {
            println("Number in base: $sourceBase, cannot contain character $character")
            return null
        }
    }

    // Convert char format to integer format (example: sourceNumber = a, in utf: 'a' == 10)
    for (character in sourceNumber) {

        for (i in range.indices) {
            if (character == utf[i]) {
                val tempRemainder = sourceBase.toDouble().pow(-counter.toDouble())
                tempNumber += i.toBigDecimal() * tempRemainder.toBigDecimal()
                counter++
                //10000
            }
        }
    }

    return tempNumber.setScale(5, RoundingMode.HALF_DOWN)
}

// Convert to any base
fun toTargetBase(sourceNumber: BigInteger, targetBase: BigInteger, utf: MutableList<Char>): String {

    var tempNumber = sourceNumber
    var str = ""

    while (true) {

        if (tempNumber < targetBase) {
            if (tempNumber > 9.toBigInteger()) str += utf[tempNumber.toInt()] else str += tempNumber
            break
        }

        val remainder = tempNumber % targetBase
        tempNumber /= targetBase

        if (remainder > 9.toBigInteger()) str += utf[remainder.toInt()] else str += remainder

    }

    return str.reversed()
}

// Convert double part
fun doublePartToTargetBase(tempRightSide: String, targetBase: BigInteger, utf: MutableList<Char>): String {
    var tmp = ""
    var tmpStringDoublePart: String
    var tmpDoublePart = tempRightSide.toBigDecimal()

    while (true) {
        if (tmp.length > 4) break
        tmpDoublePart *= targetBase.toBigDecimal()
        val tmpIntPart = tmpDoublePart.toString().substringBefore('.')
        if (tmpIntPart.toInt() > 9) tmp += utf[tmpIntPart.toInt()] else tmp += tmpIntPart
        if (tmpDoublePart.toString().substringAfter('.') == "0") break
        tmpStringDoublePart = "0.${ tmpDoublePart.toString().substringAfter('.') }"
        tmpDoublePart = tmpStringDoublePart.toBigDecimal()
    }
    return tmp
}