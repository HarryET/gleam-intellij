package run.gleam.lang.core.psi

import com.intellij.psi.tree.IElementType
import run.gleam.lang.GleamLanguage

open class GleamTokenType(debugName: String, value: Any? = null) : IElementType(debugName, GleamLanguage)
open class GleamTokenTypeJava(debugName: String): GleamTokenType(debugName)

open class GleamElementType(debugName: String) : IElementType(debugName, GleamLanguage)

fun withValue(elementType: IElementType, value: Any): IElementType {
    if (elementType is GleamTokenType) {
//        return GleamTokenType("$elementType('$value')", value)
//        return GleamTokenType("$elementType", value)
        return elementType;
    } else {
        throw IllegalArgumentException("Unsupported token type: $elementType")
    }
}
