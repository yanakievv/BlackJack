package com.example.blackjack

interface BaseView<T> {
    fun setPresenter(presenter: T)
}