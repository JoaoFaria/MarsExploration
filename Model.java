
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.event.SliderListener;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DTorus;
import uchicago.src.sim.util.Random;



public class Model extends SimModelImpl {
	
	private Schedule schedule;
	private DisplaySurface dsurf;
	private Object2DTorus space;
	
	private int numberOfActiveSources, numberOfEmptySources, spaceSize, maxSourceQuantity, numberOfSpotters, numberOfProducers, numberOfTransporters, walkmode, transportersCapacity;
	
	private long velocidadeJogo;
	
	private ArrayList<Spotter> spotters;
	private ArrayList<Transporter> transporters;
	private ArrayList<Producer> producers;
	private Station armazem;
	
	private Map<Pair , Source> mapa;	
	
	public Model() {
		this.spaceSize = 40;
		this.numberOfActiveSources = 100;//50;
		this.numberOfEmptySources = 50;//50;
		this.maxSourceQuantity = 100;
		this.numberOfSpotters = 8;//20;
		this.numberOfTransporters = 8;//20;
		this.numberOfProducers = 8;//20;
		this.velocidadeJogo = 0;
		this.walkmode = 2; //1-random; 2-probabilidades
	}
	
	public void begin() {
		buildModel();
		buildDisplay();
		buildSchedule();
	}

	public String[] getInitParam() {
		return new String[] { "spaceSize" , "numberOfActiveSources" , "numberOfEmptySources", "numberOfSpotters", "maxSourceQuantity" , "numberOfProducers", "numberOfTransporters", "walkmode"};
	}
	
	public int getMaxSourceQuantity(){
		return maxSourceQuantity;
	}

	public String getName() {
		return "Mars Exploration Model";
	}

	public Schedule getSchedule() {
		return schedule;
	}
	
	public int getSpaceSize() {
		return spaceSize;
	}
	
	public void setSpaceSize(int spaceSize) {
		this.spaceSize = spaceSize;
	}
	
	public int getNumberOfSpotters() {
		return numberOfSpotters;
	}
	
	public void setNumberOfSpotters(int numberOfSpotters) {
		this.numberOfSpotters = (numberOfSpotters==0)?1:numberOfSpotters;
	}
	
	public int getNumberOfTransporters() {
		return numberOfTransporters;
	}
	
	public void setNumberOfTransporters(int numberOfTransporters) {
		this.numberOfTransporters = (numberOfTransporters==0)?1:numberOfTransporters;	
	}

	public int getNumberOfProducers() {
		return numberOfProducers;
	}
	
	public void setNumberOfProducers(int numberOfProducers) {
		this.numberOfProducers = (numberOfProducers==0)?1:numberOfProducers;
	}
	
	public int getNumberOfActiveSources() {
		return numberOfActiveSources;
	}
	
	public void setNumberOfActiveSources(int numberOfActiveSources) {
		this.numberOfActiveSources = numberOfActiveSources;
	}
	
	public int getNumberOfEmptySources() {
		return numberOfEmptySources;
	}
	
	public void setNumberOfEmptySources(int numberOfEmptySources) {
		this.numberOfEmptySources = numberOfEmptySources;
	}
	
	public void setMaxSourceQuantity(int maxSourceQuantity){
		this.maxSourceQuantity = (maxSourceQuantity==0)?1:maxSourceQuantity;
	}
	
	public void setVelocidadeJogo(int velJogo) {
		this.velocidadeJogo = velJogo;
	}
	
	public long getVelocidadeJogo() {
		return velocidadeJogo;
	}
	
	public int getWalkmode() {
		return walkmode;
	}
	
	public void setWalkmode(int walk) {
		this.walkmode = walk;
		
	}
	
	public int getTransportersCapacity() {
		return transportersCapacity;
	}
	
	public void setTransportersCapacity(int tc) {
		this.transportersCapacity = tc;
		
	}

	public void setup() {
		schedule = new Schedule();
		if (dsurf != null) dsurf.dispose();
		dsurf = new DisplaySurface(this, "Mars Exploration Display");
		registerDisplaySurface("Mars Exploration Display", dsurf);
		
		setupCustomAction();
	}
	
