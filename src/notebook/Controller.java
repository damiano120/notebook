package notebook;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import notebook.dataModel.TodoData;
import notebook.dataModel.TodoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Controller {

    private List<TodoItem> todoItems;

    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadline;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;


    public void initialize(){

        listContextMenu = new ContextMenu();        // nowe menu kontekstowe
        MenuItem deleteMenuItem = new MenuItem("Delete");   // przycisk "delete" w menu kontekstowym
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {    //wykonaj akcje po kliknieciu "delete"
            @Override
            public void handle(ActionEvent event) {
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();     // wybierz pozycje
                deleteItem(item);       // skasuj pozycje
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);

        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
                if(newValue != null) {
                    TodoItem item = todoListView.getSelectionModel().getSelectedItem();     // wybierz pozycje
                    itemDetailsTextArea.setText(item.getDetails());                // wyswietl szczegoly wiadomosci
                    DateTimeFormatter df = DateTimeFormatter.ofPattern(" dd MMMM yyyy");    // ustaw date
                    deadline.setText(df.format(item.getDeadline()));                         // wyswietl date
                }
            }
        });

//        SORTOWANIE LISTY
        SortedList<TodoItem> sortedList = new SortedList<TodoItem>(TodoData.getInstance().getTodoItems(),
                new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem o1, TodoItem o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });

//        todoListView.setItems(TodoData.getInstance().getTodoItems());       // wyswietl liste pozycji bez sortowania
        todoListView.setItems(sortedList);      // wyswietl posortowana liste pozycji
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);    // tryb wyboru pojedynczy
        todoListView.getSelectionModel().selectFirst();     // zaznacz pierwszy

        todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> param) {
                ListCell<TodoItem> cell = new ListCell<>(){

                    @Override
                    protected void updateItem(TodoItem item, boolean empty){
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else{
                            setText(item.getShortDescription());        // ustaw tekst "short description"
                            if (item.getDeadline().isBefore(LocalDate.now().plusDays(1))){ // jesli wczoraj i dalej
                                setTextFill(Color.RED);         // ustaw tekst na czerwono
                            } else if (item.getDeadline().equals(LocalDate.now().plusDays(1))){ // jesli jutro i dalej
                                setTextFill(Color.BLUE);        // ustaw tekst na niebiesko
                            }
                        }
                    }
                };

//      KASOWANIE POZYCJI
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) ->{
                            if (isNowEmpty){
                                cell.setContextMenu(null);
                            } else{
                                cell.setContextMenu(listContextMenu);
                            }
                        });

                return cell;
            }
        });
    }

//    TWORZENIE NOWEJ NOTATKI
    @FXML
    public void showNewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();                              // nowe okno "Dialog"
        dialog.initOwner(mainBorderPane.getScene().getWindow());                 // ustawienia okna
        dialog.setTitle("Add New Todo Item");                                    // ustaw tytul okna
        dialog.setHeaderText("Use this dialog to create a new todo item");      // ustaw naglowek
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        }catch (IOException e){                                  // obsluga bledow
            System.out.println("Couldn't load the dialog");     // obsluga bledow
            e.printStackTrace();                                // obsluga bledow
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);          // dodaj przycisk OK
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);     // dodaj przycisk CANCEL

        Optional<ButtonType> result = dialog.showAndWait();     //
        if (result.isPresent() && result.get() == ButtonType.OK){
            DialogController controller = fxmlLoader.getController();
            TodoItem newItem = controller.processResults();
            todoListView.getSelectionModel().select(newItem);
        }
    }

//    KASUJ POZYCJE Z LISTY PRZYCISKIEM DELETE NA KLAWIATURZE
    public  void handleKeyPressed(KeyEvent keyEvent){       // kasowanie pozycji przyciskiem na klawiaturze
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();     // wybierz pozycje z listy
        if (selectedItem !=null){       // jesli jest wybrana pozycja
            if (keyEvent.getCode().equals(KeyCode.DELETE)){     // po wcisnieciu przycisku DELETE
                deleteItem(selectedItem);       // kasuj wybrana pozycje
            }
        }
    }

    @FXML
    public void clickListView(){
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();     // wybierz pozycje z listy
        itemDetailsTextArea.setText(item.getDetails());         // ustaw tekst "details" w obszarze tekstowym
        deadline.setText(item.getDeadline().toString());        // ustaw date jako tekst "deadline"
    }

//    KASUJ POZYCJE Z LISTY
    public void deleteItem(TodoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);  // pokaz okno z potwierdzeniem kasowania
        alert.setTitle("Delete note");            //ustaw tytul okna
        alert.setHeaderText("Delete item: "+item.getShortDescription());    // ustaw naglowek okna
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back.");       // ustaw tekst
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && (result.get() == ButtonType.OK)){      // warunki wykonania kasowania
            TodoData.getInstance().deleteTodoItem(item);        // skasuj pozycje
        }
    }
}
