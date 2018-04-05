package kotcity.ui

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotcity.data.CityFileAdapter
import tornadofx.View
import tornadofx.find
import tornadofx.runLater
import java.io.File
import java.util.function.ToIntFunction

object CityLoader {
    fun loadCity(view: View) {
        val fileChooser = FileChooser()
        fileChooser.title = "Load a city"
        // fileChooser.initialDirectory = File(System.getProperty("user.home"))
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("KotCity Data", "*.kcity")
        )
        runLater {
            val file = fileChooser.showOpenDialog(view.currentStage)
            if (file != null) {

                // let's use the dialog... :)
                view.currentWindow?.let { window ->
                    val workDialog = WorkIndicatorDialog<File>(window, "Loading City...")

                    val launchScreen = find(LaunchScreen::class)
                    launchScreen.close()
                    view.close()
                    view.currentStage?.close()
                    view.primaryStage.close()

                    workDialog.exec(file, func = ToIntFunction<File> {
                        val map = CityFileAdapter.load(file)
                        runLater {
                            val gameFrame = tornadofx.find(GameFrame::class)
                            gameFrame.setMap(map)
                            gameFrame.currentStage?.isMaximized = true
                            gameFrame.openWindow()
                            println("Gameframe should be open at this point...")
                            gameFrame.currentStage?.isMaximized = true
                            // clean up orphaned objects....
                            System.gc()
                        }
                        1
                    })
                }
            } else {
                val alert = Alert(AlertType.ERROR)
                alert.title = "Error during load"
                alert.headerText = "Could not load your city!"
                alert.contentText = "Why not? Totally unknown?"
                val stage = alert.dialogPane.scene.window as Stage
                stage.isAlwaysOnTop = true
                stage.toFront() // not sure if necessary

                alert.showAndWait()
            }
        }
    }
}