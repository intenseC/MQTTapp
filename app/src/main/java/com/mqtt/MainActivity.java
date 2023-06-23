package com.mqtt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mqtt.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.java_websocket.WebSocket;

public class MainActivity extends AppCompatActivity  {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final String MQTT_BROKER_0 = "tcp://192.168.1.3:1883";
    private static final String MQTT_BROKER_1 = "tcp://10.0.0.1:1883";
    private static final String MQTT_BROKER2 = "tcp://192.168.11.17:1883";
    private static  String MQTT_BROKER = "tcp://192.168.11.17:1883";
    private static  String topic = "mqtt_test";

    private static  String message = "messaging_test";
    private static String userFields[] = new String[3];




    private MqttAsyncClient mqttClient;
    private webSocketServer webSocketSvr;



    public void getUserInput() {
        EditText editText0 = findViewById(R.id.editText0);
        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        userFields[0] = MQTT_BROKER;
        userFields[1] = topic;
        userFields[2] = message;
        String textValue[] = new String[3];
        textValue[0] = editText0.getText().toString();
        textValue[1] = editText1.getText().toString();
        textValue[2] = editText2.getText().toString();

        for(int i = 0; i < textValue.length; i++) {
            if (!textValue[i].isEmpty()) {
                userFields[i] = textValue[i];
            }
        }
    }



    public void onButtonClicked() {
        scrPrint("MQTT ASYNC TESTING");
//        getUserInput();
        MQTTAsyncTest();
//      MQTTBlockingTest();
    }


    private void scrPrint(String dat) {
        TextView textview_first = findViewById(R.id.textview_first);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textview_first.setText(dat);
            }
        });
            webSocketSvr.broadcast(dat);
    }




    private void MQTTBlockingTest() {
        try {
            MqttClient mqttClient = new MqttClient(MQTT_BROKER, MqttClient.generateClientId(),  new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            mqttClient.connect(options);

            if (mqttClient.isConnected()) {
                System.out.println("Connected to MQTT broker");
                scrPrint("Connected to MQTT broker");
                // Subscribe to a topic
                mqttClient.subscribe(topic);

                // Publish a message
                String message = "Hello, MQTT!";
                mqttClient.publish(topic, new MqttMessage(message.getBytes()));

                // Disconnect from the broker
                mqttClient.disconnect();
                System.out.println("Disconnected from MQTT broker");
                scrPrint("Disconnected from MQTT broker");
            } else {
                System.out.println("Failed to connect to MQTT broker");
                scrPrint("Failed to connect to MQTT broker");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private void MQTTAsyncTest() {
        getUserInput();
        MemoryPersistence memoryPersistence = new MemoryPersistence();

        try {
            MqttAsyncClient mqttAsyncClient = new MqttAsyncClient(userFields[0], MqttClient.generateClientId(), memoryPersistence);
            mqttAsyncClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    scrPrint("Disconnected");
                    // on connection lost event
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    scrPrint("Got new message");
                    // incoming messages
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    scrPrint("Message Delivered");
                    // Handle 'message delivery complete' event
                }
            });

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            mqttAsyncClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    scrPrint("Connected to MQTT broker");
                    // Connection success
                    // further MQTT operations here
                    try {
                        MqttMessage mqttMessage = new MqttMessage(userFields[2].getBytes());
                        mqttAsyncClient.publish(userFields[1], mqttMessage);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    scrPrint("Failed to connect to MQTT broker");
                    // Connection failed
                    // Handle the failure scenario
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        webSocketSvr = new webSocketServer(8899) {
            @Override
            public void onMessage(WebSocket conn, String message) {
                scrPrint(message);
            }
        };
        webSocketSvr.start();

        System.out.println("Starting MQTT ");
        scrPrint("Starting MQTT client...");
//        MQTTBlockingTest();
        MQTTAsyncTest();


        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
/*
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}