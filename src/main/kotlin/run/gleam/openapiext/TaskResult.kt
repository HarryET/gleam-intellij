/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package run.gleam.openapiext

sealed class TaskResult<out T> {
    class Ok<out T>(val value: T) : TaskResult<T>()
    class Err<out T>(val reason: String, val message: String? = null) : TaskResult<T>()
}
