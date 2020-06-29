package hufs.ces.grimtalk;

import hufs.ces.grimtalk.svg.SVGGrimShape;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class DrawPane extends Pane {

	private GrimTalkModel model;

	public DrawPane(GrimTalkModel model) {
		this.model = model;
	}
	public DrawPane(GrimTalkModel model, double width, double height) {
		this.model = model;
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public void clear() {
		this.getChildren().clear();
	}
	public void redraw() {
		clear();
		//System.out.println("Shape Count="+model.shapeList.size());
		for (SVGGrimShape gsh:model.shapeList){
			this.getChildren().add(gsh.getShape());
		}
		if (model.curDrawShape!=null && model.curDrawShape.getShape()!=null) {
			this.getChildren().add(model.curDrawShape.getShape());
		}
	}
}
