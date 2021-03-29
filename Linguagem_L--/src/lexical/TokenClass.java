package lexical;

public enum TokenClass {
     unknown, main, id, typeInt, typeFloat, typeChar, typeBool, typeString, constInt, constFloat, constBool, constChar,             constString, optAdd, optPow, optMult, optDiv, optSub, optMod, optAttib, optLess, optLessEq, optGreat, optGreatEq, optEq,       optNotEq, optAnd, optOr, optNot, optConcat, condIf, condElsif, condElse, loopWhile, loopFor, read, write, separator,           paramInit, paramEnd, arrInit, arrEnd, comment, funcDef, funcReturn, funcInit, funcEnd, sentenEnd, endFile
}
