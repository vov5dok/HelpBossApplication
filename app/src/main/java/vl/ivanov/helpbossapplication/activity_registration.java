package vl.ivanov.helpbossapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.JsonReader;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class activity_registration extends AppCompatActivity {

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().hide(); // скрываем ActionBar
    }

    @SuppressLint("ResourceAsColor")
    public void buttonRegistration(View view) throws IOException {
        String login;
        String password;
        String email;
        boolean z = true;

        EditText loginObj = (EditText) findViewById(R.id.loginReg);
        EditText passwordObj1 = (EditText) findViewById(R.id.passwordReg);
        EditText passwordObj2 = (EditText) findViewById(R.id.passwordRepeatReg);
        EditText emailObj = (EditText) findViewById(R.id.emailReg);
        TextView errorObj = (TextView) findViewById(R.id.errorRegistration);

        Drawable bgDefoult = loginObj.getBackground();

        if (!loginObj.getText().toString().matches("^[a-zA-Z0-9-_]+$")) {
            errorObj.setText("Логин должен содержать латиницу, числа, тире или знак подчеркивания");
            loginObj.setBackgroundResource(R.drawable.red_border_bottom);
            z = false;
        } else {
            errorObj.setText("");
            loginObj.setBackgroundResource(R.drawable.black_border_bottom);
            z = true;
        }

        if (passwordObj1.getText().toString().equals(passwordObj2.getText().toString())) {
            errorObj.setText("");
            passwordObj1.setBackgroundResource(R.drawable.black_border_bottom);
            z = true;
        } else {
            errorObj.setText("Пароли не сопадают");
            passwordObj1.setBackgroundResource(R.drawable.red_border_bottom);
            passwordObj2.setBackgroundResource(R.drawable.red_border_bottom);
            z = false;
        }

        if (z) {
            // если z = true, то необходимо данные с формы отправить на сервер для записи данных в БД
            // когда запись в БД произойдет успешно, то открывать форму для авторизации

            int request = registrationOnServer(loginObj.getText(), passwordObj1.getText(), emailObj.getText());

            if (request == 0) { // если код ответа = 0, то регистрация успешная
                // вызов диалогового окна - начало
                dialog = new Dialog(this); // создаем новое диалоговое окно
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // скрываем заголовок
                dialog.setContentView(R.layout.dialog_registration); // путь к макету диалогового окна
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // прозрачный фон диалогового окна
                dialog.setCancelable(false); // окно нельзя закрыть кнопкой "Назад"

                // кнопка "Авторизация" - начало
                Button btncontinue = (Button) dialog.findViewById(R.id.dialog_btn_continue);
                btncontinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // обрабатываем нажатие кнопки - начало
                        finish(); // закрыть этот класс
                        // обрабатываем нажатие кнопки - конец

                        dialog.dismiss(); // закрываем диалоговое окно
                    }
                });
                // кнопка "Авторизация" - конец

                dialog.show(); // показать даилоговое окно
                // вызов диалогового окна - конец
            } else if (request == 1) {
                errorObj.setText("Сервер временно недоступен. Попробуйте позднее =(");
            } else if (request == 2) {
                errorObj.setText("Сервер временно недоступен. Попробуйте позднее =(");
            } else if (request == 3) {
                errorObj.setText("Сервер временно недоступен. Попробуйте позднее =(");
            } else if (request == 4) {
                errorObj.setText("Такой логин уже существует. Подберите другой!");
            } else if (request == 5) {
                errorObj.setText("Сервер временно недоступен. Попробуйте позднее =(");
            }
        }
    }

    public int registrationOnServer(Editable login, Editable password, Editable email) throws IOException {
        String action = "registrationUser";
        int kod_request = 1;

        URL httpbinEndpoint = new URL("https://helpboss.site/index.php");
        HttpsURLConnection urlConnect = (HttpsURLConnection) httpbinEndpoint.openConnection();
        urlConnect.setRequestMethod("POST");
        String myData = "action=" + action + "&login=" + login + "&password=" + password + "&email=" + email;
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
            return 5; // сервер временно недоступен
        }

    }
}