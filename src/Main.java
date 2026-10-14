import IA.Energia.Centrales;
import IA.Energia.Clientes;

import aima.search.framework.Problem;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

public class Main {

    public static void main(String[] args) throws Exception {

        // 1. Generar escenario
        int[] cent = {5, 10, 25}; // numero de centrales de cada tipo
        int seed = 600; // semilla para las funciones aleatorias

        int nclientes = 1100; // numero de clientes
        double[] propc = {0.25, 0.30, 0.45}; // proporcion de clientes de cada tipo
        double propg = 0.75; // proporcion de clientes garantizados

        Centrales centrales = new Centrales(cent, seed); // array de centrales
        Clientes clientes = new Clientes(nclientes, propc, propg, seed); // array de clientes

        // 2. Crear estado inicial
        EstadoEnergia estadoInicial = new EstadoEnergia(centrales, clientes,
        EstadoEnergia.EstrategiaInicial.ESTRATEGIA_1);
        // el ultimo parametro es la primera estrategia para generar la solucion inicial

        // 3. Confirmar que la solucion es valida analizando la terminal y imprimir mensaje
        System.out.println("Empieza la busqueda con algoritmo: ");

        // 4. Imprimir estado inicial
        //estadoInicial.imprimirEstado();

        // 5. Crear problema
        boolean cercaHC = true;

        Problem problema = new Problem(estadoInicial, new EnergiaSuccessorFunction(cercaHC),
        new EnergiaGoalTest(), new EnergiaHeuristicFunction());

        // 6. Crear searchs  
        HillClimbingSearch searchHC = new HillClimbingSearch();
        SimulatedAnnealingSearch searchSA = new SimulatedAnnealingSearch();

        EnergiaSuccessorFunction.resetSucesoresGenerados();

        // 7. Crear search agent
        SearchAgent agent;
        EstadoEnergia estadoFinal;

        long startTime = System.currentTimeMillis();
        if(cercaHC) {
            System.out.println("HILL CLIMBING\n");
            agent = new SearchAgent(problema, searchHC);
            estadoFinal = (EstadoEnergia) searchHC.getGoalState();
        } else {
            System.out.println("SIMULATED ANNEALING\n");
            agent = new SearchAgent(problema, searchSA);
            estadoFinal = (EstadoEnergia) searchSA.getGoalState();
        }
        long finishTime = System.currentTimeMillis();

        System.out.println("Beneficio inicial: " + estadoInicial.getBeneficio());
        System.out.println("Beneficio final: " + estadoFinal.getBeneficio());
        System.out.println("Inc Beneficio: " + (estadoFinal.getBeneficio() - estadoInicial.getBeneficio()) + "\n");
        System.out.println("Tiempo de Busqueda: " + (finishTime - startTime) + "ms\n");

        System.out.println("Acciones:");
        System.out.println(agent.getActions() + "\n");

        System.out.println("Metricas:");
        System.out.println(agent.getInstrumentation());
        System.out.println("{nodesGenerated=" + EnergiaSuccessorFunction.getSucesoresGenerados() + "}");
    }
}
