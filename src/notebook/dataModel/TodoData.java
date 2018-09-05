package notebook.dataModel;

import javafx.collections.FXCollections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class TodoData {

    private static TodoData instance = new TodoData();
    private static String filename = "TodoFileData.txt";

    private List<TodoItem> todoItems;
    private DateTimeFormatter formatter;

    public static TodoData getInstance() {
        return instance;
    }

    private TodoData(){
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");  // wzor formatu daty
    }

    public List<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void addTodoItem(TodoItem item){
        todoItems.add(item);    // dodaj pozycje do listy
    }

    public void loadTodoItems() throws IOException{     // ladowanie pozycji z pliku
        todoItems = FXCollections.observableArrayList();    // przypisz do listy
        Path path = Paths.get(filename);    // utworz sciezki pliku
        BufferedReader bufferedReader = Files.newBufferedReader(path);  // bufor odczytu

        String input;   // zmienna pomocnicza

        try{
            while ((input = bufferedReader.readLine()) !=null){     // dopoki odczytywana linia nie jest pusta
                String[] itemPieces = input.split("\t");    // przypisz do tablicy kawalek pozycji

                String shortDescription = itemPieces[0];  // rozdziel i przypisz odczytywana linie
                String details = itemPieces[1];     // rozdziel i przypisz odczytywana linie
                String dateString = itemPieces[2];  //rozdziel i przypisz odczytywana linie

                LocalDate date = LocalDate.parse(dateString, formatter);    //zmiana formatu datty
                TodoItem todoItem = new TodoItem(shortDescription, details, date);  // zapis obiektu klasy TodoItem
                todoItems.add(todoItem);    // zapis obieku do listy pozycji

            }
        } finally {
            if (bufferedReader != null){    // jesli bufor nie jest pusty
                bufferedReader.close();     // zamknij bufor
            }
        }
    }

    public void saveTodoItems() throws IOException{     // zapis pozycji do pliku
        Path path = Paths.get(filename);    // utworz sciezke pliku
        BufferedWriter bufferedWriter = Files.newBufferedWriter(path);  // bufor zapisu
        try {
            Iterator<TodoItem> iterator = todoItems.iterator();     //iteruj liste pozycji
            while (iterator.hasNext()){     //dopoki iterator ma nastepny
                TodoItem item = iterator.next();    // iteruj pozycje
                bufferedWriter.write(String.format("%s\t%s\t%s",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDeadline().format(formatter)));     // zapisz wg wzoru
                bufferedWriter.newLine();   // nowa linia
            }
        }finally {
            if (bufferedWriter !=null){     // jeśli bufor zapisu nie jest pusty
                bufferedWriter.close();     // zamknij bufor
            }
        }
    }
}
