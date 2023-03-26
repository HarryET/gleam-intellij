package run.gleam

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import run.gleam.GleamTask.TaskType.*
import run.gleam.util.GleamBackgroundTaskQueue

/**
 * A common queue for cargo and macro expansion tasks that should be executed sequentially.
 * Can run any [Task.Backgroundable], but provides additional features for tasks that implement [GleamTask].
 * The most important feature is that newly submitted tasks can cancel a currently running task or
 * tasks in the queue (See [GleamTask.taskType]).
 */
@Service
class GleamProjectTaskQueueService : Disposable {
    private val queue: GleamBackgroundTaskQueue = GleamBackgroundTaskQueue()

    /** Submits a task. A task can implement [GleamTask] */
    fun run(task: Task.Backgroundable) = queue.run(task)

    /** Equivalent to running an empty task with [GleamTask.taskType] = [taskType] */
    fun cancelTasks(taskType: GleamTask.TaskType) = queue.cancelTasks(taskType)

    /** @return true if no running or pending tasks */
    val isEmpty: Boolean get() = queue.isEmpty

    override fun dispose() {
        queue.dispose()
    }
}

val Project.taskQueue: GleamProjectTaskQueueService get() = service()

interface GleamTask {
    val taskType: TaskType
        get() = INDEPENDENT

    val progressBarShowDelay: Int
        get() = 0

    /** If true, the task will not be run (and progress bar will not be shown) until the smart mode */
    val waitForSmartMode: Boolean
        get() = false

    val runSyncInUnitTests: Boolean
        get() = false

    /**
     * Higher position in the enum means higher priority; Newly submitted tasks with higher or equal
     * priority cancels other tasks with lower or equal priority if [canBeCanceledByOther] == true.
     * E.g. [CARGO_SYNC] cancels [MACROS_UNPROCESSED] and subsequent but not [MACROS_CLEAR] or itself.
     * [MACROS_UNPROCESSED] cancels itself, [MACROS_FULL] and subsequent.
     */
    enum class TaskType(val canBeCanceledByOther: Boolean = true) {
        GLEAM_SYNC(canBeCanceledByOther = false),

        /** Can't be canceled, cancels nothing. Should be the last variant of the enum. */
        INDEPENDENT(canBeCanceledByOther = false);

        fun canCancelOther(other: TaskType): Boolean =
            other.canBeCanceledByOther && this.ordinal <= other.ordinal
    }
}

