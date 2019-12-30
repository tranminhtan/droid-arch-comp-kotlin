package com.task.app.base

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class MockSchedulersProvider : SchedulersProvider {

    override fun io(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = Schedulers.trampoline()

    override fun computation(): Scheduler = Schedulers.trampoline()
}