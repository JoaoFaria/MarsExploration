
public class Message {
	//identificador emissor
	int id;
	//x,y
	Pair coord;
	//resposta - r; pedido - p; atribuição - a
	char type;
	//tempo atendimento
	float time;
	//spotter - s; producer - p; transporter - t
	char agentType;
	
	Message(int id, char type, Pair coord, char agType)
	{
		this.id = id;
		this.coord = coord;
		this.type = type;
		this.agentType = agType;
	}
	
	Message(int id, char type, float time)
	{
		this.id = id;
		this.time = time;
		this.type = type;
	}
	
	public String toString(){
		return "Coordenadas: "+coord.first()+";"+coord.second()+" type: "+type+" tempo: "+time+ " tipo de agente: "+agentType;
	}
	
}
