package run.gleam.lang.core.stubs

import com.intellij.psi.stubs.IndexSink
import run.gleam.lang.core.stubs.index.GleamNamedElementIndex

fun IndexSink.indexFunction(stub: GleamFunctionStub) {
    indexNamedStub(stub)
}

private fun IndexSink.indexNamedStub(stub: GleamNamedStub) {
    stub.name?.let {
        occurrence(GleamNamedElementIndex.KEY, it)
    }
}
