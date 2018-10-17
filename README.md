# LocationUpdateMonitor
An application that helps test out the multiple methods of obtaining location updates via the FusedLocationProvider api.

TLDR:
* Build and submit a location update request
* Wait for location updates to come in and review their details

This project started out as a hackjob of Google Play Location samples that Google kindly provides (https://github.com/googlesamples/android-play-location). Despite the samples, there is still a lot of mystery and ambiguity surrounding the location updates mechanism, and its constraints and limitations. Rather than trusting the docs, test it out and draw your own conclusions.

It's still a very much a work in progress. Currently, only the regular callback updates and background updates are wired up.
The foreground service updates were working, but I've disabled them for the time being. Only the background updates are currently being persisted.

Some of the code is straight out of the samples provided by Google, which have been processed through the Java-to-Kotlin converter. So if it looks odd, chances are I've not gotten to it yet.


