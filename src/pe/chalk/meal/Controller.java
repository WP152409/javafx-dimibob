package pe.chalk.meal;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Controller {
    @FXML private StackPane stack;
    @FXML private DatePicker calendar;

    @FXML @SuppressWarnings("unused") private ListView<String> breakfast;
    @FXML @SuppressWarnings("unused") private ListView<String> lunch;
    @FXML @SuppressWarnings("unused") private ListView<String> dinner;
    @FXML @SuppressWarnings("unused") private ListView<String> snack;

    private ProgressIndicator progress;

    @FXML public void initialize() {
        progress = new ProgressIndicator();
        StackPane.setMargin(progress, new Insets(50));

        calendar.setValue(LocalDate.now());
        calendar.setConverter(new DateConverter());

        search(null);
    }

    public void search(@SuppressWarnings("unused") final ActionEvent event) {
        stack.getChildren().add(progress);

        new Thread(() -> {
            try {
                final LocalDate date = calendar.getValue();
                if (Objects.isNull(date)) return;

                final Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
                final Map<String, List<String>> meal = Fetcher.getMeal(Date.from(instant));

                Platform.runLater(() -> Stream.of(getClass().getDeclaredFields())
                        .filter(field -> field.getType().equals(ListView.class))
                        .forEach(field -> updateList(meal, field)));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).show());
            } finally {
                Platform.runLater(() -> stack.getChildren().remove(progress));
            }
        }).start();
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
