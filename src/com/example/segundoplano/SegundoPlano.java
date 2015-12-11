package com.example.segundoplano;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SegundoPlano extends Activity {

	Button btnSinHilo, btnHilo, btnAsync, btnCancelarAsync, btnResetearBarra, btnDialogoBarra;
	ProgressBar barraProgreso;
	TextView txtProgreso;
	ProgressDialog dialogoBarra;
	protected TareaAsincrona tarea1;
	protected TareaAsincronaDialogo tarea2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_segundo_plano);

		btnSinHilo = (Button) findViewById(R.id.btnSinHilo);
		barraProgreso = (ProgressBar) findViewById(R.id.barraProgreso);
		btnHilo = (Button) findViewById(R.id.btnHilo);
		txtProgreso = (TextView) findViewById(R.id.txtProgreso);
		btnAsync = (Button) findViewById(R.id.btnAsync);
		btnCancelarAsync = (Button) findViewById(R.id.btnCancelarAsync);
		btnResetearBarra = (Button) findViewById(R.id.btnResetearBarra);
		btnDialogoBarra = (Button) findViewById(R.id.btnDialogoBarra);

		btnSinHilo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				barraProgreso.setMax(100);
				barraProgreso.setProgress(0);
				for(int i=1; i<=10; i++) {
					tareaLarga();
					barraProgreso.incrementProgressBy(10);
				}
				Toast.makeText(SegundoPlano.this, "Instalación completada", Toast.LENGTH_SHORT).show();
			}
		});

		btnHilo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						barraProgreso.post(new Runnable() {
							public void run() {
								barraProgreso.setProgress(0);
							}
						});			    	
						for(int i=1; i<=10; i++) {		    		
							tareaLarga();						
							barraProgreso.post(new Runnable() {
								public void run() {
									txtProgreso.setText("Descargando...");
									barraProgreso.incrementProgressBy(10);
								}
							});
						}	    	
						runOnUiThread(new Runnable() {
							public void run() {
								txtProgreso.setText("Descarga completada");
								Toast.makeText(SegundoPlano.this, "Instalación completada", Toast.LENGTH_SHORT).show();
							}
						});
					}
				}).start();
			}
		});

		/* ASYNC TASK */
		btnAsync.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				txtProgreso.setText("Iniciada Async");
				tarea1 = new TareaAsincrona();
				tarea1.execute();
			}
		});

		btnCancelarAsync.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tarea1.cancel(true);
			}
		});

		/* RESETEAR BARRA	*/
		btnResetearBarra.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				barraProgreso.setMax(100);
				barraProgreso.setProgress(0);
			}
		});


		/* DI�LOGO	*/
		btnDialogoBarra.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				dialogoBarra = new ProgressDialog(SegundoPlano.this);
				dialogoBarra.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				dialogoBarra.setMessage("Descargando...");
				dialogoBarra.setCancelable(true);
				dialogoBarra.setMax(100);	 
				tarea2 = new TareaAsincronaDialogo();
				tarea2.execute();
			}
		});

	}

	private void tareaLarga(){
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.segundo_plano, menu);
		return true;
	}



	class TareaAsincrona extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			for(int i=1; i<=10; i++) {
				tareaLarga();
				publishProgress(i*10);
				if(isCancelled())
					break;
			} 
			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
			barraProgreso.setProgress(progreso);
		}

		@Override
		protected void onPreExecute() {
			barraProgreso.setMax(100);
			barraProgreso.setProgress(0);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result)
				txtProgreso.setText("Finalizada Async");
			Toast.makeText(SegundoPlano.this, "Finalizada",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onCancelled() {
			txtProgreso.setText("Cancelada Async");
			Toast.makeText(SegundoPlano.this, "Cancelada",
					Toast.LENGTH_SHORT).show();
		}



	} // tarea Asíncrona

	class TareaAsincronaDialogo extends AsyncTask<Void, Integer, Boolean>{
		@Override
		protected Boolean doInBackground(Void... params) {

			for(int i=1; i<=10; i++) {
				tareaLarga();
				publishProgress(i*10);
				if(isCancelled())
					break;
			}

			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();

			dialogoBarra.setProgress(progreso);
		}

		@Override
		protected void onPreExecute() {
			dialogoBarra.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					TareaAsincronaDialogo.this.cancel(true);
				}
			});

			dialogoBarra.setProgress(0);
			dialogoBarra.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result)
			{
				dialogoBarra.dismiss();
				Toast.makeText(SegundoPlano.this, "Finalizada",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(SegundoPlano.this, "Cancelada",
					Toast.LENGTH_SHORT).show();
		}
	}

}

