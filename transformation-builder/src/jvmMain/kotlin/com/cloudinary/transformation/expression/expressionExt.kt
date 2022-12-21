package com.cloudinary.transformation.expression

import com.cloudinary.transformation.expression.Expression.Companion.OPERATORS
import com.cloudinary.transformation.expression.Expression.Companion.PREDEFINED_VARS
import com.cloudinary.util.cldMergeToSingleUnderscore
import java.util.regex.Pattern

private val PATTERN = getPattern()
private var USER_VARIABLE_PATTERN: Pattern = Pattern.compile("\\\$_*[^_]+")

/**
 * Normalize an expression string, replace "nice names" with their coded values and spaces with "_".
 *
 * @param expression an expression
 * @return a parsed expression
 */
internal actual fun Any.cldNormalize(): String {
    val expression = this

    val conditionStr = expression.toString().cldMergeToSingleUnderscore()
    val matcher = USER_VARIABLE_PATTERN.matcher(conditionStr)
    val result = StringBuffer(conditionStr.length)

    // we first look for a user variable (starting with a $)
    var lastMatchEnd = 0
    while (matcher.find()) {
        val beforeMatch = conditionStr.substring(lastMatchEnd, matcher.start())
        result.append(normalizeBuiltins(beforeMatch))
        result.append(matcher.group())
        lastMatchEnd = matcher.end()
    }

    // we look to replace all "nice names" with their coded values.
    result.append(normalizeBuiltins(conditionStr.substring(lastMatchEnd)))
    return result.toString()
}

/**
 * Normalize an expression string, replace "nice names" with their coded values and spaces with "_".
 *
 * @param input an expression
 * @return a parsed expression
 */
private fun normalizeBuiltins(input: String): String {
    var replacement: String?
    val matcher = PATTERN.matcher(input)
    val result = StringBuffer(input.length)

    while (matcher.find()) {
        replacement = when {
            OPERATORS.containsKey(matcher.group()) -> OPERATORS[matcher.group()]
            PREDEFINED_VARS.containsKey(matcher.group()) -> PREDEFINED_VARS[matcher.group()]
            else -> matcher.group()
        }
        matcher.appendReplacement(result, replacement)
    }
    matcher.appendTail(result);
    return result.toString();
}

/**
 * @return a regex pattern for operators and predefined vars as /((operators)(?=[ _])|variables)/
 */
private fun getPattern(): Pattern {
    val pattern: String
    val operators = OPERATORS.keys.sortedDescending()
    val sb = StringBuilder("((")
    for (op in operators) {
        sb.append(Pattern.quote(op)).append("|")
    }
    sb.deleteCharAt(sb.length - 1)
    // The :${it} part is to prevent normalization of vars with a preceding colon (such as :duration),
    // It won't be found in PREDEFINED_VARS and so won't be normalized.
    sb.append(")(?=[ _])|")
        .append(PREDEFINED_VARS.keys.map { ":${it}|${it}" }.joinToString("|", transform = { "(?<!\\$)$it" }))
        .append(")")
    pattern = sb.toString()
    return Pattern.compile(pattern)
}
