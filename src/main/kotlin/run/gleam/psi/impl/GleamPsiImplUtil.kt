package run.gleam.psi.impl

import com.intellij.lang.ASTNode
import run.gleam.psi.GleamProperty
import run.gleam.psi.GleamTypes


object SimplePsiImplUtil {
    fun getKey(element: GleamProperty): String? {
        val keyNode: ASTNode? = element.getNode().findChildByType(GleamTypes.KEY)
        return keyNode?.text?.replace("\\\\ ".toRegex(), " ")
    }

    fun getValue(element: GleamProperty): String? {
        return element.getNode().findChildByType(GleamTypes.VALUE)?.text
    }
}