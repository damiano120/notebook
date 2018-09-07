package notebook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import notebook.dataModel.TodoData;

import java.io.IOException;

public class Main extends Application {

//    creating scene
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Notatnik damiano");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {             // save data to .txt
        try {
            TodoData.getInstance().saveTodoItems();     // pobierz obiekt klasy TodoData i zapisz metoda save...

        } catch (IOException e){
            System.out.println(e.getMessage());        // wyswietl wyjatek
        }
    }

    @Override
    public void init() throws Exception {                 // load data from .txt
        try {
            TodoData.getInstance().loadTodoItems();     // pobierz obiekt klasy TodoData i zaladuj metoda load...

        } catch (IOException e){
            System.out.println(e.getMessage());        //wyswietl wyjatek
        }
    }
}
