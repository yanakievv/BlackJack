package com.example.blackjack.main

interface BaseView<T> {
    fun setPresenter(presenter: T)
}