package leo.me.la.codeblue

import android.app.Application
import leo.me.la.codeblue.cache.cacheModule
import leo.me.la.codeblue.common.commonModule
import leo.me.la.codeblue.data.dataModule
import leo.me.la.codeblue.domain.domainModule
import leo.me.la.codeblue.presentation.presentationModule
import leo.me.la.codeblue.remote.remoteModule
import org.koin.android.ext.android.startKoin

class CodeBlueApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(
            this,
            listOf(
                cacheModule,
                remoteModule,
                dataModule,
                domainModule,
                presentationModule,
                commonModule
            )
        )
    }
}
