import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;

import uchicago.src.sim.space.Object2DTorus;


public class Producer extends Agent {

	public Producer(int id, int x, int y, Object2DTorus space, Color color, Map<Pair,Source> mapa, int walkmode) {
		super(id, x, y, space, color, mapa, walkmode);
	}

	public void actions(ArrayList<Spotter> spotters, ArrayList<Transporter> transporters) 
	{
		for(int i=0;i<messages.size();i++)
		{
			if(messages.get(i).type=='p')
			{
				if(messages.get(i).agentType=='s')
				{
					talkToSpotters(messages.get(i).id, new Message(this.id,'r', calcTime1(messages.get(i).coord)), spotters);				
					
				}
				messages.remove(i);
				i--;
			}
			else if(messages.get(i).type=='a')
			{
				Pair lastPos = new Pair(x,y);
				if(tasks.size()>0)
					lastPos.set(tasks.get(tasks.size()-1).first(), tasks.get(tasks.size()-1).second());
				
				Double dCurta = Double.MAX_VALUE;
				
				int largura = getSpace().getSizeX();
				int altura = getSpace().getSizeY();
				
				Pair resP = lastPos;
				//determinar ponto mais proximo
				for(int k=-1; k<2; k++)
					for(int j=-1; j<2; j++)
					{
						Pair p = new Pair(messages.get(i).coord.first()+k*largura,messages.get(i).coord.second()+j*altura);
						double d = dist2Pontos(lastPos,p);
						if(d<dCurta){
							dCurta=d;
							resP = p;
						}
					}
				
				//coloca na lista de tarefas
				tasks.add(resP);
				messages.remove(i);
				i--;
			}
		}
		
		
		//se tem source trata dela
		if(mapa.containsKey(new Pair(this.getX(),this.getY())))
		{
			//retira minério
			if(tasks.contains(new Pair(x,y)))
			{
				tasks.get(tasks.lastIndexOf(new Pair(x,y))).set(this.getX(), this.getY());
				x=this.getX();
				y=this.getY();

				if(this.status=='f')
				{
					mapa.get(new Pair(x,y)).esvazia(Integer.MAX_VALUE);
					talkToTransporters(-1, new Message(this.id,'p', new Pair(this.getX(),this.getY()), 'p'), transporters);
					status='w';
					
				}
				else if(this.status=='w')
				{
					Message m1 = new Message(-2,'r',Integer.MAX_VALUE);
					//escolhe entre os spotters
					for(int i=0;i<messages.size();i++)
					{
						if(messages.get(i).type=='r')
						{
							if(messages.get(i).time<m1.time)
								m1=messages.get(i);
							messages.remove(i);
							i--;
						}
					}
					
					//comunica ao transporter escolhido
					if(m1.id!=-2)
					{
						talkToTransporters(m1.id, new Message(this.id,'a', new Pair(this.getX(),this.getY()),'p'), transporters);
						this.status='f';
						tasks.remove(new Pair(x,y));
						
						if(tasks.size()>0)
						{
							int tmpX = tasks.get(0).first();
							if(tmpX>=space.getSizeX())
								tmpX-=space.getSizeX();
							else if(tmpX<0)
								tmpX+=space.getSizeX();
							
							int tmpY = tasks.get(0).second();
							if(tmpY>=space.getSizeY())
								tmpY-=space.getSizeY();
							else if(tmpY<0)
								tmpY+=space.getSizeY();
							
							Pair newTask = new Pair(tmpX,tmpY);
							
							if(dist2Pontos(tasks.get(0), new Pair(this.getX(),this.getY()))>
									dist2Pontos(newTask, new Pair(this.getX(),this.getY())))
								tasks.get(0).set(tmpX, tmpY);
						}
					}
				}
			}
		}
	}
}
