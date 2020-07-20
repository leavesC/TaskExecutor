package github.leavesc.taskexecutor

import android.os.Build
import android.os.Handler
import android.os.Looper
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 作者：leavesC
 * 时间：2020/7/1 22:55
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class ThreadPoolTaskExecutor : TaskExecutor() {

    companion object {

        private fun createAsync(looper: Looper): Handler {
            if (Build.VERSION.SDK_INT >= 28) {
                return Handler.createAsync(looper)
            }
            if (Build.VERSION.SDK_INT >= 16) {
                try {
                    return Handler::class.java.getDeclaredConstructor(
                        Looper::class.java, Handler.Callback::class.java,
                        Boolean::class.javaPrimitiveType
                    ).newInstance(looper, null, true)
                } catch (ignored: IllegalAccessException) {
                } catch (ignored: InstantiationException) {
                } catch (ignored: NoSuchMethodException) {
                } catch (e: InvocationTargetException) {
                    return Handler(looper)
                }
            }
            return Handler(looper)
        }

    }

    private val diskIO by lazy {
        Executors.newFixedThreadPool(4, object : ThreadFactory {

            private val THREAD_NAME_STEM = "arch_disk_io_%d"

            private val threadId = AtomicInteger(0)

            override fun newThread(runnable: Runnable): Thread {
                val thread = Thread(runnable)
                thread.name = String.format(THREAD_NAME_STEM, threadId.getAndIncrement())
                return thread
            }
        })
    }

    private val mainHandler by lazy {
        createAsync(Looper.getMainLooper())
    }

    override fun <T> executeOnIO(runnable: () -> T, taskCallback: TaskCallback<T>?) {
        diskIO.execute {
            runTask(runnable, taskCallback)
        }
    }

    override fun <T> postToMainThread(runnable: () -> T, taskCallback: TaskCallback<T>?) {
        mainHandler.post {
            runTask(runnable, taskCallback)
        }
    }

}