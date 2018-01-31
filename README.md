[Smartling Translation API](http://docs.smartling.com)
=================

This repository contains the Java SDK for accessing the Smartling Translation API.

The Smartling Translation API allows developers to seamlessly internationalize their website by automating the translation and integration of their site content.
Developers can upload resource files and download the translated files in a language of their choosing. There are options to allow for professional translation, community translation and machine translation.

For a full description of the Smartling Translation API, please read the docs at: https://developer.smartling.com/v1.0/reference


Quick start
-----------

Clone the repo, `git clone git@github.com:Smartling/api-sdk-java.git`.

```
$ mvn clean install
```

Note: Integration tests will fail without a valid Smartling account, however as of 3.0.0 this will not block the build.  With a Smartling Account you can run the integration tests:

```
$ mvn verify -DuserId=<smartling user id> -DuserSecret=<smartling user secret> -DprojectId=<smartling project>
```

See https://docs.smartling.com/display/docs/Files+API for more details.


Versioning
----------

For transparency and insight into our release cycle, and for striving to maintain backward compatibility, the Smartling Translation API SDK will be maintained under the Semantic Versioning guidelines as much as possible.

Releases will be numbered with the follow format:

`<major>.<minor>.<patch>`

And constructed with the following guidelines:

* Breaking backward compatibility bumps the major
* New additions without breaking backward compatibility bumps the minor
* Bug fixes and misc changes bump the patch

For more information on SemVer, please visit http://semver.org/.


Artifacts
---------

The latest artifacts for the SDK are available via maven. You can find them here:

* http://mvnrepository.com/artifact/com.smartling/smartling-api-sdk
* http://search.maven.org/#search|ga|1|g%3A%22com.smartling%22


Bug tracker
-----------

Have a bug? Please create an issue here on GitHub!

https://github.com/Smartling/api-sdk-java/issues


Authors
-------

Greg Jones

* http://github.com/jones-smartling
* gjones@smartling.com


Copyright and license
---------------------

Copyright 2012 Smartling, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
