## <img src="application.ico" width="32" height="32"> *JavaFX Websocket client*

###### Features:
* Connect to ws:// and wss:// websocket server
* Use custom headers for http connection
* Filtered incoming websocket message
* Collected sent message history
* Save output messages to text file
* Save sessions for load them after
* Show current session info in status bar
* Show websocket message in new tab
* Pretty json websocket message in new tab
* Send selected messages list after connect

---

###### Dependencies:
* java 1.8 or later

---
 
###### Build application for all os:
`{project.path}$ ./gradlew create` - create jar, zip (for windows), dmg (for mac) (build/distributions)

###### Build application for MacOS:
`{project.path}$ ./gradlew createApp` - create mac bundle (build/macApp)

###### Build application for Windows:
`{project.path}$ ./gradlew createExe` - create windows wrapper and libs (build/winApp)

###### Build application as full jar:
`{project.path}$ ./gradlew fullJar` - create executable jar archive with all dependencies (build/distributions)
