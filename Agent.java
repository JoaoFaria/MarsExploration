import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import uchicago.src.sim.util.Random;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DTorus;


public class Agent implements Drawable {
	
	protected int id;
	protected int x, y;
	protected Object2DTorus space;
	private Color color;
	static protected Map<Pair, Source> mapa = null;
	protected ArrayList<Message> messages;
	protected ArrayList<Pair> tasks;
	//free - f; waiting - 'w'
	protected char status;
	//1-random; 2-probabilidades
	private int mode;
	protected ArrayList<ArrayList<Integer>> walkmeter;
	
	//debug
	//private BufferedWriter out;
	
	public Agent(int id, int x, int y, Object2DTorus space, Color color , Map<Pair,Source> mapa, int walkmode){
		this.id = id;
		this.x = x;
		this.y = y;
		this.setSpace(space);
		this.color = color;
		this.mode = 2;
		messages = new ArrayList<Message>();
		tasks = new ArrayList<Pair>();
		status = 'f';
		walkmeter = new ArrayList<ArrayList<Integer>>();
		
		for(int i=0; i<space.getSizeX(); i++)
		{
			ArrayList<Integer> tmpArray = new ArrayList<Integer>();
			
			for(int j=0; j<space.getSizeY(); j++)
				tmpArray.add(1);
			
			walkmeter.add(tmpArray);
		}
		
		if(Agent.mapa==null)
			Agent.mapa = mapa;		
	}
	
	public int getID()
	{
		return id;
	}
	
	public char getStatus()
	{
		return status;
	}
	
	public void addMessage(Message msg)
	{
		messages.add(msg);
	}

	public void draw(SimGraphics arg0) {
		arg0.drawFastCircle(this.color);
	}
	
	public int getX() {
		if (x>=space.getSizeX())
			return x-space.getSizeX();
		if (x<0)
			return x+space.getSizeX();
		return x;
	}

	public int getY() {
		if (y>=space.getSizeY())
			return y-space.getSizeY();
		if (y<0)
			return y+space.getSizeY();
		return y;
	}
	
	public void walk() {
		
		ArrayList<Integer> tmpArr = walkmeter.get(this.getX());		
		tmpArr.set(this.getY(), tmpArr.get(this.getY())+1);
		walkmeter.set(this.getX(), tmpArr);
		
		int xMove = 0;
		int yMove = 0;
		
		if(tasks.size()==0)
		{			
			if(mode==1)
			{				
				while(xMove==0 && yMove==0)
				{
					xMove = Random.uniform.nextIntFromTo(0, 2)-1;
					yMove = Random.uniform.nextIntFromTo(0, 2)-1;
				}
				
				this.x += xMove;
				this.x %= this.space.getSizeX();
				this.y += yMove;
				this.y %= this.space.getSizeY();
					
				if(this.x == -1) this.x=this.space.getSizeX()-1;
				if(this.y == -1) this.y=this.space.getSizeY()-1;
			}
			else if(mode==2)
			{				
				double nTotal = 0;
				
				for(int i=-1; i<2; i++)
					for(int j=-1; j<2; j++)
						if(i!=0 || j!=0)
						{
							int tmpX = this.getX()+i;
							int tmpY = this.getY()+j;
							
							if(tmpX==-1)
								tmpX = this.space.getSizeX()-1;
							if(tmpY==-1)
								tmpY = this.space.getSizeY()-1;
							if(tmpX==this.space.getSizeX())
								tmpX = 0;
							if(tmpY==this.space.getSizeY())
								tmpY = 0;
							
							nTotal += (walkmeter.get(tmpX).get(tmpY));
						}
				
				double probTmp = Math.random()*7*nTotal;
				
				boolean fica = true;
				double accum = 0;
				for(int i=1; fica && i>-2; i--)
					for(int j=1; fica && j>-2; j--)
						if(i!=0 || j!=0)
						{
							int tmpX = this.getX()+i;
							int tmpY = this.getY()+j;
							
							if(tmpX==-1)
								tmpX = this.space.getSizeX()-1;
							if(tmpY==-1)
								tmpY = this.space.getSizeY()-1;
							if(tmpX==this.space.getSizeX())
								tmpX = 0;
							if(tmpY==this.space.getSizeY())
								tmpY = 0;
							
							
							double tmpAccum = accum+nTotal-(walkmeter.get(tmpX).get(tmpY));
							
							if(accum<=probTmp && tmpAccum>probTmp)
							{
								this.x=tmpX;
								this.y=tmpY;
								fica=false;
							}							
							
							accum=tmpAccum;
						}
			}
			this.space.putObjectAt(this.x, this.y, this);
		}
		else
		{
			Pair taskCoord = tasks.get(0);
			if(this.x<taskCoord.first())
				xMove = 1;
			else if(this.x>taskCoord.first())
				xMove = -1;
			if(this.y<taskCoord.second())
				yMove = 1;
			else if(this.y>taskCoord.second())
				yMove = -1;
			
			this.x += xMove;
			this.y += yMove;

			this.space.putObjectAt(this.x, this.y, this);
		}
	}
	
