package com.android.mycustomkeyboard.di

import com.android.mycustomkeyboard.service.SoftKeyboardService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {
	@ServiceScoped
	@Provides
	fun provideSoftKeyboardService() = SoftKeyboardService()

}