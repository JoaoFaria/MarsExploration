import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;

import uchicago.src.sim.space.Object2DTorus;


public class Transporter extends Agent {
	
	private Station station;
	private int capacidade;
	private int capacidadeOcupada;

	public Transporter(int id, int x, int y, Object2DTorus space, Color color, Map<Pair,Source> mapa,Station station, int capacidade, int walkmode) {
		super(id, x, y, space, color, mapa, walkmode);
		this.station=station;
		this.capacidadeOcupada=0;
		this.capacidade=100;
	}
	
	public void actions(ArrayList<Spotter> spotters, ArrayList<Producer> producers) {

		for(int i=0;i<messages.size();i++){
			if(messages.get(i).type=='p'){
				if(messages.get(i).agentType=='p')
				{
					
					talkToProducers(messages.get(i).id, new Message(this.id,'r', calcTime2(messages.get(i).coord,station)), producers);				
					
				}
				messages.remove(i);
				i--;
			}
			else if(messages.get(i).type=='a')
			{
				//coloca na lista de tarefas
				tasks.add(messages.get(i).coord);

				Pair lastPos = new Pair(station.getX(),station.getY());
				
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
				
				messages.remove(i);
				i--;
			}
		}
		
		if(station.getX()==this.x && station.getY()==this.y)
		{
			station.addMineral(this.capacidadeOcupada);
			this.capacidadeOcupada=0;
			if(tasks.size()>0)
				if(tasks.get(0).equals(new Pair(station.getX(),station.getY())))
					tasks.remove(0);
		}
		
		//se tem source trata dela
		else if(mapa.containsKey(new Pair(this.getX(),this.getY())))
		{	
			//retira minério explorado
			if(tasks.contains(new Pair(x,y)))
			{
				this.capacidadeOcupada = mapa.get(new Pair(this.getX(),this.getY())).transporta(capacidade-capacidadeOcupada);
				tasks.remove(new Pair(x,y));
				tasks.add(0, new Pair(station.getX(),station.getY()));
				if(mapa.get(new Pair(this.getX(),this.getY())).getRetirado()>0)
					tasks.add(1, new Pair(this.getX(),this.getY()));
			}
			
			else if(this.status=='f')
			{
				//questiona spotters
				talkToSpotters(-1, new Message(this.id,'p', new Pair(this.getX(),this.getY()), 't'), spotters);
				this.status='w';
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
				
				//comunica ao spotter escolhido
				if(m1.id!=-2)
				{
					talkToSpotters(m1.id, new Message(this.id,'a', new Pair(this.getX(),this.getY()),'t'), spotters);
					this.status='f';
				}
			}	
		}
	}

}