	public void buildModel() {
		mapa = new LinkedHashMap<Pair,Source>();
		
		//sources = new ArrayList<Source>();
		armazem = new Station(spaceSize/2,spaceSize/2, space);
		spotters = new ArrayList<Spotter>();
		producers = new ArrayList<Producer>();
		transporters = new ArrayList<Transporter>();
		
		
		space = new Object2DTorus(spaceSize, spaceSize);
		space.putObjectAt(armazem.getX(), armazem.getY(), armazem);
		
		
		//EMPTY SOURCES
		for (int i = 0; i<numberOfEmptySources; i++) {
			int x, y;
			do {
				x = Random.uniform.nextIntFromTo(0, space.getSizeX() - 1);
				y = Random.uniform.nextIntFromTo(0, space.getSizeY() - 1);
			} while (space.getObjectAt(x, y) != null || (x == 1 && y == 1));
			
			Source src = new Source(x, y, 0, space);
			space.putObjectAt(x, y, src);
			
			//sources.add(src);
			mapa.put(new Pair(x,y), src);
		}
		
		//ACTIVE SOURCES
		for (int i = 0; i<numberOfActiveSources; i++) {
			int x, y;
			do {
				x = Random.uniform.nextIntFromTo(0, space.getSizeX() - 1);
				y = Random.uniform.nextIntFromTo(0, space.getSizeY() - 1);
			} while (space.getObjectAt(x, y) != null || (x == 1 && y == 1) || mapa.containsKey(new Pair(x,y)));
			
			Source src = new Source(x, y, Random.uniform.nextIntFromTo(1, maxSourceQuantity), space); ////////////mudar
			space.putObjectAt(x, y, src);
			//sources.add(src);
			mapa.put(new Pair(x,y), src);
		}
		
		int id = 1;
		
		//SPOTTERS
		for (int i = 0; i<numberOfSpotters; i++) {
			int x, y;
			x = space.getSizeX()/10;//armazem.getX();
			y = space.getSizeY()/2;//armazem.getY();
			
			Spotter spt = new Spotter(id, x, y, space, new Color(0,0,255), mapa, walkmode);
			id++;
			space.putObjectAt(x, y, spt);
			spotters.add(spt);
		}
		
		//TRANSPORTERS
		for (int i = 0; i<numberOfTransporters; i++) {
			int x, y;
			x = armazem.getX();
			y = armazem.getY();
			
			Transporter tsp = new Transporter(id, x, y, space, new Color(255,255,0), mapa,armazem, walkmode, transportersCapacity);
			id++;
			space.putObjectAt(x, y, tsp);
			transporters.add(tsp);
		}
		
		//PRODUCERS
		int y;
		for (int i = 0, x=8*space.getSizeX()/10; i<numberOfProducers; i++,x+=space.getSizeX()/10) {
			//int x, y;
			//x = 80;//armazem.getX();
			y = space.getSizeY()/2;//armazem.getY();
			
			Producer prd = new Producer(id, x, y, space, new Color(255,0,0) , mapa, walkmode);
			id++;
			space.putObjectAt(x, y, prd);
			producers.add(prd);
		}
	}

	private void buildDisplay() {
		ArrayList<Object> listaObjectos = new ArrayList<Object>();
		for(Map.Entry<Pair,Source> entry : mapa.entrySet()){
			Source src = (Source) entry.getValue();
			listaObjectos.add(src);
		}
		listaObjectos.add(armazem);
		listaObjectos.addAll(spotters);
		listaObjectos.addAll(transporters);
		listaObjectos.addAll(producers);
		
		
		
		// space and display surface
		Object2DDisplay display = new Object2DDisplay(space);
		display.setObjectList(listaObjectos);
		dsurf.addDisplayableProbeable(display, "Objects Space");
		
		dsurf.display();

	}

	private void buildSchedule() {
		schedule.scheduleActionBeginning(0, new MainAction());
		schedule.scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
	}
	
	private void setupCustomAction () {

		modelManipulator.init ();

		// this will add a slider to the Custom Action tab
		modelManipulator.addSlider ("Velocidade de jogo", 0, 10, 1, new SliderListener () {
			public void execute () {
					if (isSlidingLeft && !isAdjusting) {
						velocidadeJogo += value*10;
					} else if (!isSlidingLeft && !isAdjusting) {
						velocidadeJogo -= value*10;
					}
					velocidadeJogo = (velocidadeJogo<0) ? 0 : velocidadeJogo;
					velocidadeJogo = (velocidadeJogo>300) ? 300 : velocidadeJogo;
			}
		});
	}

	class MainAction extends BasicAction {

		public void execute() {	
			
			try {
				Thread.sleep(velocidadeJogo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i = 0; i < numberOfProducers; i++) {
				Producer prd = producers.get(i);
				if(prd.getStatus()=='f')
					prd.walk();
				prd.actions(spotters, transporters);
			}
			// iterate through all agents
			for(int i = 0; i < numberOfSpotters; i++) {
				Spotter spt = spotters.get(i);
				if(spt.getStatus()=='f')
					spt.walk();
				spt.actions(producers,transporters);
			}
			for(int i = 0; i < numberOfTransporters; i++) {
				Transporter tsp = transporters.get(i);
				if(tsp.getStatus()=='f')
					tsp.walk();
				tsp.actions(spotters, producers);
			}
			
		}

	}


	public static void main(String[] args)throws Exception {
		SimInit init = new SimInit();
		init.loadModel(new Model(), null, false);
		
	}
}
