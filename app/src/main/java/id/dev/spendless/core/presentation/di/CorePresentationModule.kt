package id.dev.spendless.core.presentation.di

import id.dev.spendless.core.presentation.pin_prompt.PinPromptViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val corePresentationModule = module {
    viewModelOf(::PinPromptViewModel)
}