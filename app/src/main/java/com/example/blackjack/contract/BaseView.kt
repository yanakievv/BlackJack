package com.example.blackjack.contract

interface BaseView<T> {
    fun setPresenter(presenter: T)
}