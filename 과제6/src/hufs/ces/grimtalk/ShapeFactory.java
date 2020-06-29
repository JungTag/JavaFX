package hufs.ces.grimtalk;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;
import javafx.scene.shape.VLineTo;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ShapeFactory {

	private volatile static ShapeFactory uniqueSFInstance;
	
	GrimTalkModel model = null;

	private ShapeFactory(GrimTalkModel model) {
		this.model = model;
	}
	public static ShapeFactory getInstance(GrimTalkModel model) {
		if (uniqueSFInstance == null) {
			synchronized (GrimTalkModel.class) {
				if (uniqueSFInstance == null) {
					uniqueSFInstance = new ShapeFactory(model);
				}
			}
		}
		return uniqueSFInstance;
	}
	public Shape createPaintedShape(Shape shape) {

		if (model.isShapeFill()){
			shape.setFill(model.getShapeFillColor());
		}
		else {
			shape.setFill(Color.TRANSPARENT);
		}

		if (model.isShapeStroke()){
			shape.setStrokeWidth(model.getShapeStrokeWidth());
			shape.setStroke(model.getShapeStrokeColor());
		}
		else {
			shape.setStroke(Color.TRANSPARENT);
		}
		return shape;
	}
	public Ellipse createPaintedEllipse() {
		Ellipse shape = new Ellipse();

		if (model.isShapeFill()){
			shape.setFill(model.getShapeFillColor());
		}
		else {
			shape.setFill(Color.TRANSPARENT);
		}
		if (model.isShapeStroke()){
			shape.setStrokeWidth(model.getShapeStrokeWidth());
			shape.setStroke(model.getShapeStrokeColor());
		}
		else {
			shape.setStroke(Color.TRANSPARENT);
		}
		return shape;
	}
	public Line createPaintedLine() {
		Line shape = new Line();

		shape.setStrokeWidth(model.getShapeStrokeWidth());
		shape.setStroke(model.getShapeStrokeColor());
		return shape;
	}
	public Path createPaintedPath() {
		Path shape = new Path();

		if (model.isShapeFill()){
			shape.setFill(model.getShapeFillColor());
		}
		else {
			shape.setFill(Color.TRANSPARENT);
		}

		if (model.isShapeStroke()){
			shape.setStrokeWidth(model.getShapeStrokeWidth());
			shape.setStroke(model.getShapeStrokeColor());
		}
		else {
			shape.setStroke(Color.TRANSPARENT);
		}
		return shape;
	}
	public Shape createMousePointedLine() {
		Point2D pstart = model.getStartMousePosition();
		Point2D pend = model.getCurrMousePosition();
		return createPaintedShape(new Line(pstart.getX(), pstart.getY(), pend.getX(), pend.getY()));

	}

	public Shape createPolygonFromClickedPoints(){
		ArrayList<Point2D> points = model.polygonPoints;
		Polygon result = new Polygon();
		if (points != null && points.size() > 2) {
			for (int i=0; i<points.size(); ++i){
				result.getPoints().add(points.get(i).getX());
				result.getPoints().add(points.get(i).getY());
			}
		}
		return createPaintedShape(result);
	}
	public Shape createPolylineFromClickedPoints(){
		ArrayList<Point2D> points = model.polygonPoints;
		Polyline result = new Polyline();
		if (points != null && points.size() > 0) {
			for (int i=0; i<points.size(); ++i){
				result.getPoints().add(points.get(i).getX());
				result.getPoints().add(points.get(i).getY());
			}
		}
		return createPaintedShape(result);
	}
	public Shape createMousePointedEllipse(){

		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();

		if (pcurr.distance(topleft) <= Utils.MINPOLYDIST)
			return null;
		double radiusX = (pcurr.getX() - topleft.getX()) / 2;
		double radiusY = (pcurr.getY() - topleft.getY()) / 2;
		double centerX = topleft.getX() + radiusX;
		double centerY = topleft.getY() + radiusY;
		return createPaintedShape(new Ellipse(centerX, centerY, radiusX, radiusY));
	}
	public Shape createRegularPolygon(int nvertex){
		Point2D center = model.getStartMousePosition();
		Point2D pi = model.getCurrMousePosition();
		if (pi.distance(center) <= Utils.MINPOLYDIST)
			return new Path();

		double nangle = 360.0/nvertex; // 360/n degree
		Rotate rot = new Rotate(nangle);

		Point2D[] polyPoints = new Point2D[nvertex];
		polyPoints[0] = rot.transform(pi.getX()-center.getX(), pi.getY()-center.getY()); 
		for (int i=1; i<polyPoints.length; ++i){
			polyPoints[i] = rot.transform(polyPoints[i-1]);
		}

		Translate tra = new Translate(center.getX(), center.getY());
		//polyPoints[0] = new Point2D(pi.getX(), pi.getY()); 
		for (int i=0; i<polyPoints.length; ++i){
			polyPoints[i] = tra.transform(polyPoints[i]);
		}
		Path polygonPath = new Path();
		polygonPath.getElements().add(new MoveTo(polyPoints[0].getX(), polyPoints[0].getY()));
		for (int i=1; i<polyPoints.length; ++i){
			polygonPath.getElements().add(new LineTo(polyPoints[i].getX(), polyPoints[i].getY()));
		}
		//polygonPath.getElements().add(new LineTo(polyPoints[0].getX(), polyPoints[0].getY()));
		polygonPath.getElements().add(new ClosePath());

		return createPaintedShape(polygonPath);

	}

	static public void translateShape(Shape shape, double dx, double dy) {

		if (shape instanceof Line) {
			Line line = (Line) shape;
			line.setStartX(line.getStartX()+dx);
			line.setStartY(line.getStartY()+dy);
			line.setEndX(line.getEndX()+dx);
			line.setEndY(line.getEndY()+dy);
		}
		else if (shape instanceof Ellipse) {
			Ellipse ellipse = (Ellipse) shape;
			ellipse.setCenterX(ellipse.getCenterX()+dx);
			ellipse.setCenterY(ellipse.getCenterY()+dy);
		}
		else if (shape instanceof Path) {
			Path path =(Path) shape;
			for (PathElement el:path.getElements()) {
				if (el instanceof MoveTo) {
					MoveTo pel = (MoveTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof LineTo) {
					LineTo pel = (LineTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof ArcTo) {
					ArcTo pel = (ArcTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof HLineTo) {
					HLineTo pel = (HLineTo)el;
					pel.setX(pel.getX() + dx);
				}
				else if (el instanceof VLineTo) {
					VLineTo pel = (VLineTo)el;
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof CubicCurveTo) {
					CubicCurveTo pel = (CubicCurveTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
					pel.setControlX1(pel.getControlX1() + dx);
					pel.setControlY1(pel.getControlY1() + dy);
					pel.setControlX2(pel.getControlX2() + dx);
					pel.setControlY2(pel.getControlY2() + dy);
				}
				else if (el instanceof QuadCurveTo) {
					QuadCurveTo pel = (QuadCurveTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
					pel.setControlX(pel.getControlX() + dx);
					pel.setControlY(pel.getControlY() + dy);
				}
			}
		}
		else if (shape instanceof Polygon) {
			Polygon pol =(Polygon) shape;
			ObservableList<Double> points = pol.getPoints();
			for (int i=0; i<points.size(); i+=2) {
				points.set(i, points.get(i)+dx);
				points.set(i+1, points.get(i+1)+dy);
			}
		}
		else if (shape instanceof Polyline) {
			Polyline pol =(Polyline) shape;
			ObservableList<Double> points = pol.getPoints();
			for (int i=0; i<points.size(); i+=2) {
				points.set(i, points.get(i)+dx);
				points.set(i+1, points.get(i+1)+dy);
			}
		}
	}
}
