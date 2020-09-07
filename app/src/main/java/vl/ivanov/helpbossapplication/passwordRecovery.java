package vl.ivanov.helpbossapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class passwordRecovery extends AppCompatActivity {

    private int idUser = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_recovery);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().hide(); // скрываем ActionBar
    }

    /**
     * Нажали на кнопку Далее, когда ввели email
     * @param view
     */
    public void sendEmailToServer(View view) throws IOException {
        int kod_request = 0;
        String idd = "";
        String action = "passwordRecovery";
        EditText emailObj = (EditText) findViewById(R.id.emailPassRec);

        URL httpbinEndpoint = new URL("https://helpboss.site/index.php");
        HttpsURLConnection urlConnect = (HttpsURLConnection) httpbinEndpoint.openConnection();
        urlConnect.setRequestMethod("POST");
        String myData = "action=" + action + "&email=" + emailObj.getText() + "&method=getUserEmail";
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

//                    break; // Break out of the loop
                } else if (key.equals("id_user")) { // Check if desired key
                    // Fetch the value as a String
                    idUser = Integer.parseInt(jsonReader.nextString());

//                    break; // Break out of the loop
                } else {
                    jsonReader.skipValue(); // Skip values of other keys
                }
            }
            jsonReader.close();
            urlConnect.disconnect();

            if (kod_request == 0) {
                TextView errorObj = (TextView) findViewById(R.id.errorPasswordRecovery);
                errorObj.setText("Такого email не существует. Проверьте поле с email");
            } else {
                System.out.println(idUser);
                // скрываем поле ввода email и кнопку "Далее" для него
                LinearLayout fieldEmailObj = (LinearLayout) findViewById(R.id.containerEmailPassRec);
                fieldEmailObj.setVisibility(View.GONE);

                LinearLayout btnOneObj = (LinearLayout) findViewById(R.id.containerButtonPassEmail);
                btnOneObj.setVisibility(View.GONE);

                // показываем описание поля ввода кода, поля ввода кода и кнопку "Далее"
                TextView descCodeObj = (TextView) findViewById(R.id.descriptionPasswordRecovery);
                descCodeObj.setVisibility(View.VISIBLE);

                LinearLayout fieldCodeObj = (LinearLayout) findViewById(R.id.containerCodePassRec);
                fieldCodeObj.setVisibility(View.VISIBLE);

                LinearLayout btnTwoObj = (LinearLayout) findViewById(R.id.containerButtonPassCode);
                btnTwoObj.setVisibility(View.VISIBLE);
            }

        } else {
            TextView errorObj = (TextView) findViewById(R.id.errorPasswordRecovery);
            errorObj.setText("Сервер временно недоступен :-(  Разработчики устраняют эту проблему");
        }

    }

    /**
     * Нажали кнопку Далее после ввода проверочного кода
     * @param view
     * @throws IOException
     */
    public void sendCodeToServer(View view) throws IOException {
        int kod_request = 0;
        String action = "passwordRecovery";
        EditText codeObj = (EditText) findViewById(R.id.codePassRec);

        URL httpbinEndpoint = new URL("https://helpboss.site/index.php");
        HttpsURLConnection urlConnect = (HttpsURLConnection) httpbinEndpoint.openConnection();
        urlConnect.setRequestMethod("POST");
        String myData = "action=" + action + "&method=getRecCode" + "&code=" + codeObj.getText() + "&idUser=" + idUser;
        System.out.println(myData);
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

            if (kod_request == 0) {
                TextView errorObj = (TextView) findViewById(R.id.errorPasswordRecovery);
                errorObj.setText("Код введен неверно");
            } else {

                // скрываем описание поля ввода кода, поля ввода кода и кнопку "Далее"
                TextView descCodeObj = (TextView) findViewById(R.id.descriptionPasswordRecovery);
                descCodeObj.setVisibility(View.GONE);

                LinearLayout fieldCodeObj = (LinearLayout) findViewById(R.id.containerCodePassRec);
                fieldCodeObj.setVisibility(View.GONE);

                LinearLayout btnTwoObj = (LinearLayout) findViewById(R.id.containerButtonPassCode);
                btnTwoObj.setVisibility(View.GONE);

                // показываем поля ввода паролей и кнопку Сохранить
                LinearLayout fieldPassOneObj = (LinearLayout) findViewById(R.id.containerPasswordRecovery);
                fieldPassOneObj.setVisibility(View.VISIBLE);

                LinearLayout fieldPassTwoObj = (LinearLayout) findViewById(R.id.containerPasswordRepeatRecovery);
                fieldPassTwoObj.setVisibility(View.VISIBLE);

                LinearLayout btnSavePassword = (LinearLayout) findViewById(R.id.containerButtonPasswordNew);
                btnSavePassword.setVisibility(View.VISIBLE);
            }

        } else {
            TextView errorObj = (TextView) findViewById(R.id.errorPasswordRecovery);
            errorObj.setText("Сервер временно недоступен :-(  Разработчики устраняют эту проблему");
        }
    }

    /**
     * Нажали кнопку Сохранить, когда ввели новые пароли
     * @param view
     * @throws IOException
     */
    public void sendPasswordToServer(View view) throws IOException {
        int kod_request = 0;
        String action = "passwordRecovery";
        EditText passObj = (EditText) findViewById(R.id.passwordRecovery);
        EditText passRepeatObj = (EditText) findViewById(R.id.passwordRepeatRecovery);
        TextView errorObj = (TextView) findViewById(R.id.errorPasswordRecovery);

        if (passObj.getText().toString().equals(passRepeatObj.getText().toString())) {
            errorObj.setText("");

            URL httpbinEndpoint = new URL("https://helpboss.site/index.php");
            HttpsURLConnection urlConnect = (HttpsURLConnection) httpbinEndpoint.openConnection();
            urlConnect.setRequestMethod("POST");
            String myData = "action=" + action + "&method=recoveryPasswordUser" + "&password=" + passObj.getText() + "&idUser=" + idUser;
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

                if (kod_request == 0) {
                    errorObj = (TextView) findViewById(R.id.errorPasswordRecovery);
                    errorObj.setText("Придумайте другой пароль");
                } else {

                    // закрываем эту активити
                    finish();
                }

            } else {
                errorObj.setText("Сервер временно недоступен :-(  Разработчики устраняют эту проблему");
            }

        } else {
            errorObj.setText("Пароли не сопадают");
            passObj.setBackgroundResource(R.drawable.red_border_bottom);
            passRepeatObj.setBackgroundResource(R.drawable.red_border_bottom);
        }


    }
}