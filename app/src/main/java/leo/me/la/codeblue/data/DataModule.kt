package leo.me.la.codeblue.data

import org.koin.dsl.module.module

val dataModule = module {
    factory<UserRepository> {
        UserRepositoryImpl(
            get(),
            get()
        )
    }
}
