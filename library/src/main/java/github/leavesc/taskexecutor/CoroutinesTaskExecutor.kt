package github.leavesc.taskexecutor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 作者：leavesC
 * 时间：2020/7/1 23:40
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class CoroutinesTaskExecutor : TaskExecutor() {

    private val globalScope: CoroutineScope
        get() = GlobalScope

    override fun <T> executeOnIO(runnable: () -> T, taskCallback: TaskCallback<T>?) {
        globalScope.launch(Dispatchers.IO) {
            runTask(runnable, taskCallback)
        }
    }

    override fun <T> postToMainThread(runnable: () -> T, taskCallback: TaskCallback<T>?) {
        globalScope.launch(Dispatchers.Main) {
            runTask(runnable, taskCallback)
        }
    }

}