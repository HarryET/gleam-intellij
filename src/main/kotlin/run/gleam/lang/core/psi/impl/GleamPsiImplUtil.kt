package run.gleam.lang.core.psi.impl

import com.intellij.lang.ASTNode
import run.gleam.lang.core.psi.GleamProperty
import run.gleam.lang.core.psi.GleamTypes

object SimplePsiImplUtil {
    fun getKey(element: GleamProperty): String? {
        val keyNode: ASTNode? = element.getNode().findChildByType(GleamTypes.KEY)
        return keyNode?.text?.replace("\\\\ ".toRegex(), " ")
    }

    fun getValue(element: GleamProperty): String? {
        return element.getNode().findChildByType(GleamTypes.VALUE)?.text
    }
}