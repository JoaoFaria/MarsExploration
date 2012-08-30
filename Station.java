import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DTorus;


public class Station implements Drawable {
	private int x, y;
	private long totalArmazenado;
	private Object2DTorus space;

	public Station(int x, int y, Object2DTorus space){
		this.x = x;
		this.y = y;
		this.space = space;
		this.totalArmazenado = 0;
	}

	public void draw(SimGraphics g) {
		g.drawFastRect(new Color(255, 150, 0)); //COR BRANCA
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public long getTotalArmazenado() {
		return totalArmazenado;
	}
	
	public void addMineral(int value)
	{
		this.totalArmazenado+=value;
	}
}
