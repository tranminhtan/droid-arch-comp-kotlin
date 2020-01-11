package com.task.app.base

import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer

interface RxTransformer<T, R> : SingleTransformer<T, R>, ObservableTransformer<T, R>