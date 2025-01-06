package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private static final String FILE_PATH = "src/main/resources/finance_data.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Сохранение данных
    public static void saveData(List<User> users) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            GSON.toJson(users, writer);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Загрузка данных
    public static List<User> loadData() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            User[] users = GSON.fromJson(reader, User[].class);
            if (users != null) {
                return new ArrayList<>(List.of(users)); // Конвертируем в изменяемый список
            } else {
                return new ArrayList<>(); // Возвращаем пустой список, если файл пуст
            }
        } catch (IOException e) {
            System.out.println("No previous data found or error reading file. Starting fresh.");
            return new ArrayList<>(); // Возвращаем пустой список при отсутствии файла
        }
    }
}
