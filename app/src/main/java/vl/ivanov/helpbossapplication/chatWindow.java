package vl.ivanov.helpbossapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class chatWindow extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Messages> messages = new ArrayList<Messages>();
    private ArrayList<Messages> messagesNew = new ArrayList<Messages>();

    EditText msgObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        getSupportActionBar().hide(); // скрываем ActionBar

        msgObj = (EditText) findViewById(R.id.textMessageChat);

        ImageView btnOpenSettings = (ImageView) findViewById(R.id.ico_settings);
        LinearLayout btnExitSetting = (LinearLayout) findViewById(R.id.btn_exit_setting);

        btnOpenSettings.setOnClickListener(this);
        btnExitSetting.setOnClickListener(this);

    }

    public void sendMessage(View v) throws IOException {
        int idUser;
        String msg = "";
        String msgRobot = "";
        RecyclerView recyclerView;
        msg = msgObj.getText().toString(); // берем текст сообщения из поля ввода
        msgObj.setText("");

        idUser = sendMessageOnServer(msg);

        if (idUser > 0) {

            messagesNew = messages;
            messagesNew.add(new Messages(msg, idUser, messagesNew.size()));

            MsgDiffUtil msgDiffUtil = new MsgDiffUtil(messages, messagesNew);
            DiffUtil.DiffResult msgDiffResult = DiffUtil.calculateDiff(msgDiffUtil);

            recyclerView = (RecyclerView) findViewById(R.id.list_messages);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(mLinearLayoutManager);
            MsgAdapter msgAdapter = new MsgAdapter(messagesNew);

            recyclerView.setAdapter(msgAdapter);
            msgDiffResult.dispatchUpdatesTo(msgAdapter);
            recyclerView.smoothScrollToPosition(messagesNew.size());


            // на этом этапе нам нужно создать метод для отправления на сервер сообщение пользователя, его id, чтобы получить
            // ответ робота на сообщение пользователя
            msgRobot = messageRobotToUser(msg, idUser);

            messagesNew.add(new Messages(msgRobot, 1, messagesNew.size()));

            msgDiffUtil = new MsgDiffUtil(messages, messagesNew);
            msgDiffResult = DiffUtil.calculateDiff(msgDiffUtil);

            recyclerView = (RecyclerView) findViewById(R.id.list_messages);
            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(mLinearLayoutManager);
            msgAdapter = new MsgAdapter(messagesNew);

            recyclerView.setAdapter(msgAdapter);
            msgDiffResult.dispatchUpdatesTo(msgAdapter);
            recyclerView.smoothScrollToPosition(messagesNew.size());

        }
    }

    /**
     * Метод для записи сообщения пользователя в БД на сервере
     * @param message - сообщение пользователя
     * @throws IOException
     */
    private int sendMessageOnServer(String message) throws IOException {

        String action = "sendMessage";
        String login = getLoginOnClient();
        int idUser = 0; // ID пользователя, который отправил сообщение на сервер

        URL httpbinEndpoint = new URL("https://helpboss.site/index.php");
        HttpsURLConnection urlConnect = (HttpsURLConnection) httpbinEndpoint.openConnection();
        urlConnect.setRequestMethod("POST");
        String myData = "action=" + action + "&login=" + login + "&msg=" + message;
        urlConnect.setDoOutput(true);
        urlConnect.getOutputStream().write(myData.getBytes());

        if (urlConnect.getResponseCode() == 200) {
            InputStream responseBody = urlConnect.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            jsonReader.beginObject(); // Start processing the JSON object

            while (jsonReader.hasNext()) { // Loop through all keys
                String key = jsonReader.nextName(); // Fetch the next key
                if (key.equals("user_id")) { // Check if desired key
                    // Fetch the value as a String
                    idUser = Integer.parseInt(jsonReader.nextString());

                    break; // Break out of the loop
                } else {
                    jsonReader.skipValue(); // Skip values of other keys
                }
            }
            jsonReader.close();
            urlConnect.disconnect();

        } else {
            return -1;
        }

        return idUser;

    }

    /**
     * Метод для получения сообщения робота на вопрос пользователя
     * Сообщение пользователя отправляется повторно на сервер, на сервере обрабатывается и присылается ответ
     * @param msgUser - сообщение пользователя
     * @param idUser - ID пользователя
     * @return - сообщение робота, которое уже можно вставлять в массив сообщений
     */
    private String messageRobotToUser(String msgUser, int idUser) throws IOException {
        int requestCode = 777;
        String action = "sendMessageRobot";
        String msgRobot = "Сервер временно недоступен. Мы работаем над этой проблемой!";

        URL httpbinEndpoint = new URL("https://helpboss.site/index.php");
        HttpsURLConnection urlConnect = (HttpsURLConnection) httpbinEndpoint.openConnection();
        urlConnect.setRequestMethod("POST");

        String myData = "action=" + action + "&msg_user=" + msgUser + "&id_user=" + idUser;

        urlConnect.setDoOutput(true);
        urlConnect.getOutputStream().write(myData.getBytes());

        if (urlConnect.getResponseCode() == 200) {
            InputStream responseBody = urlConnect.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            jsonReader.beginObject(); // Start processing the JSON object

            while (jsonReader.hasNext()) { // Loop through all keys
                String key = jsonReader.nextName(); // Fetch the next key
                System.out.println("JSON::::::::::" + key);
                if (key.equals("request_kod")) { // Check if desired key
                    // Fetch the value as a String
                    requestCode = Integer.parseInt(jsonReader.nextString());
                    System.out.println("Код от сервера::::::::::" + requestCode);
//                    break; // Break out of the loop
                } else if (key.equals("msg_bot")) { // Check if desired key
                    // Fetch the value as a String
                    msgRobot = jsonReader.nextString();
                    System.out.println("Сообщение от сервера::::::::::" + msgRobot);
//                    break; // Break out of the loop
                } else {
                    jsonReader.skipValue(); // Skip values of other keys
                }
            }
            jsonReader.close();
            urlConnect.disconnect();

        } else {
            System.out.println("Код от сервера не 200*********************");
            return msgRobot;
        }
        System.out.println("Проверка на код от сервера прошла******************");
        return msgRobot;

    }


    /**
     * Метод для получения логина из файла настроек
     */
    private String getLoginOnClient() {
        String fileProperties = "saveSettings";
        SharedPreferences settings;
        settings = getSharedPreferences(fileProperties, MODE_PRIVATE);

        return settings.getString("login", "0");
    }


    // Отслеживаем нажатия кнопок на активити
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ico_settings: // кнопка открытия настроек

                // блокируем поле ввода сообщения
                EditText blockEditMsg = (EditText) findViewById(R.id.textMessageChat);
                blockEditMsg.setClickable(false);
                blockEditMsg.setFocusable(false);
                blockEditMsg.setLongClickable(false);

                // блокируем кнопку отправки сообщения
                Button btnSendMsg = (Button) findViewById(R.id.sendMessageChat);
                btnSendMsg.setClickable(false);

                // показываем темный фон под окном с выводом настроек
                LinearLayout blockFonSettings = (LinearLayout) findViewById(R.id.fon_wnd_settings);
                blockFonSettings.setVisibility(View.VISIBLE);

                // показываем блок с настройками
                LinearLayout blockSettings = (LinearLayout) findViewById(R.id.block_settings);
                blockSettings.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_exit_setting: // кнопка "Выход"

                // очищаем логин и пароль из файла настроек - начало
                SharedPreferences settings;
                settings = getSharedPreferences("saveSettings", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putString("login", "");
                prefEditor.putString("password", "");
                prefEditor.apply();
                // очищаем логин и пароль из файла настроек - конец

                finish(); // закрыть этот класс

                break;
        }
    }
}
