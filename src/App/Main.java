package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            //Σύνδεση με τη SQLLite. Αν δεν έχει δημιουργηθεί πίνακας με τα στοιχεία τότε
            //δημιουργείτε αυτόματα ένας
            db.Database.initialize();
            //Εισαγωγή του αρχείου fxml σαν root
            Parent root = FXMLLoader.load(
                    getClass().getResource("/ui/library.fxml")
            );


            //Εισαγωγή του root στη σκηνή
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            //Εισαγωγή τίτλου
            primaryStage.setTitle("Library App");

            //Εισαγωγή της φωτογραφίας σαν logo του προγράμματος
            Image logo = new Image("/ui/images/LibraryLogo.png");
            primaryStage.getIcons().add(logo);

            //Εισαγωγή κώδικα Css στο JavaFX
            scene.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());

            //Εισαγωγή διαστάσεων παραθύρου
            primaryStage.setWidth(1280);
            primaryStage.setHeight(800);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
