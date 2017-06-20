package pe.chalk.meal;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Controller {
    @FXML private DatePicker calendar;

    @FXML @SuppressWarnings("unused") private ListView<String> breakfast;
    @FXML @SuppressWarnings("unused") private ListView<String> lunch;
    @FXML @SuppressWarnings("unused") private ListView<String> dinner;
    @FXML @SuppressWarnings("unused") private ListView<String> snack;

    @FXML public void initialize() {
        calendar.setValue(LocalDate.now());
        calendar.setConverter(new DateConverter());
    }

    public void search(@SuppressWarnings("unused") final ActionEvent event) {
        try {
            final Instant instant = calendar.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant();
            final Map<String, List<String>> meal = Fetcher.getMeal(Date.from(instant));

            Stream.of(getClass().getDeclaredFields())
                    .filter(field -> field.getType().equals(ListView.class))
                    .forEach(field -> updateList(meal, field));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void updateList(Map<String, List<String>> meal, Field field) {
        try {
            final ListView<String> listView = (ListView<String>) field.get(this);
            final List<String> list = meal.getOrDefault(field.getName(), Collections.singletonList("Not available."));

            listView.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
