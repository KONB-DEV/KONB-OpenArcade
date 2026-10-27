package off.kys.openarcade.di

import androidx.room.Room
import off.kys.openarcade.core.data.local.database.AppDatabase
import off.kys.openarcade.core.data.local.preferences.ArcadePreferences
import off.kys.openarcade.core.data.repository.GameRepositoryImpl
import off.kys.openarcade.core.data.repository.MediaRepositoryImpl
import off.kys.openarcade.core.data.repository.SystemRepositoryImpl
import off.kys.openarcade.core.domain.repository.GameRepository
import off.kys.openarcade.core.domain.repository.MediaRepository
import off.kys.openarcade.core.domain.repository.SystemRepository
import off.kys.openarcade.core.domain.usecase.AddGamesUseCase
import off.kys.openarcade.core.domain.usecase.GetGamesUseCase
import off.kys.openarcade.core.domain.usecase.GetSystemStatusUseCase
import off.kys.openarcade.core.domain.usecase.RefreshGamesUseCase
import off.kys.openarcade.core.domain.usecase.RemoveGameUseCase
import off.kys.openarcade.core.util.ColorExtractor
import off.kys.openarcade.core.util.GameScanner
import off.kys.openarcade.core.util.IconManager
import off.kys.openarcade.core.util.TimeUtils
import off.kys.openarcade.features.analytics.domain.GetAnalyticsDataUseCase
import off.kys.openarcade.features.analytics.ui.AnalyticsViewModel
import off.kys.openarcade.features.app_picker.domain.GetAppListUseCase
import off.kys.openarcade.features.app_picker.ui.AppPickerViewModel
import off.kys.openarcade.features.game_details.domain.GetGameByPackageUseCase
import off.kys.openarcade.features.game_details.domain.LaunchGameUseCase
import off.kys.openarcade.features.game_details.domain.RefreshGameStatsUseCase
import off.kys.openarcade.features.game_details.ui.GameDetailViewModel
import off.kys.openarcade.features.games_launcher.domain.RefreshAllGameStatsUseCase
import off.kys.openarcade.features.games_launcher.domain.RenameGameUseCase
import off.kys.openarcade.features.games_launcher.domain.ToggleFavoriteUseCase
import off.kys.openarcade.features.games_launcher.domain.UpdateGameCategoryUseCase
import off.kys.openarcade.features.games_launcher.domain.UpdateGameIconUseCase
import off.kys.openarcade.features.games_launcher.ui.GamesLauncherViewModel
import off.kys.openarcade.features.settings.ui.SettingsViewModel
import off.kys.openarcade.ui.main.AppViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module providing persistent infrastructure data layer dependencies.
 *
 * Configures the Room database framework instance ([AppDatabase]), its respective DAOs,
 * shared application preferences data stores ([ArcadePreferences]), and binds abstraction contracts
 * to concrete implementations for game, media, and system repositories.
 */
val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, "openarcade-db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single { get<AppDatabase>().gameDao() }
    single { ArcadePreferences(androidContext()) }

    single<MediaRepository> { MediaRepositoryImpl(get(), get()) }
    single<SystemRepository> { SystemRepositoryImpl(androidApplication(), get()) }
    single<GameRepository> { GameRepositoryImpl(androidContext(), get(), get(), get()) }
}

/**
 * Koin module providing foundational operational utility structures.
 *
 * Exposes core singleton tools leveraged across distinct boundaries for application features,
 * handling physical icon mutations ([IconManager]), palette computations ([ColorExtractor]),
 * device-level package queries ([GameScanner]), and system time localization mappings ([TimeUtils]).
 */
val utilityModule = module {
    single { IconManager(androidContext()) }
    single { ColorExtractor() }
    single { GameScanner(androidContext()) }
    single { TimeUtils(androidContext()) }
    single<off.kys.openarcade.core.util.HapticFeedbackService> {
        off.kys.openarcade.core.util.HapticFeedbackServiceImpl(androidContext(), get())
    }
}

/**
 * Koin module mapping core business logic and transaction boundaries.
 *
 * Declares domain-level Interactors / Use Cases using short-lived [factoryOf] registrations.
 * This guarantees instances remain stateless across distinct invocations from consuming view models.
 */
val domainModule = module {
    factoryOf(::GetGamesUseCase)
    factoryOf(::RefreshGamesUseCase)
    factoryOf(::RefreshGameStatsUseCase)
    factoryOf(::RefreshAllGameStatsUseCase)
    factoryOf(::GetGameByPackageUseCase)
    factoryOf(::UpdateGameCategoryUseCase)
    factoryOf(::GetAnalyticsDataUseCase)
    factoryOf(::GetSystemStatusUseCase)
    factoryOf(::LaunchGameUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::RemoveGameUseCase)
    factoryOf(::RenameGameUseCase)
    factoryOf(::UpdateGameIconUseCase)
    factoryOf(::AddGamesUseCase)
    factoryOf(::GetAppListUseCase)
}

/**
 * Koin module mapping architecture state containers to user presentation targets.
 *
 * Hooks up UI view models to native lifecycle structures. Includes explicit parameter mappings
 * for shared scope initialization hooks ([AppViewModel]), default reflection-driven declarations,
 * and dynamic assisted runtime navigation parameters (such as the package name required by [GameDetailViewModel]).
 */
val featureViewModelModule = module {
    viewModel {
        AppViewModel(
            prefs = get(),
            getGamesUseCase = get(),
            getSystemStatusUseCase = get(),
            refreshGamesUseCase = get(),
            refreshAllGameStatsUseCase = get()
        )
    }
    viewModelOf(::GamesLauncherViewModel)
    viewModelOf(::AppPickerViewModel)
    viewModelOf(::AnalyticsViewModel)
    viewModelOf(::SettingsViewModel)

    viewModel { (packageName: String) ->
        GameDetailViewModel(
            packageName = packageName,
            getGameByPackageUseCase = get(),
            updateGameCategoryUseCase = get(),
            refreshGameStatsUseCase = get(),
            launchGameUseCase = get(),
            prefs = get()
        )
    }
}

/**
 * Consolidates the modular subcomponents of the dependency graph into a single reference collection.
 *
 * Pass this directly to `startKoin` inside the target application class context wrapper
 * to initialize structural resolution rules during application launch runtime.
 */
val appModules = listOf(
    dataModule,
    utilityModule,
    domainModule,
    featureViewModelModule
)