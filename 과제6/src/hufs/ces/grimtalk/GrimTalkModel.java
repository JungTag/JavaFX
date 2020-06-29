package hufs.ces.grimtalk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import hufs.ces.grimtalk.svg.SVGGrimShape;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class GrimTalkModel {
	
	private int editState = Utils.SHAPE_PENCIL;

	
	private float shapeStrokeWidth = 10f;
	private Color shapeStrokeColor = Color.BLACK;
	private boolean shapeStroke = true;
	private boolean shapeFill = false;
	private Color shapeFillColor = null;
	
	public ObservableList<SVGGrimShape> shapeList = null;
	
	private Point2D startMousePosition = null;
	private Point2D currMousePosition = null;
	private Point2D prevMousePosition = null;
	
	public SVGGrimShape curDrawShape = null;
	public ArrayList<Point2D> polygonPoints = null;
	private int selectedShape = -1;
	
	private int nPolygon = 3;
	
	private File saveFile = null;
	private int selectedShapeIndex = -1;

	public GrimTalkModel(){
		//this.mainFrame = frame;
		this.shapeList = FXCollections.observableArrayList();
		this.shapeStrokeColor = Color.BLACK;
		this.shapeFillColor = Color.TRANSPARENT;
		this.polygonPoints = new ArrayList<Point2D>();
	}

	public int getEditState() {
		return editState;
	}

	public void setEditState(int editState) {
		this.editState = editState;
	}

	public Point2D getStartMousePosition() {
		return startMousePosition;
	}

	public void setStartMousePosition(Point2D mousePosition) {
		this.startMousePosition = mousePosition;
	}
	public Point2D getPrevMousePosition() {
		return prevMousePosition;
	}

	public void setPrevMousePosition(Point2D mousePosition) {
		this.prevMousePosition = mousePosition;
	}

	public Point2D getCurrMousePosition() {
		return currMousePosition;
	}

	public void setCurrMousePosition(Point2D clickedMousePosition) {
		this.currMousePosition = clickedMousePosition;
	}
	/**
	 * @return the saveFile
	 */
	public File getSaveFile() {
		return saveFile;
	}

	/**
	 * @param saveFile the saveFile to set
	 */
	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
		//mainFrame.setTitle("�׸��� - "+saveFile.getPath());
	}
	/**
	 * @return the nPolygon
	 */
	public int getNPolygon() {
		return nPolygon;
	}

	/**
	 * @param nPolygon the nPolygon to set
	 */
	public void setNPolygon(int nPolygon) {
		this.nPolygon = nPolygon;
	}

	/**
	 * @return the selectedShape
	 */
	public void getSelectedShape(){
		int selIndex = -1;
		Shape shape = null;
		for (int i=this.shapeList.size()-1; i >= 0; --i){
			shape = this.shapeList.get(i).getShape();
			if (shape.contains(this.getStartMousePosition().getX(), this.getStartMousePosition().getY())){
				selIndex = i;
				break;
			}
		}
		if (selIndex != -1){
			Color scolor = (Color)shape.getStroke();
			Color fcolor = (Color)shape.getFill();
			if (shape.getStroke()!=Color.TRANSPARENT){
				shape.setStroke(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 0.5));
			}
			if (shape.getFill()!=Color.TRANSPARENT){
				shape.setFill(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 0.5));
			}
		}
		this.setSelectedShapeIndex(selIndex);
	}

	public void getUnDeletedShape(){
		int selIndex = -1;
		Shape shape = null;
		for (int i=this.shapeList.size()-1; i >= 0; --i){
			shape = this.shapeList.get(i).getShape();
			if (shape.contains(this.getStartMousePosition().getX(), this.getStartMousePosition().getY())){
				selIndex = i;
				break;
			}
		}
		
		this.setSelectedShapeIndex(selIndex);
	}
	/**
	 * @return the shapeStrokeColor
	 */
	public Color getShapeStrokeColor() {
		return shapeStrokeColor;
	}

	/**
	 * @param shapeStrokeColor the shapeStrokeColor to set
	 */
	public void setShapeStrokeColor(Color shapeStrokeColor) {
		this.shapeStrokeColor = shapeStrokeColor;
	}

	/**
	 * @return the shapeFill
	 */
	public boolean isShapeFill() {
		return shapeFill;
	}

	/**
	 * @param shapeFill the shapeFill to set
	 */
	public void setShapeFill(boolean shapeFill) {
		this.shapeFill = shapeFill;
	}

	/**
	 * @return the shapeFillColor
	 */
	public Color getShapeFillColor() {
		return shapeFillColor;
	}

	/**
	 * @param shapeFillColor the shapeFillColor to set
	 */
	public void setShapeFillColor(Color shapeFillColor) {
		this.shapeFillColor = shapeFillColor;
	}

	/**
	 * @return the shapeStrokeWidth
	 */
	public float getShapeStrokeWidth() {
		return shapeStrokeWidth;
	}

	/**
	 * @param shapeStrokeWidth the shapeStrokeWidth to set
	 */
	public void setShapeStrokeWidth(float shapeStrokeWidth) {
		this.shapeStrokeWidth = shapeStrokeWidth;
	}

	public boolean isShapeStroke() {
		return shapeStroke;
	}

	public void setShapeStroke(boolean shapeStroke) {
		this.shapeStroke = shapeStroke;
	}
	public int getSelectedShapeIndex() {
		return selectedShapeIndex;
	}

	/**
	 * @param selectedShape the selectedShape to set
	 */
	public void setSelectedShapeIndex(int selIndex) {
		this.selectedShapeIndex = selIndex;
	}
	
}
