Traceur
=======
Traceur enables easier debugging of RxJava2 exceptions, by appending
the source of any asynchronous calls to the original exception.

An example RxJava exception:
![](assets/stacktrace-without-traceur.png)



The same exception with Traceur enabled:
![](assets/stacktrace-with-traceur.png)

Notice that the source of the asynchronous call is linked in the stack
trace.

Usage
-----
Enable logging:
```
Traceur.enableLogging();
```

Disable logging:
```
Traceur.disableLogging();
```

Download
--------
```
compile 'com.tspoon.traceur:traceur:1.0.0'
```


License
-------

    Copyright 2017 Oisín O'Neill

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
