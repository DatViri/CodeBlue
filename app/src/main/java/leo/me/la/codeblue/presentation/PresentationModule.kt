package leo.me.la.codeblue.presentation

import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val presentationModule = module {
    viewModel {
        UserViewModel(get(), get())
    }
}
