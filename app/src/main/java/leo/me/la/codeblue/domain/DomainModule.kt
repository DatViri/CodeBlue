package leo.me.la.codeblue.domain

import org.koin.dsl.module.module

const val TAG_THREAD_EXECUTOR = "TAG_THREAD_EXECUTOR"
const val TAG_POST_EXECUTION = "TAG_POST_EXECUTION"
val domainModule = module {
    factory {
        FetchUserUseCase(
            get(),
            get(TAG_THREAD_EXECUTOR),
            get(TAG_POST_EXECUTION)
        )
    }
}
