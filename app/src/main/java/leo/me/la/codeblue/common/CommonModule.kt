package leo.me.la.codeblue.common

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import leo.me.la.codeblue.domain.TAG_POST_EXECUTION
import leo.me.la.codeblue.domain.TAG_THREAD_EXECUTOR
import org.koin.dsl.module.module

val commonModule = module {

    factory(TAG_THREAD_EXECUTOR) {
        Schedulers.io()
    }

    single<Scheduler>(TAG_POST_EXECUTION) {
        AndroidSchedulers.mainThread()
    }
}
