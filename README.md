# Location Update Monitor
An application that helps test out the multiple methods of obtaining location updates via the FusedLocationProvider api.

TLDR:
* Build and submit a location update request
* Wait for location updates to come in and review their details

This project started out as a hackjob of Google Play Location samples that Google kindly provides (https://github.com/googlesamples/android-play-location). Despite the samples, there is still a lot of mystery and ambiguity surrounding the location updates mechanism, and its constraints and limitations. Rather than trusting the docs, test it out and draw your own conclusions.

The app is still a very much a work in progress. Currently, only the regular callback updates and background updates are wired up.
The foreground service updates were working, but I've disabled them for the time being. Only the background updates are currently being persisted.

Some of the code is straight out of the samples provided by Google, which have been processed through the Java-to-Kotlin converter. So if it looks odd, chances are I've not gotten to it yet.


## License

Copyright (c) 2018 Zaglab

```Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.```
