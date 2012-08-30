import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DTorus;


public class Source implements Drawable {
	private int x, y;
	private int quantidade;
	private int retirado;
	private Object2DTorus space;

	public Source(int x, int y, int quantidade, Object2DTorus space){
		this.x = x;
		this.y = y;
		this.space = space;
		this.quantidade = quantidade;
		this.retirado=0;
	}

	public void draw(SimGraphics g) {
		if( this.quantidade == 0 && this.retirado==0)
			g.drawCircle(new Color(200, 200, 200));
		else if(this.retirado>0)
				g.drawFastCircle(new Color(200, 200, 200));
		else
			g.drawFastCircle(new Color(184,134,11));
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getQuantidade() {
		return quantidade;
	}
	
	public int getRetirado(){
		return retirado;
	}
	
	public int esvazia(int valor) {
		int tmp = quantidade;
		if(quantidade <= valor) {
			retirado+=quantidade;
			quantidade = 0;
			return tmp;
		}
		quantidade -= valor;
		retirado+=valor;
		return valor;
	}
	
	public int transporta(int capacidade)
	{
		if(capacidade>=retirado)
		{
			int tmp=retirado;
			retirado=0;
			return tmp;
		}
		
		retirado-=capacidade;
		return capacidade;
	}
	
	public String toString(){
		return this.x+";"+y+":"+this.quantidade+":"+this.retirado;
	}
}
