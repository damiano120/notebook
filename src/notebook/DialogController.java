package notebook;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import notebook.dataModel.TodoData;
import notebook.dataModel.TodoItem;

import java.time.LocalDate;

public class DialogController {

    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;

    public TodoItem processResults(){
        String shortDescription = shortDescriptionField.getText().trim();   // przypisz pobrany tekst
        String details = detailsArea.getText().trim();      // przypisz pobrany tekst
        LocalDate deadlineValue = deadlinePicker.getValue();    // przypisz pobrany tekst

        TodoItem newItem = new TodoItem(shortDescription, details, deadlineValue);  // nowy obiekt klasy TodoItem
        TodoData.getInstance().addTodoItem(newItem);    //dodaj nowa pozycje do obiektu klasy TodoData
        return newItem;     // zwroc nowa pozycje
    }
}
