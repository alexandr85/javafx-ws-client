## ![](src/main/resources/images/icon-32.png?raw=true) *JavaFX websocket client*

###### Features:
* Connect to ws:// and wss:// websocket server
* Use custom headers for http connection
* Filtered incoming socket message
* Collected sent message history
* Save output messages to text file
* Save sessions for load them after
* Show session info in status bar

---

###### Dependencies:
* java 1.8.0_40 or later
* maven 3

---

###### Create jar package:
* execute command `{project.path}$ mvn clean assembly:assembly` 
* find it on path `{project.path}$ target/ws.client.jar`
 
###### Create mac os bundle package:
* execute command `{project.path}$ mvn clean package appbundle:bundle` 
* find it on path `{project.path}$ target/javafx-ws-client-{version}/ws.client.app`
