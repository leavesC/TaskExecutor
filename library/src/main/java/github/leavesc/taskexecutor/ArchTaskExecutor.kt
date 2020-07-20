package github.leavesc.taskexecutor

import java.util.concurrent.Executor

/**
 * 作者：leavesC
 * 时间：2020/7/1 22:55
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class ArchTaskExecutor private constructor() : TaskExecutor() {

    companion object {

        val instance: ArchTaskExecutor by lazy {
            ArchTaskExecutor()
        }

        val mainThreadExecutor = Executor { command ->
            instance.postToMainThread({
                command.run()
            }, null)
        }

        val iOThreadExecutor = Executor { command ->
            instance.executeOnIO({
                command.run()
            }, null)
        }

    }

    private var delegate: TaskExecutor = ThreadPoolTaskExecutor()

    fun setDelegate(taskExecutor: TaskExecutor) {
        delegate = taskExecutor
    }

    override fun <T> executeOnIO(runnable: () -> T, taskCallback: TaskCallback<T>?) {
        delegate.executeOnIO(runnable, taskCallback)
    }

    override fun <T> postToMainThread(runnable: () -> T, taskCallback: TaskCallback<T>?) {
        delegate.postToMainThread(runnable, taskCallback)
    }

    override fun isMainThread(): Boolean {
        return delegate.isMainThread()
    }

}