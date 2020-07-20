package github.leavesc.taskexecutor

import android.os.Looper

/**
 * 作者：leavesC
 * 时间：2020/7/1 22:55
 * 描述：
 * GitHub：https://github.com/leavesC
 */
interface TaskCallback<T> {

    fun onSuccess(data: T) {

    }

    fun onFail(throwable: Throwable) {

    }

}

interface EmptyTaskCallback : TaskCallback<Unit> {

    override fun onSuccess(data: Unit) {
        onSuccess()
    }

    fun onSuccess() {

    }

}

abstract class TaskExecutor {

    abstract fun <T> executeOnIO(runnable: () -> T, taskCallback: TaskCallback<T>?)

    fun executeOnIOEmpty(runnable: () -> Unit, taskCallback: EmptyTaskCallback? = null) {
        executeOnIO(runnable, taskCallback)
    }

    fun <T> executeOnMain(runnable: () -> T, taskCallback: TaskCallback<T>?) {
        if (isMainThread()) {
            runTask(runnable, taskCallback)
        } else {
            postToMainThread(runnable, taskCallback)
        }
    }

    fun <T> executeOnMainEmpty(runnable: () -> Unit, taskTaskCallback: EmptyTaskCallback? = null) {
        executeOnMain(runnable, taskTaskCallback)
    }

    protected fun <T> runTask(runnable: () -> T, taskCallback: TaskCallback<T>?) {
        try {
            val res = runnable()
            taskCallback?.onSuccess(res)
        } catch (throwable: Throwable) {
            taskCallback?.onFail(throwable)
        }
    }

    internal abstract fun <T> postToMainThread(
        runnable: () -> T,
        taskCallback: TaskCallback<T>?
    )

    open fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }

}