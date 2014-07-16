package br.usp.icmc.lrm.telemetria;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.usp.icmc.lrm.gmarques.telemetria.R;
import br.usp.icmc.lrm.gmarques.telemetria.R.id;
import br.usp.icmc.lrm.telemetria.network.ClientConnection;
import br.usp.icmc.lrm.telemetria.network.Server;

public class Telemetria extends Activity {

	TextView accText = null;
	Server server = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		accText = (TextView) findViewById(R.id.hello);
		Button captureButton = (Button) findViewById(id.button_capture);

        captureButton.setOnClickListener(
            new View.OnClickListener() {
            	private ClientConnection connection;
            	@Override
	            public void onClick(View v) {
	            	this.connection = new ClientConnection();
	            	try {
						this.connection.connect("localhost:5050");
						this.connection.sendString("000000 hlo 0001\n\r");
						this.connection.receiveString();
						this.connection.sendString("000000 off gps 0\n\r");
						this.connection.receiveString();
						this.connection.sendString("000000 off networklocation 0\n\r");
						this.connection.receiveString();
						this.connection.sendString("000000 off cameras 0\n\r");
						this.connection.receiveString();
						this.connection.sendString("000000 off battery 0\n\r");
						this.connection.receiveString();
						this.connection.sendString("000000 off accelerometer 0\n\r");
						this.connection.receiveString();
						this.connection.sendString("000000 cls 0\n\r");
						this.connection.receiveString();
						this.connection = null;
						Toast.makeText(getBaseContext(),
								"Tira a mão do meu botão", Toast.LENGTH_SHORT).show();
						accText.setText("OK");
					} catch (Exception e) {
						Log.e("Telemetria", "Error on button: " + e.getMessage());
					}
	            }
            }
        );
		try {
			server = new Server(5050, getApplicationContext());
			accText.setText("ServerUP");
		} catch (IOException e) {
			Log.e("Telemetria", "Error on Server: " + e.getMessage());
			accText.setText(e.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (this.server != null) {
			this.server.stop();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}