	//se id==-1 manda para todos
	protected boolean talkToSpotters(int id, Message msg, ArrayList<Spotter> spotters)
	{
		ArrayList<Spotter> agents = new ArrayList<Spotter>();
		
		if(id==-1)
			agents = spotters;
		else
		{
			for(Spotter s : spotters)
			{
				if(s.getID()==id)
				{
					agents.add(s);
					break;
				}
			}
		}
		
		if(agents.size()==0)
			return false;
		
		for(Spotter s : agents)
			s.addMessage(msg);
		
		return true;
	}
	
	protected boolean talkToProducers(int id, Message msg, ArrayList<Producer> producers)
	{
		ArrayList<Producer> agents = new ArrayList<Producer>();
		
		if(id==-1)
			agents = producers;
		else
		{
			for(Producer p : producers)
			{
				if(p.getID()==id)
				{
					agents.add(p);
					break;
				}
			}
		}
		
		if(agents.size()==0)
			return false;
		
		for(Producer p : agents)
			p.addMessage(msg);
		
		
		return true;
	}
	
	protected boolean talkToTransporters(int id, Message msg, ArrayList<Transporter> transporters)
	{
		ArrayList<Transporter> agents = new ArrayList<Transporter>();
		
		if(id==-1)
			agents = transporters;
		else
		{
			for(Transporter t : transporters)
			{
				if(t.getID()==id)
				{
					agents.add(t);
					break;
				}
			}
		}
		
		if(agents.size()==0)
			return false;
		
		for(Transporter t : agents){
			t.addMessage(msg);
		}
		
		return true;
	}

	public void setSpace(Object2DTorus space) {
		this.space = space;
	}

	public Object2DTorus getSpace() {
		return space;
	}
	
	protected float calcTime1(Pair lastCoord) {
		float result = 0;
		
		int currentX = this.x;
		int currentY = this.y;
		
		int largura = getSpace().getSizeX();
		int altura = getSpace().getSizeY();
		
		//calcula a distancia entre cada tarefa
		for(int k=0; k<tasks.size(); k++)
		{
			double dCurta = Double.MAX_VALUE;
			
			int nextX = tasks.get(k).first();
			int nextY = tasks.get(k).second();
			
			//determinar ponto mais proximo
			for(int i=-1; i<2; i++)
				for(int j=-1; j<2; j++)
				{
					Pair p = new Pair(nextX+i*largura,nextY+j*altura);
					double d = dist2Pontos(new Pair(currentX,currentY),p);
					if(d<dCurta){
						dCurta=d;
						//tasks.set(k, p);
					}
				}
			result += dCurta;
			
			currentX = nextX;
			currentY = nextY;
			
			//soma os ciclos em que está parado a comunicar
			result += 1.0;
		}
		
		double dCurta = Double.MAX_VALUE;
		
		//determinar ponto mais proximo
		for(int i=-1; i<2; i++)
			for(int j=-1; j<2; j++)
			{
				Pair p = new Pair(lastCoord.first()+i*largura,lastCoord.second()+j*largura);
				double d = dist2Pontos(new Pair(currentX,currentY),p);
				if(d<dCurta)
					dCurta=d;
			}
				
		result += dCurta;
						
		return result; 
	}
	
	protected float calcTime2(Pair lastCoord, Station station)
	{
		float result = 0;
		
		int currentX = this.x;
		int currentY = this.y;
		
		int largura = getSpace().getSizeX();
		int altura = getSpace().getSizeY();
		
		//se tiver tarefas
		if(tasks.size()>0)
		{		
			//calcula a distancia entre cada tarefa
			for(int k=0; k<tasks.size(); k++)
			{
				double dCurta = Double.MAX_VALUE;
				
				int nextX = tasks.get(k).first();
				int nextY = tasks.get(k).second();
				
				Pair p = null;
				
				//determinar ponto mais proximo
				for(int i=-1; i<2; i++)
					for(int j=-1; j<2; j++)
					{
						p = new Pair(nextX+i*largura,nextY+j*altura);
						double d = dist2Pontos(new Pair(currentX,currentY),p);
						if(d<dCurta){
							dCurta=d;
						}
					}
				result += dCurta;
				
				currentX = station.getX();
				currentY = station.getY();
				
				result += dist2Pontos(new Pair(currentX,currentY),p);
			}
		}
		
		result += (float) Math.sqrt(
				(currentX-lastCoord.first())*(currentX-lastCoord.first()) +
				(currentY-lastCoord.second())*(currentY-lastCoord.second()));
		
		return result;
	}
	
	protected float dist2Pontos(Pair p1, Pair p2) {
		
		return (float) Math.sqrt(
				(p1.first()-p2.first())*(p1.first()-p2.first()) +
				(p1.second()-p2.second())*(p1.second()-p2.second()));
	}

}
