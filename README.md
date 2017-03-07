# CaptureView

Draw rectangle to select specific area.

![sample](http://ww1.sinaimg.cn/mw690/6db4aff6ly1fde9sylvgqg20gs0tuu0x)  

## Features

* Select specific area.

## Screenshot

![Screenshot](http://ww1.sinaimg.cn/mw690/6db4aff6ly1fde9r8vp7jj20k00zkmzi)

## Import
##### Gradle
```
compile 'me.next.captureview:captureview:1.0.1'
```

##### Maven
```
<dependency>
  <groupId>me.next.captureview</groupId>
  <artifactId>captureview</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

## Usage

##### Layout.xml
```
<me.next.captureview.CaptureView
        android:id="@+id/your_id"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

##### code
```
CaptureView captureView = (CaptureView) findViewById(R.id.cv_main);
        captureView.setPaintColor(getResources().getColor(R.color.colorAccent));
        captureView.setOnButtonClickListener(new OnButtonClickListener() {
            @Override
            public void onConfirmClick(Rect rect) {
                int left = rect.left;
                int top = rect.top;
                int right = rect.right;
                int bottom = rect.bottom;
                // do something ...
            }

            @Override
            public void onCancelClick() {

            }
        });

```

## License

    Copyright 2017 NeXT

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
