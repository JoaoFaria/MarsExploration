import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;

import uchicago.src.sim.space.Object2DTorus;


public class Spotter extends Agent {
	
	private ArrayList<Pair> visited;

	public Spotter(int id, int x, int y, Object2DTorus space, Color color, Map<Pair,Source> mapa, int walkmode){
		super(id, x, y, space, color, mapa, walkmode);
		visited=new ArrayList<Pair>();
	}
	
	public void actions(ArrayList<Producer> producers,ArrayList<Transporter> transporters) 
	{
		//trata das mensagens
		for(int i=0;i<messages.size();i++)
		{
			if(messages.get(i).type=='p')
			{
				//calcular o tempo até atender pedido
				if(messages.get(i).agentType=='p'){
					talkToProducers(messages.get(i).id, new Message(this.id,'r', calcTime1(messages.get(i).coord)), producers);
				}
				else if(messages.get(i).agentType=='t')
					talkToTransporters(messages.get(i).id, new Message(this.id,'r', calcTime1(messages.get(i).coord)), transporters);
				messages.remove(i);
				i--;
			}
			else if(messages.get(i).type=='a' && walkmeter.get(messages.get(i).coord.first()).get(messages.get(i).coord.second())==1)
			{
				
				//coloca na lista de tarefas se o destino ainda não foi visitado
				if(!visited.contains(messages.get(i).coord))
				{
					
					Pair lastPos = new Pair(this.x,this.y);
					for(int j=0; j<tasks.size(); j++)
					{
						lastPos.set(tasks.get(j).first(), tasks.get(j).second());
					}
					
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
					
					tasks.add(resP);
				}
				messages.remove(i);
				i--;
			}
			
			
		}
		
		
		
		//se tem source trata dela
		if(mapa.containsKey(new Pair(this.getX(),this.getY())))
		{
			//retira a tarefa já feita e marca o local como visitado
			if(tasks.contains(new Pair(x,y)))
			{
				tasks.get(tasks.lastIndexOf(new Pair(x,y))).set(this.getX(), this.getY());
				x=this.getX();
				y=this.getY();
				
				tasks.remove(new Pair(x,y));
				visited.add(new Pair(x,y));
			}
			
			if(mapa.get(new Pair(this.getX(),this.getY())).getQuantidade()>0)
			{
				if(this.status=='f')
				{
					//questiona producers
					talkToProducers(-1, new Message(this.id,'p', new Pair(this.getX(),this.getY()), 's'), producers);
					this.status='w';
				}
				else if(this.status=='w')
				{
					Message m1 = new Message(-2,'r',Integer.MAX_VALUE);
					//escolhe entre os producers
					for(int i=0; i<messages.size(); i++)
					{
						if(messages.get(i).type=='r')
						{
							if(messages.get(i).time<m1.time)
								m1=messages.get(i);
							messages.remove(i);
							i--;
						}
					}
					
					
					//comunica ao producer escolhido
					if(m1.id!=-2)
					{
						
						talkToProducers(m1.id, new Message(this.id,'a', new Pair(this.getX(),this.getY()),'s'), producers);
						this.status='f';
					}
				}
				
			}
			else if(this.status=='w')
					this.status='f';
		}
	}
	
	
}

