package com.hadirahimi.movie.di

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.material.tabs.TabLayout
import com.hadirahimi.movie.R
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module
{
    //  Font : Bold
    @Provides
    @Singleton
    @Named(Constants.BOLD)
    fun provideFontBold(@ApplicationContext context : Context) : Typeface =
        ResourcesCompat.getFont(context , R.font.roboto_bold) !!
    
    // Font : Regular
    @Provides
    @Singleton
    @Named(Constants.REGULAR)
    fun provideFontRegular(@ApplicationContext context : Context) : Typeface =
        ResourcesCompat.getFont(context , R.font.roboto_regular) !!
    
    // SnapHelper : Single Scroll
    @Provides
    @Singleton
    fun providePagerSnapHelper() = PagerSnapHelper()
    
    // Response : Genre
    @Provides
    @Singleton
    fun publicGenres() = ResponseGenre()
    

    
}