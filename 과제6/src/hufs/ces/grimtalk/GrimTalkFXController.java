package hufs.ces.grimtalk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import hufs.ces.grimtalk.Utils;
import hufs.ces.grimtalk.svg.SVGGrimEllipse;
import hufs.ces.grimtalk.svg.SVGGrimLine;
import hufs.ces.grimtalk.svg.SVGGrimPath;
import hufs.ces.grimtalk.svg.SVGGrimPolygon;
import hufs.ces.grimtalk.svg.SVGGrimPolyline;
import hufs.ces.grimtalk.svg.SVGGrimShape;
import hufs.ces.grimtalk.svg.SaxSVGPathParseHandler;
import hufs.ces.rmi.RMIMessanger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class GrimTalkFXController extends AnchorPane {

	private static final String HOST = "localhost";
	private RMIMessanger rmiObj;
	
	private String clientID = null;
	private String partnerID = null;
	private ReceiverThread receiver = null;

	private DrawPane drawPane;
	
	public Stage parentStage;
	private GrimTalkModel model;
	private ShapeFactory sf;
	
	DoubleProperty widthProp = new SimpleDoubleProperty();
	DoubleProperty heightProp = new SimpleDoubleProperty();
	
	ColorPicker fcolorPicker = new ColorPicker(Color.WHITE);
	ColorPicker scolorPicker = new ColorPicker(Color.BLACK);
	
	public GrimTalkFXController() {

		model = new GrimTalkModel();
		sf = ShapeFactory.getInstance(model);

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/grimtalk.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fcolorPicker.setOnAction(t->{
			Color color = fcolorPicker.getValue();
			model.setShapeFillColor(color);
		});
		Label fcolorLabel =  new Label("", fcolorPicker);
		fcolorLabel.setContentDisplay(ContentDisplay.RIGHT);
		fcolorLabel.setStyle("-fx-padding: 0 0 0 15;");
		menuFillColor.setGraphic(fcolorLabel);

		scolorPicker.setOnAction(t->{
			Color color = scolorPicker.getValue();
			model.setShapeStrokeColor(color);
		});
		Label scolorLabel =  new Label("", scolorPicker);
		scolorLabel.setContentDisplay(ContentDisplay.RIGHT);
		scolorLabel.setStyle("-fx-padding: 0 0 0 15;");
		menuStrokeColor.setGraphic(scolorLabel);
		
		drawPane = new DrawPane(model);
		drawPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		drawPane.setOnMousePressed(e->handleMousePressed(e));
		drawPane.setOnMouseReleased(e->handleMouseReleased(e));
		drawPane.setOnMouseDragged(e->handleMouseDragged(e));
		drawFramePane.setCenter(drawPane);

		widthProp.bind(drawPane.widthProperty());
		widthProp.addListener((obs, oldVal, newVal) -> {
			double val = ((ObservableDoubleValue)obs).get();
			//System.out.format("drawPane w=%s newVal=%s \n", val, newVal);
		});
		heightProp.bind(drawPane.heightProperty());
		heightProp.addListener((obs, oldVal, newVal) -> {
			double val = ((ObservableDoubleValue)obs).get();
			//System.out.format("drawPane h=%s newVal=%s \n", val, newVal);
		});
		
		model.shapeList.addListener((ListChangeListener<SVGGrimShape>) c-> {
	        while (c.next()) {
	            if (c.wasAdded()) {
	            	//System.out.println("Shape Count ="+model.shapeList.size());
	            }
	            if (c.wasRemoved()) {
	            	//System.out.println("Shape Count ="+model.shapeList.size());
	            }
	        }
		});
		
		initDrawPane();
		
		initRMIObj();
		
	}
	
	void initRMIObj() {
		//System.setSecurityManager(new RMISecurityManager());
		Object obj = null;
		Context namingContext = null;
		String rmiObjectName = "rmi://" + HOST + "/RMIMessanger";
		try {
			namingContext = new InitialContext();
			System.out.println("RMI registry bindings: ");
			Enumeration<NameClassPair> e = namingContext.list("rmi://localhost/");
			while (e.hasMoreElements())
				System.out.println(e.nextElement().getName()+" is in naming Context List");
			
			obj = namingContext.lookup(rmiObjectName);
			rmiObj = (RMIMessanger) obj;
		} catch (NamingException e1) {
			System.err.println("Could not find the requested remote object on the server");
			e1.printStackTrace();
		}
	}
	
	void initDrawPane() {		
		model.shapeList.clear();
		model.curDrawShape = null;
		drawPane.redraw();
	}
	
    @FXML
    private AnchorPane root;

    @FXML
    private BorderPane drawFramePane;

	
    @FXML
    private MenuItem menuNew;

    @FXML
    private MenuItem menuExit;

    @FXML
    private RadioMenuItem menuLine;

    @FXML
    private RadioMenuItem menuPencil;

    @FXML
    private MenuItem menuMove;

    @FXML
    private MenuItem menuDelete;

    @FXML
    private MenuItem menuDeleteAll;

    @FXML
    private MenuItem menuStrokeWidth;

    @FXML
    private MenuItem menuStrokeColor;

    @FXML
    private MenuItem menuFillColor;

    @FXML
    private CheckMenuItem menuCheckStroke;

    @FXML
    private CheckMenuItem menuCheckFill;

    @FXML
    private MenuItem menuAbout;

    
    @FXML
    private Label lblMessage;

    @FXML
    private Label lblEditState;

    @FXML
    private Label lblShapeCount;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnConnect;

    @FXML
    private Button btnSend;

    @FXML
    void handleMenuAbout(ActionEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(parentStage);   
		alert.setTitle("About");
		alert.setHeaderText("GrimTalk Ver 0.1.1");
		alert.setContentText("Programmed by cskim, ces, hufs.ac.kr");

		alert.showAndWait();
    }

    @FXML
    void handleMenuCheckFill(ActionEvent event) {
		CheckMenuItem checkFill = (CheckMenuItem)event.getSource();
		if (checkFill.isSelected())
			model.setShapeFill(true);
		else
			model.setShapeFill(false);
    }

    @FXML
    void handleMenuCheckStroke(ActionEvent event) {
		CheckMenuItem checkStroke = (CheckMenuItem)event.getSource();
		if (checkStroke.isSelected())
			model.setShapeStroke(true);
		else
			model.setShapeStroke(false);
    }

    @FXML
    void handleMenuDelete(ActionEvent event) {
    	model.setEditState(Utils.EDIT_DELETE);
		drawPane.redraw();
    }

    @FXML
    void handleMenuDeleteAll(ActionEvent event) {
    	initDrawPane(); 
    }
    
    @FXML
    void handleMenuExit(ActionEvent event) {
		Platform.exit();
    }

    @FXML
    void handleMenuFillColor(ActionEvent event) {

    }

    @FXML
    void handleMenuLine(ActionEvent event) {
    	model.setEditState(Utils.SHAPE_LINE);
		drawPane.redraw();
    }

    @FXML
    void handleMenuMove(ActionEvent event) {
    	model.setEditState(Utils.EDIT_MOVE);
		if (model.curDrawShape != null){
			model.shapeList.add(model.curDrawShape);
			model.curDrawShape = null;
		}
		drawPane.redraw();
    }

    @FXML
    void handleMenuNew(ActionEvent event) {
		initDrawPane();
    }

    @FXML
    void handleMenuOval(ActionEvent event) {
		model.setEditState(Utils.SHAPE_OVAL);
		drawPane.redraw();
    }

    @FXML
    void handleMenuPencil(ActionEvent event) {
		model.setEditState(Utils.SHAPE_PENCIL);
		drawPane.redraw();
    }

    @FXML
    void handleMenuPolygon(ActionEvent event) {
		model.setEditState(Utils.SHAPE_POLYGON);
		drawPane.redraw();
    }

    @FXML
    void handleMenuRegular(ActionEvent event) {
		model.setEditState(Utils.SHAPE_REGULAR);
		String[] possibleValues = { 
				"3", "4", "5", "6", "7",
				"8", "9", "10", "11", "12"
		};
		List<String> dialogData = Arrays.asList(possibleValues);

		ChoiceDialog<String> dialog = new ChoiceDialog<>(dialogData.get(0), dialogData);
		dialog.setTitle("Regular Polygon");
		dialog.setHeaderText("Number of Points");
		dialog.initOwner(parentStage);
		Optional<String> result = dialog.showAndWait();
		String selectedValue = String.valueOf(model.getNPolygon());
		if (result.isPresent()) {
			selectedValue = result.get();
		}

		model.setNPolygon(Integer.parseInt((String)selectedValue));

		drawPane.redraw();
    }

    @FXML
    void handleMenuStrokeColor(ActionEvent event) {
    }

    @FXML
    void handleMenuStrokeWidth(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog("10");
		dialog.initOwner(parentStage);
		dialog.setTitle("Set Stroke Width");
		dialog.setHeaderText("Enter Stroke Width Value");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String inputVal = result.get();
			model.setShapeStrokeWidth(Float.parseFloat(inputVal));
		}

    }

	// Mouse Event Handler
    void handleMouseEntered(MouseEvent event) {
    	//model.setMouseInside(true);
    }

    void handleMouseExited(MouseEvent event) {
    	//model.setMouseInside(false);
    }

    @FXML
    void handleMouseDragged(MouseEvent event) {
		Point2D p1 = new Point2D(Math.max(0, event.getX()), Math.max(0, event.getY()));

		if (event.getButton()==MouseButton.PRIMARY){
			model.setPrevMousePosition(model.getCurrMousePosition());
			model.setCurrMousePosition(p1);

			switch (model.getEditState()){
			case Utils.SHAPE_LINE:
				model.curDrawShape = new SVGGrimLine((Line)(sf.createMousePointedLine()));
				break;
			case Utils.SHAPE_PENCIL:
				((Path)model.curDrawShape.getShape()).getElements().add(new LineTo(p1.getX(), p1.getY()));
				break;
			case Utils.SHAPE_POLYGON:
				break;
			case Utils.SHAPE_REGULAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createRegularPolygon(model.getNPolygon())));
				break;
			case Utils.SHAPE_OVAL:
				model.curDrawShape = new SVGGrimEllipse((Ellipse)(sf.createMousePointedEllipse()));
				break;
			case Utils.EDIT_MOVE:
				moveShapeByMouse();
				break;
			case Utils.EDIT_DELETE:
				model.getUnDeletedShape();
				break;
			}
		}
		drawPane.redraw();
    }

    @FXML
    void handleMousePressed(MouseEvent event) {
		//System.out.format("drawPane w=%s h=%s\n", drawPane.getWidth(), drawPane.getHeight());
		//System.out.format("drawPane Property w=%s h=%s\n", widthProp.get(), heightProp.get());
		Point2D p1 = new Point2D(Math.max(0, event.getX()), Math.max(0, event.getY()));

		if (event.getButton()==MouseButton.PRIMARY){
			model.setStartMousePosition(p1);
			model.setCurrMousePosition(p1);
			model.setPrevMousePosition(p1);				
			switch (model.getEditState()){
			case Utils.SHAPE_LINE:
				model.curDrawShape = new SVGGrimLine((Line)(sf.createMousePointedLine()));
				break;
			case Utils.SHAPE_PENCIL:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createPaintedShape(new Path(new MoveTo(p1.getX(), p1.getY())))));
				break;
			case Utils.SHAPE_POLYGON:
				model.polygonPoints.add(model.getCurrMousePosition());
				if (event.isShiftDown()) {
					//((Path)model.curDrawShape).getElements().add(new ClosePath());
					model.curDrawShape = new SVGGrimPolygon((Polygon)(sf.createPolygonFromClickedPoints()));
					model.polygonPoints.clear();
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				else {
					model.curDrawShape = new SVGGrimPolyline((Polyline)(sf.createPolylineFromClickedPoints()));
				}
				break;
			case Utils.SHAPE_REGULAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createRegularPolygon(model.getNPolygon())));
				break;
			case Utils.SHAPE_OVAL:
				model.curDrawShape = new SVGGrimEllipse((Ellipse)(sf.createMousePointedEllipse()));
				break;
			case Utils.EDIT_MOVE:
				model.getSelectedShape();
				break;
			case Utils.EDIT_DELETE:
				model.getUnDeletedShape();
				break;
			}
		}
		drawPane.redraw();
    }

    @FXML
    void handleMouseReleased(MouseEvent event) {
		Point2D p1 = new Point2D(Math.max(0, event.getX()), Math.max(0, event.getY()));
		//System.out.println("Mouse Released at "+p1);

		if (event.getButton()==MouseButton.PRIMARY){
			model.setPrevMousePosition(model.getCurrMousePosition());
			model.setCurrMousePosition(p1);
			//System.out.println("Mouse Released at "+p1);

			switch (model.getEditState()){
			case Utils.SHAPE_LINE:
				model.curDrawShape = new SVGGrimLine((Line)(sf.createMousePointedLine()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_PENCIL:
				((Path)model.curDrawShape.getShape()).getElements().add(new LineTo(p1.getX(), p1.getY()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_POLYGON:
				break;
			case Utils.SHAPE_REGULAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createRegularPolygon(model.getNPolygon())));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_OVAL:
				model.curDrawShape = new SVGGrimEllipse((Ellipse)(sf.createMousePointedEllipse()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.EDIT_MOVE:
				if (model.getSelectedShapeIndex()!=-1) {
					endShapeMove();
				}
				break;
			case Utils.EDIT_DELETE:
				if (model.getSelectedShapeIndex()!=-1) {
					deleteShape();
				}
				break;
			}
		}
    }
    
	private void deleteShape(){ 
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			drawPane.getChildren().remove(shape);
			model.shapeList.remove(model.shapeList.get(selIndex));
		}
	}
    
	private void moveShapeByMouse(){
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			double dx = model.getCurrMousePosition().getX() - model.getPrevMousePosition().getX();
			double dy = model.getCurrMousePosition().getY() - model.getPrevMousePosition().getY();

			ShapeFactory.translateShape(shape, dx, dy);
		}
	}
	private void endShapeMove(){
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			Color scolor = (Color)shape.getStroke();
			Color fcolor = (Color)shape.getFill();
			if (shape.getStroke()!=Color.TRANSPARENT){
				shape.setStroke(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 1));
			}
			if (shape.getFill()!=Color.TRANSPARENT){
				shape.setFill(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 1));
			}
			double dx = model.getCurrMousePosition().getX() - model.getPrevMousePosition().getX();
			double dy = model.getCurrMousePosition().getY() - model.getPrevMousePosition().getY();

			ShapeFactory.translateShape(shape, dx, dy);
		}
	}
	
    @FXML
    void handleConnectBtn(ActionEvent event) {
		try {
			String id = rmiObj.connect(clientID);
			if (id != null) {
				partnerID = id;

				lblMessage.setText("You connected to ID:" + partnerID + "\n");
				btnConnect.setDisable(true);

				receiver = new ReceiverThread(rmiObj);
				receiver.start();
			}
			else
				lblMessage.setText("You can not connect. Try again\n");

		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

	
    @FXML
    void handleRegisterBtn(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.initOwner(parentStage);
		dialog.setTitle("Register ID");
		dialog.setHeaderText("Enter Your ID");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String id  = result.get();
			try {
				if (rmiObj.register(id) ) {
					clientID = id; 
					btnRegister.setDisable(true);
					parentStage.setTitle("GrimTalk -- ID <"+id+"> ");
				}
				else {
					lblMessage.setText("Invalid ID:" + id + "\n");
				}
			} 
			catch (RemoteException e1) {
				e1.printStackTrace();

			}
		}
    }

    @FXML
    void handleSendBtn(ActionEvent event) {
		sendGrimAction();
    }
	void sendGrimAction(){
		StringBuilder sb = new StringBuilder();
		
		//sb.append("<?xml version='1.0' encoding='utf-8' standalone='no'?> \n");
		sb.append("<svg xmlns:svg='http://www.w3.org/2000/svg' ");
		sb.append("     xmlns='http://www.w3.org/2000/svg' \n");
		sb.append(String.format("width='%f.0' ", widthProp.getValue()));
		sb.append(String.format("height='%f.0' ", heightProp.getValue()));
		sb.append("overflow='visible' xml:space='preserve'> \n");
		for (SVGGrimShape gs:model.shapeList){
			sb.append("    "+gs.getSVGShapeString());
			sb.append('\n');
		}
		sb.append("</svg>\n");

		String theLines = sb.toString();
		//System.out.println(theLines);

		try {
			rmiObj.write(clientID, theLines);
		} 
		catch (RemoteException e1) {
			e1.printStackTrace();

		}
	}

	class ReceiverThread extends Thread {

		private RMIMessanger messanger;

		public ReceiverThread(RMIMessanger messanger) {
			this.messanger = messanger;
		}

		public void run() {

			while (true) {
				
				convertSVGText2SVGGrimShapeList();	
				
				Thread.yield();

			}  

		}
		void convertSVGText2SVGGrimShapeList() {
			SaxSVGPathParseHandler saxTreeHandler = new SaxSVGPathParseHandler(); 

			try {
				String theLines = messanger.read(clientID);
				//System.out.println("received svg="+theLines);
				InputStream grimStream = new ByteArrayInputStream(theLines.getBytes());

				SAXParserFactory saxf = SAXParserFactory.newInstance();
				SAXParser saxParser = saxf.newSAXParser();
				
				saxParser.parse(new InputSource(grimStream), saxTreeHandler);
			}
			catch(Exception e){
				e.printStackTrace();
			}

			model.shapeList.clear();
			ObservableList<SVGGrimShape> gshapeList = saxTreeHandler.getPathList();
			for (SVGGrimShape gsh:gshapeList) {
				model.shapeList.add(gsh);
			}
			
			Platform.runLater(()->{
				drawPane.redraw();
			});

		}

	}
	
}
