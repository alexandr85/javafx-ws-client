## <img src="application.ico" width="32" height="32"> *JavaFX websocket client*

###### Features:
* Connect to ws:// and wss:// websocket server
* Use custom headers for http connection
* Filtered incoming socket message
* Collected sent message history
* Save output messages to text file
* Save sessions for load them after
* Show current session info in status bar
* Show socket message new tab
* Pretty json socket message in new tab
* Send messages list after connection

---

###### Dependencies:
* java 1.8 or later

---
 
###### Create packages (use [maven]):
* execute command `{project.path}$ mvn clean package` 
* jar package path `{project.path}$ target/WSClient.jar`
* win os package path `{project.path}$ target/WSClient.exe`
* mac os package path `{project.path}$ target/WSClient.app`

###### Create mac os dmg archive:
* execute command in target folder `hdiutil create -srcfolder WSClient.app WSClient.dmg`

[maven]: https://maven.apache.org
