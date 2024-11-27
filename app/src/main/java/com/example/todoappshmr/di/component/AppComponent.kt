package com.example.todoappshmr.di.component

import android.content.Context
import com.example.todoappshmr.MainActivity
import com.example.todoappshmr.di.module.AppModule
import com.example.todoappshmr.di.module.NetworkModule
import com.example.todoappshmr.di.module.RepositoryModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RepositoryModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}