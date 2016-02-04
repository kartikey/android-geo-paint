# Geo-Paint
Check out [this video](https://vimeo.com/4762735) of dogs playing in the park. And [this video](https://vimeo.com/6502058) of the dogs without the park. And [this article](http://www.bbc.com/news/technology-35175265) about a creative runner (follow the in-article links for more!)


The Android framework was designed for _mobile_ devices---that is, devices that are able to easily move around the physical world. Being able to harness and react to a device's _location_ is what makes Android apps different than "regular" computers, and is arguably the entire point of developing them!

For this assignment, you will build an app that uses the device's _physical location_ as input for a simple drawing program. By caring the device around a (large) space, the user will be able to draw pictures based on their movements, similar to the links above or the work by [this artist](http://www.gpsdrawing.com/gallery.html). In effect, users will carry around a virtual "pen" (similar to that used in [Turtle graphics](https://en.wikipedia.org/wiki/Turtle_graphics)), producing a drawing visible on a map. Users will also be able to share these drawings with others, allowing for collaborative art. You'll also be asked to actually _use_ your app to produce some artwork of your own!

This assignment should be completed individually. You are welcome to ask for help (either from me or from your classmates), but remember the Gilligan's Island rule!


### Objectives
By completing this assignment you will practice and master the following skills:
* Accessing location sensors (e.g., GPS) found on mobile devices
* Integrating Google Play Services and displaying interactive maps
* Handling run-time permission requests
* Creating and using menus
* Loading and using packages and third-party libraries in Android
* Saving files and sharing them with other apps


## User Stories
The user stories for the Geo-Paint app are:
* As a user, I want to be able to draw a picture mirroring my movements
* As a user, I want to see my drawing displayed on a map
* As a user, I want to draw in different colors
* As a user, I want to save my drawing and share it with others
* Extra Credit: As a user, I want to view drawings shared with me


## Implementation Details
This assignment actually has _fewer_ moving parts, though they can still be tricky to test and get working.

### Fork and Create
As with all assignments, you should start by **forking** and **cloning** this repository. It includes any starter code plus the `SUBMISSION.md` file you will need to complete.

You will need to create a new project through Android Studio. Make sure to name your project **geopaint**, so that the package is `edu.uw.uwnetid.geopaint`. _You will need to save the project inside your cloned repo!_ (i.e., in the top-level directory).

For this assignment, you should once again target **API 15 (4.0.3 Ice Cream Sandwich)** as your minimum SDK.

Your application will probably just need a single Activity (though you can include more if it helps with the user experience), and you will need to include at least one fragment (since maps are put in fragments). You can start with the _Google Maps Activity_ template, or with a _blank_ Activity (and fill in the few required steps to display a map).

Once you've created the app, go into the `build.gradle` file (Module level) and set the target SDK to be **23 or lower**; (you will need to target Marshmallow to be able to test runtime permission requests).

#### Play Services and Keys
You will need to add the **Google Play Services** package to your application as well, so that you can use the included Location services. In Android Studio, go to `Tools > Android > SDK Manager`, and under the `SDK Tools` tab select `Google Play services` and `Google Repository` (see [here](http://developer.android.com/sdk/installing/adding-packages.html) for details).

- You'll also need to modify your `build.gradle` file so that you can get access to the Location classes. In the ___module-level___ `build.gradle` file, under `dependencies` add
  ```
  compile 'com.google.android.gms:play-services-location:8.4.0'
  ```
  This will load in the location services (but not the other services, which require keys)

  Note that testing there were a couple issues getting the [gradle plugin](https://developers.google.com/android/guides/google-services-plugin?hl=en) setup; basically making sure that you provide the correct keys and files to access play services. The above dependency _should_ be sufficient. If there are issues, you _might_ need to register your app for [cloud messaging access](https://developers.google.com/mobile/add) in order to get required `.json` configuration file (though it shouldn't be required; it may just be a bug in Android). If you have any troubles, please check in with us!

Lastly, you will **also** need to sign up for a [Google Maps API Key](https://developers.google.com/maps/documentation/android-api/start#step_4_get_a_google_maps_api_key), and include it in your application.


### Overall User Interface
Your application's interface should center on a displayed map. This map should be _dynamic_, in that the user can use the built-in interface to move the camera around, etc. The map should at least include [zoom controls](https://developers.google.com/maps/documentation/android-api/controls#zoom_controls), as well as another other interaction elements you wish. This map will display the current drawing the user is creating.

While the user will draw on the map by walking around (and having their location automatically create drawings on the map), your app will also need to support a few other interactions, including raising and lowering the "pen" and selecting the color (see _Changing the Pen Color_ below). You should provide this functionality via an [options menu](http://developer.android.com/guide/topics/ui/menus.html#options-menu). Additionally, your menu will need to support a the ability to share the drawing; the cleanest interface (though not code!) for this is to use a [`SharedActionProvider`](http://developer.android.com/training/sharing/shareaction.html) (see _Saving and Sharing_ below for details).


### Getting the Location
Although Android includes a built-in framework for working with location, the framework provided by the Google Play services is much more robust and is the preferred system (though note that this will not be available on devices that don't support Google Play services!). See [Making Your App Location-Aware](http://developer.android.com/training/location/index.html) for details about fetching location ([this](http://blog.teamtreehouse.com/beginners-guide-location-android) is another decent tutorial).

Per the documentation, you will need to [build](http://developer.android.com/reference/com/google/android/gms/common/api/GoogleApiClient.Builder.html) a [`GoogleApiClient`](http://developer.android.com/reference/com/google/android/gms/common/api/GoogleApiClient.html) object and `connect()` it (handled via the appropriate callbacks). You can then use the [LocationServices.FusedLocationApi](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderApi) to [request location updates](http://developer.android.com/training/location/receive-location-updates.html).

- A `LocationRequest` at a 10 second interval (5 second fastest) with a "high accuracy" priority is considered appropriate for getting the real-time location we're interested. You can also slow down the interval if needed.

Note that you will need permission to access a user's location (specifically, `ACCESS_FINE_LOCATION` for GPS-precision location). This is considered a [**dangerous**](http://developer.android.com/guide/topics/security/permissions.html#normal-dangerous) permission, and thus requires special handling in Marshmallow (API 23) and higher. _In addition_ to including the permission request in the `Manifest`, you will also need to [request permission at run-time](http://developer.android.com/training/permissions/requesting.html) (that's a guide link you should look at).

- The basic idea is that whenever you're going to get the location (e.g, with the `FusedLocationApi`), you will need to use `ContextCompat.checkSelfPermission()` to check if you have permission. If you do, then you can go about your business. But if not, you'll need to request permissions via `ActivityCompat.requestPermissions()`. This request will eventually issue a callback (`onRequestPermissionsResult()`) where you can "try again" to make your location request. If the user won't give you permission, then the user won't be drawing right now!

  - Refactoring your location requests into a separate method is great for avoid code repetition!

  - You are not required to show a "rationale" for why you need the permission for this assignment, though this would be a good place for a simple AlertDialog!

Testing location updates using your phone's built-in GPS can be a bit of a hassle (since you need to move around). However, if you work with the emulator, you can "fake" a location update by using the [Android Device Monitor](http://developer.android.com/tools/help/monitor.html) (found at `Tools > Android > Android Device Monitor` in Android Studio). In the Emulator tab under _Emulator Control_, you can "send" the emulator a location as if it had picked that up from the GPS.

### Drawing on a Map
To show a drawing on the map, you'll need to draw a [Shape](https://developers.google.com/maps/documentation/android-api/shapes) on it. Shapes are determined by a series of `LatLng` coordinates, which is exactly what you'll be getting from location updates!

In particular, you'll be interested in drawing [Polylines](https://developers.google.com/maps/documentation/android-api/shapes#polylines), which are multi-segment lines. The basic algorithm to use is that every time the user moves (so you get a new location update), add that spot to the multi-line, effectively creating a new short line to the current location from the previous one, thus tracing movements!

  - Because `Polylines` are drawn once defined, in order to "add" a point you'll need to <a href="https://developers.google.com/android/reference/com/google/android/gms/maps/model/Polyline.html#getPoints()">get</a> a list of all the points in the line, add your new point(s) to that, and then `set` the updated list as the Polyline's points.

The drawing doesn't need to be a single, continuous line: the user is able to either "raise" or "lower" the virtual pen on the map. If the pen is _up_, then no drawing should occur. If the pen is _down_, then you add more points to the `Polyline`.

- When the app starts, the pen should begin in the "up" position, so that the user needs to opt-in to drawing.
- When the user selects to put the pen down, your app can start drawing a _new_ `Polyline`, and will add new points as the user moves.
- If the user selects to raise the pen again, you should "end" the current `Polyline`. This isn't an explicit step, but it means that when the the user puts the pen down again, they'll be drawing a new line!
- If the user changes the color of the line (see below), you should end the current Polyline and immediately create another starting at the current location (but with the new color). That is, the pen should stay either "up" or "down" on a color change.


**Important:** Normally, map drawings and state (e.g., the location currently shown) will **reset** every time the Activity is re-created... and Activities get re-created a lot (like from rotating the phone). The _easiest_ workaround for this is to specify that the MapFragment should be <a href="http://developer.android.com/reference/android/app/Fragment.html#setRetainInstance(boolean)">retained</a> across activity re-creation. This will let you be able to save the current fragment's appearance, including the drawing, across configuration changes or some app switches.


Note that this is only a work-around; if the Activity gets destroyed (either at user request or because of low memory), the drawing will still be lost if it hasn't been explicitly persisted. The _ideal_ solution is probably to save each Polyline and its points in an `SQLiteDatabase`, but that is not required for this assignment. Alternatively, you can periodically save the current drawing to a file (in a _background thread_, such as through an `ASyncTask!`), which is functionality you'll need to include anyway (see below)! This can help if you find yourself losing your drawings a lot.

- You can also convert a Polyline into a String with the [Maps Utilility Library's](https://developers.google.com/maps/documentation/android-api/utility/) [PolyUtil](http://googlemaps.github.io/android-maps-utils/javadoc/com/google/maps/android/PolyUtil.html) class, if needed (though you'll need to save the color separately). See below for more details about using this library.


### Changing the Pen Color
In order to make the artwork more visually appealing, you should allow the user to specify the color of the pen. Android provides a [`Color`](http://developer.android.com/intl/zh-tw/reference/android/graphics/Color.html) class similar to Java's that allows color specification in terms of RGB, HSV, hex codes, etc.

It would be nice for the user to be able to select a color from a complex interface, such as a color wheel or color picker. Setting up such a picker sounds like a lot of work... and is a common enough component that you'd expect someone else to have implemented it. Thus you should find a **third party library** that provides a color picker, and use that to let the user choose a color!

A good place to look for third-party Android libraries is [jCenter](https://bintray.com/bintray/jcenter). This is a repository (similar to `npm` in Node) that lists Java packages that can be easily loaded in through Gradle; _jCenter_ is the default repository for Android. Search this site (using the search bar at the top!) for an appropriate library to include (`android` and `color` are good keywords).

- Once you've found a library, you'll probably want to visit it's GitHub page for more details; _jCenter_ doesn't usually show module documentation. Generally you'll be able to install and use a library by including it as another `dependency` in Gradle, and then simply importing and using the classes like you would any othr Android component.

- A Google search also found [Android Arsenal](https://android-arsenal.com/) as a potential source for libraries, if you can't find anything you like).

- Be sure and include a note about what library you used in your `SUBMISSION.md` file.

- This will involve practicing learning to use new components on your own!

You might consider using the [SharedPreferences](http://developer.android.com/training/basics/data-storage/shared-preferences.html) (with or without a Settings fragment) to save the chosen color(s) application executions, so if I always want to draw my picture in Purple I can.

- You could save other drawing properties, like the pen thickness as well.


### Saving & Sharing Drawings
_Note: this section is more experiment, and the professor reserves the right to adjust it as needed._

Art is most meaningful when it can be shared with others, so you should include a way for the user to _save_ and _share_ the drawings they've created (so they don't lose all their hard work!). While a database is a good way to _save_ data, it makes it hard to share the picture. The `GoogleMap` class does include an <a href="https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.html#snapshot(com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback)">`.snapshot()`</a> method for taking a screen capture, but for copyright reasons these images are not supposed to be used outside of the app (e.g., they can't be shared). It's also hard to add more to a screenshot...

To get around this, your app should allow the user to save their drawing to a _file_. In order to make drawings as shareable as possible, you should save drawings using [GeoJSON](http://geojson.org/) format, which is an extension of JSON data that represents geographic information (a basic example is [here](http://geojson.org/geojson-spec.html#examples)). GeoJSON format can be read by Google Maps (the online version), as well as other mapping systems like [Mapbox](https://www.mapbox.com/) or [Leaflet](http://leafletjs.com/).

#### Saving The Drawing
The first step is being able to save the current drawing. While Google does provide utility methods for working with GeoJSON data in the [Maps Utility Library](https://developers.google.com/maps/documentation/android-api/utility/) ([docs](http://googlemaps.github.io/android-maps-utils/)). However, these utilities are for _reading_ existing GeoJSON data; so to save your data in that format you'll have to "roll your own" (we'll see if we can get some sample code provided).

When the user chooses to **save** the drawing (such as by selecting an option from the menu), you will need to fetch all of the current `Polylines` on the map, and then parse through them to produce a String representing a GeoJSON object.

  - This is more of that "basic Java practice", doing String building. You can also use `JSONObjects` to keep things organized, or even the `GeoJSON` utility classes (unfortunately their `.toString()` methods don't actually produce valid JSON).

  - You can test your outputted String by pasting it into [this linter](http://geojsonlint.com/) or [this interactive drawer](http://geojson.io/#map=2/20.0/0.0).

You will then need to write this String to a file saved _privately_ on [External Storage](http://developer.android.com/guide/topics/data/data-storage.html#filesExternal). This will make the files world-readable so they can be shared later, but will also keep them "hidden" from the user as part of the app!

  - You will need to request permission to write to external storage.

  - Remember to check for media availability!

  - You can get access to a "private" external folder with the <a href="http://developer.android.com/reference/android/content/Context.html#getExternalFilesDir(java.lang.String)">`getExternalFilesDir()`</a> method. You can then create a new `FileOutputStream` object to write to:
  ```java
  File file = new File(this.getExternalFilesDir(null), "drawing.geojson");
  FileOutputStream outputStream = new FileOutputStream(file);
  outputStream.write(string.getBytes()); //write the string to the file
  outputStream.close(); //close the stream
  ```

**Important:** You should save your file with the `.geojson` extension. Technical it should be a `.json` file, but by being more specific you'll be prepared for your application to be able to _open_ these files later if you wanted.


#### Sharing the Drawing
You should enable the user to share your drawing (the file!) through an [Action Provider](http://developer.android.com/training/appbar/action-views.html#action-provider). This is a handy widget that produces a "Share" menu button, that will allow some data to be shared with any app that supports it. See the [reference](http://developer.android.com/reference/android/support/v7/widget/ShareActionProvider.html) for more details (be careful not to mix up the support and non-support versions!)

- You will need to craft an `Intent` to share the drawing through. This should use `ACTION_SEND`, a type of `text/plain`, and should include the file `Uri` as an `EXTRA` (specifically, an `EXTRA_STREAM`).


#### Loading a Drawing (extra credit!)
As extra credit, add the ability for your application to "open" `.geojson` files and show them in the drawing. There are a few parts to this, all of them complex:

- You'll need to add an `<intent-filter>` so that your application can open downloaded `.geojson` files (see [here](http://richardleggett.co.uk/blog/2013/01/26/registering_for_file_types_in_android/) for one unverified example).

- You'll also need to load that GeoJSON String into a [`GeoJSONLayout`](http://googlemaps.github.io/android-maps-utils/javadoc/) using the [Map Utility Library](http://googlemaps.github.io/android-maps-utils/).

- You may also want to add interfaces that allow the user to select _which_ `.geojson` file to open. You may be able to find a File Picker library to use (similar to the Color Picker!)


## Make some Art!
Before you turn in your application, you should actually take it out for a spin and produce a drawing! This can be anything you want; I'm hoping for something on the complexity of a short word, a happy-face, or a [butterfly](http://www.gpsdrawing.com/gallery/land/nbutterlfy.htm). Scale or distance traveled doesn't matter. You should upload a screen-capture of your app displaying the drawing (in the `screenshots` folder of the repo).



## Submit Your Solution
In order to submit programming assignments in this class, you will need to both `push` your completed program to your GitHub repository (the one in the cloud that you created by forking), and submit a link to your repository to [Canvas](https://canvas.uw.edu/) (so that we know where to find your work)!

Before you submit your assignment, double-check the following:

* Test that your app builds (from `gradle`!), installs, and works without errors. It should fulfill all the user stories.
* You've included a screenshot showing off your artwork.
* Fill out the `SUBMISSION.md`included in the assignment directory, answering the questions.
* Commit the final version of your work, and push your code to your GitHub repository.

Submit a a link to your GitHub repository via [this canvas page](https://canvas.uw.edu/courses/1023396/assignments/3082084).

The assignment is due on **Wed Feb 10 at 6:00 AM**.

### Grading Rubric
See the assignment page on Canvas for the grading rubric.


_Based on an assignment by Don Patterson._
