CheckableView
===========

CheckableView is a redesigned checkbox with more interactive components that make it extraordinary. Custom animation, colors, customization, make this a must have component in your arsenal.
It is highly configurable and works for both phone and tablet. CheckableView is only available for API levels 15+, however, you may fork/clone and modify this. See below for usage and examples.
If you have any questions, issues, or want to contribute, please submit an issue or Pull Request, or you may contact me.

What It Looks Like:
------------------

See a short video of this control here:


### Screen Shots


How To Use It:
-------------

### Basic Example

```java

// CheckableView Sample.
// See Sample Module for additional customization as well as multiple examples.

 <com.github.gfranks.checkable.view.CheckableView
     android:id="@+id/checkable_view"
     android:layout_width="125dp"
     android:layout_height="100dp"
     app:checkedImage="@drawable/checked_image"
     app:checkedColor="@color/blue"
     app:normalImage="@drawable/normal_image"
     app:normalColor="@color/gray_light"
     app:borderColor="@color/gray"
     app:borderWidth="3"
     app:normalBackgroundColor="@color/gray"
     app:checkedBackgroundColor="@color/white"
     app:isChecked="true" />
```


Customization:
----------------
* `checkedImage` Resource Id of the checked image to be set
* `normalImage` Resource Id of the normal image to be set
* `checkedColor` Color used to set the color filter on the checked image view
* `normalColor` Color used to set the color filter on the normal image view
* `borderColor` Color used as the border color of the CheckableView
* `borderWidth` Width of the border of the CheckableView
* `borderRadius` Radius to be used to define the corners of the border of the CheckableView
* `normalBackgroundColor` Color used as the background when the state is unchecked
* `checkedBackgroundColor` Color used as the background when the state is checked
* `animationDuration` Duration used for all animation
* `isChecked` The checked state of the CheckableView


Callback Methods:
----------------
// OnCheckedChangeListener
 /**
  * Called when the checked state of a CheckableView has changed.
  *
  * @param checkableView The CheckableView view whose state has changed.
  * @param isChecked  The new checked state of CheckableView.
  */
 void onCheckedChanged(CheckableView checkableView, boolean isChecked);

Installation:
------------

### Directly include source into your projects

- Simply copy the source/resource files from the library folder into your project.

### Use binary approach

- Follow these steps to include aar binary in your project:

    1: Copy com.github.gfranks.checkable.view-1.0.aar into your projects libs/ directory.

    2: Include the following either in your top level build.gradle file or your module specific one:
    ```
      repositories {
         flatDir {
             dirs 'libs'
         }
     }
    ```
    3: Under your dependencies for your main module's build.gradle file, you can reference that aar file like so:
    ```compile 'com.github.gfranks.checkable.view:com.github.gfranks.checkable.view-1.0@aar'```

License
-------
Copyright (c) 2015 Garrett Franks. All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.