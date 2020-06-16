/**
 * Created on Mar 9, 2018
 * @author cskim -- hufs.ac.kr, Dept of CES
 * Copy Right -- Free for Educational Purpose
 */
package hufs.ces.digitalclock;

import java.util.Calendar;
import java.util.Formatter;

import hufs.ces.digitalclock.DigitalClockPaneThread.TimerThread;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class TwoDigitalClockPane extends BorderPane {

	private Label lblDate1;
	private Label lblDate2;
	private Button btnStart;
	
	public TwoDigitalClockPane() {
		
		initialize();
	}
	void initialize() {
		
		this.setPrefSize(500, 350);
		
		lblDate1 = new Label();
		lblDate1.setPrefWidth(250);
		lblDate1.setPrefHeight(Double.MAX_VALUE);
		lblDate1.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		lblDate1.setTextFill(Color.rgb(0, 128, 0));
		lblDate1.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
		lblDate1.setAlignment(Pos.CENTER);
		lblDate1.setTextAlignment(TextAlignment.CENTER);
		this.setLeft(lblDate1);

		lblDate2 = new Label();
		lblDate2.setPrefWidth(250);
		lblDate2.setPrefHeight(Double.MAX_VALUE);
		lblDate2.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		lblDate2.setTextFill(Color.rgb(0, 128, 0));
		lblDate2.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
		lblDate2.setAlignment(Pos.CENTER);
		lblDate2.setTextAlignment(TextAlignment.CENTER);
		this.setRight(lblDate2);
		
		btnStart = new Button("Start");
		btnStart.setPrefHeight(50);
		btnStart.setOnAction(e->startAction());	
		btnStart.setPrefWidth(Double.MAX_VALUE);
		this.setBottom(btnStart);

	}
	void startAction() {
		setTimeDisplay1();
	    // Create an animation for a running clock
	    Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), e->setTimeDisplay1()));
	    animation.setCycleCount(Timeline.INDEFINITE);
	    animation.play(); // Start animation

		setTimeDisplay2();
		Thread th = new TimerThread();
		th.setDaemon(true);
		th.start();
	}
	
	void setTimeDisplay1(){
		
		Calendar calendar = Calendar.getInstance();

		StringBuilder dateText = new StringBuilder();
		Formatter buildDate = new Formatter(dateText);
		buildDate.format("%1$tY.%1$tm.%1$td", calendar);
		lblDate1.setText(dateText.toString());
	}

	void setTimeDisplay2(){

		Platform.runLater(()->{
			Calendar calendar = Calendar.getInstance();

			StringBuilder dateText = new StringBuilder();
			Formatter buildDate = new Formatter(dateText);
			buildDate.format("%1$tY.%1$tm.%1$td", calendar);
			lblDate2.setText(dateText.toString());
		});
	}
	class TimerThread extends Thread {

		@Override
		public void run() {
			while (true){
				setTimeDisplay2();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}