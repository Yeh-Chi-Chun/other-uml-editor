package mod.instance;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.BasicStroke;

import javax.swing.JPanel;

import Define.AreaDefine;
import Pack.DragPack;
import bgWork.handler.CanvasPanelHandler;
import mod.IFuncComponent;
import mod.ILinePainter;

public class DependencyLine extends MyLine implements IFuncComponent, ILinePainter {
	JPanel from;
	int fromSide;
	Point fp = new Point(0, 0);
	JPanel to;
	int toSide;
	Point tp = new Point(0, 0);
	int arrowSize = 6;
	int panelExtendSize = 10;
	boolean isSelect = false;
	int selectBoxSize = 5;
	CanvasPanelHandler cph;

	public DependencyLine(CanvasPanelHandler cph) {
		super(cph);
		this.setOpaque(false);
		this.setVisible(true);
		this.setMinimumSize(new Dimension(1, 1));
		this.cph = cph;
	}

	@Override
	public void paintComponent(Graphics g) {
		Point fpPrime;
		Point tpPrime;
		renewConnect();
		fpPrime = new Point(fp.x - this.getLocation().x, fp.y - this.getLocation().y);
		tpPrime = new Point(tp.x - this.getLocation().x, tp.y - this.getLocation().y);
		if (isSelect == true) {
			paintSelect(g);
		}
		else {
			g.setColor(Color.BLACK);
		}
		
		Graphics2D g2d = (Graphics2D) g.create();
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
		g2d.setStroke(dashed);
		g2d.drawLine(fpPrime.x, fpPrime.y, tpPrime.x, tpPrime.y);
		paintArrow(g, tpPrime);
		
	}

	@Override
	public void reSize() {
		Dimension size = new Dimension(Math.abs(fp.x - tp.x) + panelExtendSize * 2,
				Math.abs(fp.y - tp.y) + panelExtendSize * 2);
		this.setSize(size);
		this.setLocation(Math.min(fp.x, tp.x) - panelExtendSize, Math.min(fp.y, tp.y) - panelExtendSize);
	}

	@Override
	public void paintArrow(Graphics g, Point point) {
		Point fpPrime = new Point(fp.x - this.getLocation().x, fp.y - this.getLocation().y);
		Point tpPrime = new Point(tp.x - this.getLocation().x, tp.y - this.getLocation().y);
		Point tip = tpPrime.getLocation();
		Point tail = fpPrime.getLocation();
        double phi = Math.toRadians(40);
        int barb = 20;
		Graphics2D g2 = (Graphics2D)g;
        double dy = tip.y - tail.y;
        double dx = tip.x - tail.x;
        double theta = Math.atan2(dy, dx);
        double x, y, rho = theta + phi;
        
        for(int j = 0; j < 2; j++)
        {
            x = tip.x - barb * Math.cos(rho);
            y = tip.y - barb * Math.sin(rho);
            g2.draw(new Line2D.Double(tip.x, tip.y, x, y));
            rho = theta - phi;
        }
	}
	

	@Override
	public void setConnect(DragPack dPack) {
		Point mfp = dPack.getFrom();
		Point mtp = dPack.getTo();
		from = (JPanel) dPack.getFromObj();
		to = (JPanel) dPack.getToObj();
		fromSide = new AreaDefine().getArea(from.getLocation(), from.getSize(), mfp);
		toSide = new AreaDefine().getArea(to.getLocation(), to.getSize(), mtp);
		renewConnect();
		System.out.println("from side " + fromSide);
		System.out.println("to side " + toSide);
	}

	void renewConnect() {
		try {
			fp = getConnectPoint(from, fromSide);
			tp = getConnectPoint(to, toSide);
			this.reSize();
		} catch (NullPointerException e) {
			this.setVisible(false);
			cph.removeComponent(this);
		}
	}

	Point getConnectPoint(JPanel jp, int side) {
		Point temp = new Point(0, 0);
		Point jpLocation = cph.getAbsLocation(jp);
		if (side == new AreaDefine().TOP) {
			temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
			temp.y = jpLocation.y;
		} else if (side == new AreaDefine().RIGHT) {
			temp.x = (int) (jpLocation.x + jp.getSize().getWidth());
			temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
		} else if (side == new AreaDefine().LEFT) {
			temp.x = jpLocation.x;
			temp.y = (int) (jpLocation.y + jp.getSize().getHeight() / 2);
		} else if (side == new AreaDefine().BOTTOM) {
			temp.x = (int) (jpLocation.x + jp.getSize().getWidth() / 2);
			temp.y = (int) (jpLocation.y + jp.getSize().getHeight());
		} else {
			temp = null;
			System.err.println("getConnectPoint fail:" + side);
		}
		return temp;
	}

	@Override
	public void paintSelect(Graphics gra) {
		gra.setColor(Color.GREEN);
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public JPanel getFromShape()
	{
		return this.from;
	}
	
	public int getFromSide()
	{
		return this.fromSide;
	}
	
	public JPanel getToShape()
	{
		return this.to;
	}
	
	public int getToSide()
	{
		return this.toSide;
	}

}
