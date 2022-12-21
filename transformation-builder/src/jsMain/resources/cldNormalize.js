/**
* Normalize a string expression
* @function Cloudinary#normalize
* @param {string} expression a expression, e.g. "w gt 100", "width_gt_100", "width > 100"
* @return {string} the normalized form of the value expression, e.g. "w_gt_100"
*/
function cldNormalize(expression) {
    if (expression == null) {
        return expression;
    }
    expression = String(expression);
    const operators = "\\|\\||>=|<=|&&|!=|>|=|<|/|-|\\+|\\*|\\^";

    // operators
    const operatorsPattern = "((" + operators + ")(?=[ _]))";
    const operatorsReplaceRE = new RegExp(operatorsPattern, "g");
    expression = expression.replace(operatorsReplaceRE, match => Expression.OPERATORS[match]);

    // predefined variables
    // The :${v} part is to prevent normalization of vars with a preceding colon (such as :duration),
    // It won't be found in PREDEFINED_VARS and so won't be normalized.
    // It is done like this because ie11 does not support regex lookbehind
    const predefinedVarsPattern = "(" + Object.keys(Expression.PREDEFINED_VARS).map(v=>`:${v}|${v}`).join("|") + ")";
    const userVariablePattern = '(\\$_*[^_ ]+)';

    const variablesReplaceRE = new RegExp(`${userVariablePattern}|${predefinedVarsPattern}`, "g");
    expression = expression.replace(variablesReplaceRE, (match) => (Expression.PREDEFINED_VARS[match] || match));

    return expression.replace(/[ _]+/g, '_');
}
