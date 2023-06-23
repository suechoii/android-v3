package com.community.mingle.di

import com.community.mingle.api.*
import com.community.mingle.service.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideMemberRepository(memberService: MemberService): MemberRepository {
        return MemberRepository(memberService)
    }

    @Provides
    fun provideAuthRepository(authService: AuthService): AuthRepository {
        return AuthRepository(authService)
    }

    @Provides
    fun provideHomeRepository(homeService: HomeService): HomeRepository {
        return HomeRepository(homeService)
    }

    @Provides
    fun providePostRepository(postService: PostService): PostRepository {
        return PostRepository(postService)
    }

    @Provides
    fun provideUnivTotalRepository(univTotalService: UnivTotalService): UnivTotalRepository {
        return UnivTotalRepository(univTotalService)
    }

    @Provides
    fun provideMyPageRepository(myPageService: MyPageService): MyPageRepository {
        return MyPageRepository(myPageService)
    }

    @Provides
    fun provideMarketRepository(marketService : MarketService): MarketRepository {
        return MarketRepository(marketService)
    }
}