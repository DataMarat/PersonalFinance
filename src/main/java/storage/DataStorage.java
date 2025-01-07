package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class DataStorage {
    private static final String FILE_PATH = "src/main/resources/finance_data.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Сохранение данных
    public static void saveData(List<User> users) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            String json = GSON.toJson(users);
            writer.write(json);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Загрузка данных
    public static List<User> loadData() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<List<User>>() {}.getType();
            return GSON.fromJson(reader, listType);
        } catch (FileNotFoundException e) {
            System.out.println("No data file found. Starting fresh.");
            return null;
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
            return null;
        }
    }
}
