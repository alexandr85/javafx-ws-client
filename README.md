## *JavaFX Websocket & Rest client for testing*

###### Features:
* Connect to ws:// and wss:// websocket server
* Filtered incoming websocket message
* Collected sent message history
* Use custom headers for http connection
* Save output messages to text file
* Show current session info in status bar
* Show websocket message in new tab
* Send selected messages list after connect
* Send GET & POST http request with header & parameters
* View response headers & body message
* Pretty json message in new tab as tree view

---

###### Dependencies:
* java 1.8
* java 1.9+ not supported yet

---
 
###### Build application for all os:
`{project.path}$ ./gradlew create` - create jar, zip (for windows), dmg (for mac) (build/distributions)

###### Build application for MacOS:
`{project.path}$ ./gradlew createApp` - create mac bundle (build/macApp)

###### Build application for Windows:
`{project.path}$ ./gradlew createExe` - create windows wrapper and libs (build/winApp)

###### Build application as full jar:
`{project.path}$ ./gradlew fullJar` - create executable jar archive with all dependencies (build/distributions)
