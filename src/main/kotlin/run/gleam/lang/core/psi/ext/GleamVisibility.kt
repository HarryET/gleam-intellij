package run.gleam.lang.core.psi.ext

import com.intellij.psi.impl.ElementBase
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.PlatformIcons
import run.gleam.lang.core.psi.GleamVisibilityModifier
import javax.swing.Icon

sealed class GleamVisibility {
    object Private : GleamVisibility()
    object Public : GleamVisibility()
}

interface GleamVisible : GleamElement {
    val visibility: GleamVisibility
    val isPublic: Boolean
}

interface GleamVisibilityOwner : GleamVisible {
    val vis: GleamVisibilityModifier?
        get() = PsiTreeUtil.getStubChildOfType(this, GleamVisibilityModifier::class.java)

    override val visibility: GleamVisibility
        get() = vis?.visibility ?: GleamVisibility.Private

    override val isPublic: Boolean
        get() = vis != null
}

/**
 * Supports adding different kinds of visibility modifiers, for now we only have 1
 */
enum class GleamVisStubKind {
    PUB
}

fun GleamVisibilityOwner.iconWithVisibility(flags: Int, icon: Icon): Icon {
    val visibilityIcon = when (vis?.stubKind) {
        GleamVisStubKind.PUB -> PlatformIcons.PUBLIC_ICON
        null -> PlatformIcons.PRIVATE_ICON
    }
    return ElementBase.iconWithVisibilityIfNeeded(flags, icon, visibilityIcon)
}

fun GleamVisibility.intersect(other: GleamVisibility): GleamVisibility = when (this) {
    GleamVisibility.Private -> this
    GleamVisibility.Public -> other
}

fun GleamVisibility.unite(other: GleamVisibility): GleamVisibility = when {
    this == GleamVisibility.Private && other is GleamVisibility.Private -> GleamVisibility.Private
    else -> GleamVisibility.Public
}

fun GleamVisibility.format(): String = when (this) {
    GleamVisibility.Private -> ""
    GleamVisibility.Public -> "pub "
}

val GleamVisibilityModifier.visibility: GleamVisibility
    get() = when (stubKind) {
        GleamVisStubKind.PUB -> GleamVisibility.Public
    }

val GleamVisibilityModifier.stubKind: GleamVisStubKind
    get() = greenStub?.kind ?: when {
        else -> GleamVisStubKind.PUB
    }
