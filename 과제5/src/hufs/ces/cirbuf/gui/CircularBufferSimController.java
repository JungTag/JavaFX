package hufs.ces.cirbuf.gui;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import hufs.ces.cirbuf.CircularBuffer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CircularBufferSimController extends AnchorPane {

	private final static int DEFAULT_BUFFER_COUNT = 10;
	private static boolean isopen = true;
	
	public Stage parentStage = null;

	volatile CircularBuffer<String> cirbuf1 = null;
	volatile CircularBuffer<String> cirbuf2 = null;
	volatile CircularBuffer<String> cirbuf3 = null;
	volatile Iterator<BigInteger> fibGen = null;
	BufferShape[] bufShapes1 = null;
	BufferShape[] bufShapes2 = null;
	BufferShape[] bufShapes3 = null;
	ArrayList<String> writeList = new ArrayList<>();
	ArrayList<String> readList = new ArrayList<>();
	
	//volatile IntegerProperty bufUseCount = new SimpleIntegerProperty(0);
	
	
    @FXML
    private AnchorPane root;

    @FXML
    private Button btnStart;

    @FXML
    private Pane drawPane;
    
    @FXML
    private Label prod_Count;

    @FXML
    private Label cons_Count;
    
    @FXML
    private Label write_Data;

    @FXML
    private Label read_Data;

    @FXML
    void handleBtnStart(ActionEvent event) {
    	initCircularBuffer1(DEFAULT_BUFFER_COUNT);
    	initCircularBuffer2(DEFAULT_BUFFER_COUNT);
    	initCircularBuffer3(DEFAULT_BUFFER_COUNT);
		Thread prod1 = new Thread(new ProducerTask());
		prod1.start();
		Thread cons1 = new Thread(new ConsumerTask1());
		Thread cons2 = new Thread(new ConsumerTask2());
		cons1.start();
		cons2.start();
		Thread cons3 = new Thread(new ConsumerTask3());
		cons3.start();
    }
    
	public CircularBufferSimController() {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cirbuf.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initialize();
	}

	private void initialize() {
		//fibGen = new BigFibonacciIterator();
		fibGen = new BigIntegerIterator();
		
	}
	void initCircularBuffer1(int siz) {
    	//bufUseCount = new SimpleIntegerProperty(0);
		cirbuf1 =  new CircularBuffer<String>(siz);
		bufShapes1 = new BufferShape[cirbuf1.getBufSize()];
		buildCircularBufferShape1();
	}
	void initCircularBuffer2(int siz) {
    	//bufUseCount = new SimpleIntegerProperty(0);
		cirbuf2 =  new CircularBuffer<String>(siz);
		bufShapes2 = new BufferShape[cirbuf2.getBufSize()];
		buildCircularBufferShape2();
	}
	void initCircularBuffer3(int siz) {
    	//bufUseCount = new SimpleIntegerProperty(0);
		cirbuf3 =  new CircularBuffer<String>(siz);
		bufShapes3 = new BufferShape[cirbuf3.getBufSize()];
		buildCircularBufferShape3();
	}
	void buildCircularBufferShape1() {
//		drawPane.getChildren().clear();
		
		double cx = drawPane.getWidth()/3;
		double cy = drawPane.getHeight()/3;
		//System.out.println("cx="+cx+" cy="+cy);

		double outrad = cx*0.4;
		
		int siz = cirbuf1.getBufSize(); 
		double angl = 2*Math.PI/siz;
		
		for (int i=0; i<siz; ++i) {
			bufShapes1[i] = new BufferShape(-100.0, -100.0);
			bufShapes1[i].setBufPath(cx, cy, outrad, angl);
			bufShapes1[i].setText(String.valueOf(i), cx, cy, outrad, angl);
			bufShapes1[i].setRot(i*Math.toDegrees(angl), cx, cy);
			bufShapes1[i].setBackground(Color.SNOW);
			drawPane.getChildren().add(bufShapes1[i]);
		}
	}
	void buildCircularBufferShape2() {
//		drawPane.getChildren().clear();
		
		double cx = drawPane.getWidth()/3;
		double cy = drawPane.getHeight()/3;
		//System.out.println("cx="+cx+" cy="+cy);

		double outrad = cx*0.4;
		
		int siz = cirbuf2.getBufSize(); 
		double angl = 2*Math.PI/siz;
		
		for (int i=0; i<siz; ++i) {
			bufShapes2[i] = new BufferShape(-100.0, 200.0);
			bufShapes2[i].setBufPath(cx, cy, outrad, angl);
			bufShapes2[i].setText(String.valueOf(i), cx, cy, outrad, angl);
			bufShapes2[i].setRot(i*Math.toDegrees(angl), cx, cy);
			bufShapes2[i].setBackground(Color.SNOW);
			drawPane.getChildren().add(bufShapes2[i]);
		}
	}
	void buildCircularBufferShape3() {
//		drawPane.getChildren().clear();
		
		double cx = drawPane.getWidth()/3;
		double cy = drawPane.getHeight()/3;
		//System.out.println("cx="+cx+" cy="+cy);

		double outrad = cx*0.4;
		
		int siz = cirbuf3.getBufSize(); 
		double angl = 2*Math.PI/siz;
		
		for (int i=0; i<siz; ++i) {
			bufShapes3[i] = new BufferShape(400.0, 50.0);
			bufShapes3[i].setBufPath(cx, cy, outrad, angl);
			bufShapes3[i].setText(String.valueOf(i), cx, cy, outrad, angl);
			bufShapes3[i].setRot(i*Math.toDegrees(angl), cx, cy);
			bufShapes3[i].setBackground(Color.SNOW);
			drawPane.getChildren().add(bufShapes3[i]);
		}
	}
	void setBufferShapeColor1() {
		int siz = cirbuf1.getBufSize();
		int front = cirbuf1.getFront();
		int rear = cirbuf1.getRear();
		for (int i=0; i<siz; ++i) {
			bufShapes1[i].setBackground(Color.SNOW);
		}
		int bp = front;
		for (int count=1; count <= cirbuf1.getOccupiedBufferCount(); ++count) {
			bufShapes1[bp].setBackground(Color.CYAN);
			bp = (bp + 1) % siz;
		}
		if (front!=rear ) {
			bufShapes1[front].setBackground(Color.GREEN);
			bufShapes1[rear].setBackground(Color.BLUE);
		}
		else if (cirbuf1.getOccupiedBufferCount()>0) {
			bufShapes1[front].setBackground(Color.GREEN);
		}
		else {
			bufShapes1[front].setBackground(Color.CYAN);
		}
		
	}
	void setBufferShapeColor2() {
		int siz = cirbuf2.getBufSize();
		int front = cirbuf2.getFront();
		int rear = cirbuf2.getRear();
		for (int i=0; i<siz; ++i) {
			bufShapes2[i].setBackground(Color.SNOW);
		}
		int bp = front;
		for (int count=1; count <= cirbuf2.getOccupiedBufferCount(); ++count) {
			bufShapes2[bp].setBackground(Color.CYAN);
			bp = (bp + 1) % siz;
		}
		if (front!=rear ) {
			bufShapes2[front].setBackground(Color.GREEN);
			bufShapes2[rear].setBackground(Color.BLUE);
		}
		else if (cirbuf2.getOccupiedBufferCount()>0) {
			bufShapes2[front].setBackground(Color.GREEN);
		}
		else {
			bufShapes2[front].setBackground(Color.CYAN);
		}
		
	}
	void setBufferShapeColor3() {
		int siz = cirbuf3.getBufSize();
		int front = cirbuf3.getFront();
		int rear = cirbuf3.getRear();
		for (int i=0; i<siz; ++i) {
			bufShapes3[i].setBackground(Color.SNOW);
		}
		int bp = front;
		for (int count=1; count <= cirbuf3.getOccupiedBufferCount(); ++count) {
			bufShapes3[bp].setBackground(Color.CYAN);
			bp = (bp + 1) % siz;
		}
		if (front!=rear ) {
			bufShapes3[front].setBackground(Color.GREEN);
			bufShapes3[rear].setBackground(Color.BLUE);
		}
		else if (cirbuf3.getOccupiedBufferCount()>0) {
			bufShapes3[front].setBackground(Color.GREEN);
		}
		else {
			bufShapes3[front].setBackground(Color.CYAN);
		}
		
	}
	private class ProducerTask implements Runnable {
		public void run() {
			try {
				while (true) {
					//System.out.println("Producer writes " + i);
					if(new java.util.Random().nextInt(2)==0){
						BigInteger num = fibGen.next();
						cirbuf1.write(String.valueOf(num)); // Add a value to the buffer
						Platform.runLater(()->{
							setBufferShapeColor1();
							prod_Count.setText(" " + String.valueOf(cirbuf1.getOccupiedBufferCount()+cirbuf2.getOccupiedBufferCount()));
							if(writeList.size() == 10) {
								writeList.remove(0);
								writeList.add(String.valueOf(num));
							}
							else {
								writeList.add(String.valueOf(num));
							}
							String writeData = "";
							for(String s : writeList) {
								writeData += " " + s + " ";
							}
							write_Data.setText(writeData);
						});
					}
					else {
						BigInteger num = fibGen.next();
						cirbuf2.write(String.valueOf(num)); // Add a value to the buffer
						Platform.runLater(()->{
							setBufferShapeColor2();
							prod_Count.setText(" " + String.valueOf(cirbuf1.getOccupiedBufferCount()+cirbuf2.getOccupiedBufferCount()));
							if(writeList.size() == 10) {
								writeList.remove(0);
								writeList.add(String.valueOf(num));
							}
							else {
								writeList.add(String.valueOf(num));
							}
							String writeData = "";
							for(String s : writeList) {
								writeData += " " + s + " ";
							}
							write_Data.setText(writeData);
						});
					}
					// Put the thread into sleep
					Thread.sleep((int)(Math.random() * 250));
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	// A task for reading and deleting an int from the buffer
	private class ConsumerTask1 implements Runnable {
		public void run() {
			try {
				while (true) {
					String sval = cirbuf1.read();
					cirbuf3.write(String.valueOf(sval));
					System.out.println("\t\t\tConsumer1 reads " + sval);
					Platform.runLater(()->{
						setBufferShapeColor1();
					});
					// Put the thread into sleep
					Thread.sleep((int)(Math.random() * 500));
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class ConsumerTask2 implements Runnable {
		public void run() {
			try {
				while (true) {
					String sval = cirbuf2.read();
					cirbuf3.write(String.valueOf(sval));
					System.out.println("\t\t\tConsumer2 reads " + sval);
					Platform.runLater(()->{
						setBufferShapeColor2();
					});
					// Put the thread into sleep
					Thread.sleep((int)(Math.random() * 500));
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class ConsumerTask3 implements Runnable {
		public void run() {
			try {
				while (true) {
					String sval = cirbuf3.read();
					System.out.println("\t\t\tConsumer3 reads " + sval);
					Platform.runLater(()->{
						setBufferShapeColor3();
						cons_Count.setText(" "+ String.valueOf(cirbuf3.getOccupiedBufferCount()));
						if(readList.size() == 10) {
							readList.remove(0);
							readList.add(sval);
						}
						else {
							readList.add(sval);
						}
						String readData = "";
						for(String s : readList) {
							readData += " " + s + " ";
						}
						read_Data.setText(readData);
					});
					// Put the thread into sleep
					Thread.sleep((int)(Math.random() * 250));
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

}
