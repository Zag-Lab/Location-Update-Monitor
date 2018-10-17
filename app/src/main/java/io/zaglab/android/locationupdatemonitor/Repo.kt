package io.zaglab.android.locationupdatemonitor

object Repo {
    //TODO: set up room

    interface Persistable<T> {
        fun save()
        fun get() : T
        fun clear()
    }
}