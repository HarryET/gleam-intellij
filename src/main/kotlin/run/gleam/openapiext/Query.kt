/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package run.gleam.openapiext

import com.intellij.openapi.util.Condition
import com.intellij.util.FilteredQuery
import com.intellij.util.InstanceofQuery
import com.intellij.util.Query

// Be careful with queries: they are `Iterable`s, so they have Kotlin's
// `map`, `filter` and friends, which convert then to List.

fun <U> Query<U>.filterQuery(condition: Condition<U>): Query<U> = FilteredQuery(this, condition)

inline fun <reified V: Any> Query<*>.filterIsInstanceQuery(): Query<V> = InstanceofQuery(this, V::class.java)

@Suppress("UnstableApiUsage")
fun <U, V> Query<U>.mapQuery(f: (U) -> V) = mapping(f)

