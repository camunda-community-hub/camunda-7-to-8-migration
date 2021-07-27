'use strict';

function convertJuel(juelExpression) {
    var feelExpression = juelExpression;
    var hints = [];

    // Remove brackets but add leading "="
    feelExpression = feelExpression.replace(/#{(.*)}/g,"=$1");
    feelExpression = feelExpression.replace(/\${(.*)}/g,"=$1");

    // Replace "=="
    feelExpression = feelExpression.replace(/==/g, "=");

    // Replace "gt", "lt", "eq" and "ne"
    feelExpression = feelExpression.replace(/gt/g, ">");
    feelExpression = feelExpression.replace(/lt/g, "<");
    feelExpression = feelExpression.replace(/eq/g, "=");
    feelExpression = feelExpression.replace(/ne/g, "!=");

    // Replace || and &&
    feelExpression = feelExpression.replace(/&&/g, " and ");
    feelExpression = feelExpression.replace(/\|\|/g, " or ");

    // Finally replace double spaces introduced by above changes by single spaces
    feelExpression = feelExpression.replace(/  /g, " ");

    return {
        feelExpression: feelExpression,
        hints: hints
    };
}

console.log( convertJuel("#{x}") );
console.log( convertJuel("#{x>5}") );

module.exports = convertJuel;