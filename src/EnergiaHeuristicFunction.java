import aima.search.framework.HeuristicFunction;

public class EnergiaHeuristicFunction implements HeuristicFunction {

	public double getHeuristicValue(Object state) {
		EstadoEnergia estado = (EstadoEnergia) state;
		
        return -estado.getBeneficio();
	}

}