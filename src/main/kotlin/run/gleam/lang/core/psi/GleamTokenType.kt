package run.gleam.lang.core.psi

import com.intellij.psi.tree.IElementType
import run.gleam.lang.GleamLanguage

open class GleamTokenType(debugName: String) : IElementType(debugName, GleamLanguage)

open class GleamElementType(debugName: String) : IElementType(debugName, GleamLanguage)