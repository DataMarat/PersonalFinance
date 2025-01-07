# Personal Finance Manager

**Personal Finance Manager** — консольное приложение для управления личными финансами. С его помощью вы сможете отслеживать доходы и расходы, устанавливать лимиты по категориям, а также управлять своим кошельком.


## Возможности

- **Регистрация и авторизация пользователей:**
    - Каждый пользователь имеет уникальный идентификатор (UUID).
    - Авторизация по логину и паролю.
- **Управление категориями:**
    - Добавление категорий для расходов.
    - Установка лимитов по категориям.
- **Работа с кошельком:**
    - Привязка кошелька к пользователю.
    - Добавление доходов и расходов.
    - Переводы между кошельками разных пользователей.
- **Статистика:**
    - Общая сумма доходов и расходов.
    - Расходы по категориям.
    - Остаток лимитов по категориям.
- **Сохранение данных:**
    - Все данные сохраняются в файл `finance_data.json`.
    - Данные автоматически загружаются при запуске.


## Установка и запуск

1. Скачайте проект.
2. Убедитесь, что у вас установлен **Java 11** или выше.
3. Запустите проект через терминал с помощью следующих команд:
   ```bash
   javac -d bin src/main/java/app/Main.java
   java -cp bin app.Main
    ```
4. Следуйте инструкциям в меню.

## Команды

После запуска приложения вы увидите меню. Вот список доступных команд:

### Главное меню:
- `r` - Регистрация нового пользователя
- `l` - Вход в систему
- `o` - Выход из текущего аккаунта
- `c` - Создать кошелёк
- `w` - Перейти в кошелёк
- `a` - Добавить категорию
- `v` - Просмотреть категории
- `t` - Перевести деньги другому пользователю
- `+` - Добавить доход
- `-` - Добавить расход
- `f` - Фильтрация операций
- `s` - Просмотр статистики
- `q` - Выход из приложения

## Как пользоваться

1. **Регистрация:**
    - Выберите `r` для регистрации нового пользователя.
    - Введите логин и пароль.
2. **Создание кошелька:**
    - После входа в систему выберите `c` для создания кошелька.
3. **Добавление категорий:**
    - Используйте `a`, чтобы добавить категории, например, "Еда", "Транспорт".
4. **Добавление операций:**
    - Используйте `+` для добавления доходов и `-` для расходов.
5. **Переводы:**
    - Выберите `t` для перевода денег другому пользователю. Укажите UUID получателя и сумму.
6. **Статистика:**
    - Выберите `s`, чтобы просмотреть доходы, расходы и остатки лимитов.

## Сохранение данных

- При каждом изменении данных они автоматически сохраняются в файл `finance_data.json`.
- Данные загружаются при следующем запуске программы.

## Пример работы

### Ввод данных:

   ```bash
    Enter your choice: `r`  
    Enter login: `ivan`  
    Enter password: `password123`  
    **Registration successful!** You are now logged in as: `ivan`, your ID: `da0aa159-beba-403f-a9f7-7e481c95fb24`
    
    Enter your choice: `c`  
    **Wallet created successfully!**
    ```

### Вывод статистики:

   ```bash
    Total Income: `5000.0`  
    Total Expense: `2000.0`
    
    Category Budgets:
    - **Food:** Budget `4000.0`, Remaining: `3000.0`
    - **Transport:** Budget `2000.0`, Remaining: `2000.0`
    ```


## Ограничения

- Данные о пользователях и кошельках хранятся в одном файле `finance_data.json`. Файл сгенерируется при старте и первых пользователях.
- Переводы между кошельками возможны только у пользователей с активными кошельками.
