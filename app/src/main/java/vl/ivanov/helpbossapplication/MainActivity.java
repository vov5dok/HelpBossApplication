package vl.ivanov.helpbossapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonReader;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private String loginInSet = "";
    private String passwordInSet = "";

    private static final String fileProperties = "saveSettings";
    SharedPreferences settings;

    public static TextView err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String message_error = "";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().hide(); // скрываем ActionBar

        settings = getSharedPreferences(fileProperties, MODE_PRIVATE);

        readProperties();

        // Проверяем при запуске активити авторизован ли пользователь
        // если да - то сразу запускаем активити с сообщениями
        // если сервер недоступен, то сразу пишем пользователю, что сервер недоступен
        try {
            if (verificationAuth() == 1) {
                Intent intent1 = new Intent("vl.ivanov.helpbossapplication.activity_chat_window");
                startActivity(intent1);
            } else {
                // необходимо авторизоваться
            }
        } catch (IOException e) {
            // сервер недоступен
            message_error = "Извините, сервис временно недоступен";
        }

        err = (TextView) findViewById(R.id.errorAuth);
        err.setText(message_error);

    }

    // метод, который вызывается при нажатии на кнопку "Войти"
    public void loginIn(View v) throws IOException {
        String login; // логин
        String password; // пароль
        String action = "loginIn"; // action по которому сервер должен понять какую задачу необходимо решить
        String text_request = ""; // текст ответа сервера
        String text_error_user = ""; // текст ошибки для пользователя
        int kod_request = 0; // код ответа сервера (по умолчания авторизация ошибочна)

        TextView err = (TextView) findViewById(R.id.errorAuth);
        EditText loginObj = (EditText) findViewById(R.id.login); // объект поля Логин
        EditText passwordObj = (EditText) findViewById(R.id.password); // объект поля Пароль

        login = loginObj.getText().toString(); // пишем в login значение поля login
        password = passwordObj.getText().toString(); // пишем в password значение поля password
        password = Util.md5(password); // переводим обычный текст в хэшированный вид md5


        URL httpbinEndpoint = new URL("https://helpboss.site/index.php");
        HttpsURLConnection urlConnect = (HttpsURLConnection) httpbinEndpoint.openConnection();
        urlConnect.setRequestMethod("POST");
        String myData = "action=" + action + "&login=" + login + "&password=" + password;
        urlConnect.setDoOutput(true);
        urlConnect.getOutputStream().write(myData.getBytes());

        if (urlConnect.getResponseCode() == 200) {
            InputStream responseBody = urlConnect.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            jsonReader.beginObject(); // Start processing the JSON object

            while (jsonReader.hasNext()) { // Loop through all keys
                String key = jsonReader.nextName(); // Fetch the next key
                if (key.equals("request_kod")) { // Check if desired key
                    // Fetch the value as a String
                    kod_request = Integer.parseInt(jsonReader.nextString());

                    break; // Break out of the loop
                }
                if (key.equals("desc")) {
                    text_request = jsonReader.nextString();
                    break; // Break out of the loop
                } else {
                    jsonReader.skipValue(); // Skip values of other keys
                }
            }
            jsonReader.close();
            urlConnect.disconnect();

            if (kod_request == 1) { // если код ответа = 1, то успешаня авторизация

                // записываем логин, пароль в файл настроек
                SharedPreferences settings;
                settings = getSharedPreferences("saveSettings", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putString("login", login);
                prefEditor.putString("password", password);
                prefEditor.apply();
                // конец записи логина и пароля в файл настроек

                Intent intent1 = new Intent("vl.ivanov.helpbossapplication.activity_chat_window");
                startActivity(intent1);

            }

            if (kod_request == 0) { // если код ответа = 0, то Логин или пароль ввели неверно

                text_error_user = "Логин/пароль введены неверно" + text_request;

            }

            err.setText(text_error_user);

        } else {
            err.setText("Сервер временно недоступен! Попробуйте позже");
        }

    }

    /**
     * Запуск окна для регистрации аккаунта
     * @param v
     */
    public void registration(View v) {
        Intent intent = new Intent("vl.ivanov.helpbossapplication.activity_registration");
        startActivity(intent);
    }

    /**
     * Запуск окна для восстановления пароля
     * @param v
     */
    public void passwordRecovery(View v) {
        Intent intent = new Intent("vl.ivanov.helpbossapplication.password_recovery");
        startActivity(intent);
    }

    /**
     * Метод для чтения логина и пароля из файла настроек
     */
    private void readProperties() {
        loginInSet = settings.getString("login", "0");
        passwordInSet = settings.getString("password", "0");
    }

    /**
     * Метод, который возвращает авторизован ли уже пользователь
     */
    private int verificationAuth() throws IOException {
        String action = "loginIn";
        int kod_request = 0;

        URL httpbinEndpoint = new URL("https://helpboss.site/index.php");
        HttpsURLConnection urlConnect = (HttpsURLConnection) httpbinEndpoint.openConnection();
        urlConnect.setRequestMethod("POST");
        String myData = "action=" + action + "&login=" + loginInSet + "&password=" + passwordInSet;
        urlConnect.setDoOutput(true);
        urlConnect.getOutputStream().write(myData.getBytes());

        if (urlConnect.getResponseCode() == 200) {
            InputStream responseBody = urlConnect.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            jsonReader.beginObject(); // Start processing the JSON object

            while (jsonReader.hasNext()) { // Loop through all keys
                String key = jsonReader.nextName(); // Fetch the next key
                if (key.equals("request_kod")) { // Check if desired key
                    // Fetch the value as a String
                    kod_request = Integer.parseInt(jsonReader.nextString());

                    break; // Break out of the loop
                } else {
                    jsonReader.skipValue(); // Skip values of other keys
                }
            }
            jsonReader.close();
            urlConnect.disconnect();

            return kod_request;

        } else {
            return -1; // сервер временно недоступен
        }
    }

}
