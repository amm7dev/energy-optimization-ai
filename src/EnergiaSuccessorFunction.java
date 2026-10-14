import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class EnergiaSuccessorFunction implements SuccessorFunction {

    private static int sucesoresGenerados = 0;
    private final boolean HC;
    private final Random random = new Random();

    // Hemos anyadido este constructor en vez del constructor por defecto para poder
    // elegir que tipo de busqueda vamos a ejecutar desde el main
    public EnergiaSuccessorFunction(boolean HC) {
        this.HC = HC;
    }

    public static int getSucesoresGenerados() {
        return sucesoresGenerados;
    }

    public static void resetSucesoresGenerados() {
        sucesoresGenerados = 0;
    }

	public List getSuccessors(Object state) {
		if(HC) return getSuccessorsHC(state);
        else return getSuccessorsSA(state);
	}


    public List getSuccessorsHC(Object state) {
        List successors = new ArrayList();
		EstadoEnergia estado = (EstadoEnergia) state;
		
        // Explorar asignaciones
        for(int cliente = 0; cliente < estado.getNumClientes(); cliente++) {
            for(int central = 0; central < estado.getNumCentrales(); central++) {
                if(estado.puedeAplicarAsignar(cliente, central)) {
                    EstadoEnergia nuevo = new EstadoEnergia(estado);

                    nuevo.asignar(cliente, central);

                    successors.add(new Successor(
                        "Asignar cliente " + cliente + " a central " + central, nuevo
                    ));
                    sucesoresGenerados++;
                }
            }
        }

        // Explorar desasignaciones
        for (int cliente = 0; cliente < estado.getNumClientes(); cliente++) {
            if (estado.puedeAplicarDesasignar(cliente)) {

                EstadoEnergia nuevo = new EstadoEnergia(estado);
                nuevo.desasignar(cliente);

                successors.add(
                    new Successor(
                        "Desasignar " + cliente,
                        nuevo
                    )
                );
                sucesoresGenerados++;
            }
        }

        // Explorar reasignaciones
        for (int cliente = 0; cliente < estado.getNumClientes(); cliente++) {
            for (int central = 0; central < estado.getNumCentrales(); central++) {

                if (estado.puedeAplicarReasignar(cliente, central)) {

                    EstadoEnergia nuevo = new EstadoEnergia(estado);
                    nuevo.reasignar(cliente, central);

                    successors.add(
                        new Successor(
                            "Reasignar " + cliente + " -> " + central,
                            nuevo
                        )
                    );
                    sucesoresGenerados++;
                }
            }
        }

		return successors;
    }


    public List getSuccessorsSA(Object state) {
        List successors = new ArrayList();
        EstadoEnergia estado = (EstadoEnergia) state;

        int nclientes = estado.getNumClientes();
        int ncentrales = estado.getNumCentrales();

        List<Integer> operadores = new ArrayList<>();
        operadores.add(0); operadores.add(1); operadores.add(2);

        while(operadores.size() != 0) {
            int indice = random.nextInt(operadores.size());
            int operador = operadores.remove(indice);

            for (int intento = 0; intento < 100; ++intento) {
                int cliente = random.nextInt(nclientes);
                int central = random.nextInt(ncentrales);

                EstadoEnergia nuevo = new EstadoEnergia(estado);
                String descripcion = "";

                if (operador == 0 && estado.puedeAplicarAsignar(cliente, central)) {
                    nuevo.asignar(cliente, central);
                    descripcion = "Asignar " + cliente + " -> " + central;

                } else if (operador == 1 && estado.puedeAplicarDesasignar(cliente)) {
                    nuevo.desasignar(cliente);
                    descripcion = "Desasignar " + cliente;

                } else if (operador == 2 && estado.puedeAplicarReasignar(cliente, central)) {
                    nuevo.reasignar(cliente, central);
                    descripcion = "Reasignar " + cliente + " -> " + central;

                } else {
                    continue;
                }

                successors.add(new Successor(descripcion, nuevo));
                sucesoresGenerados++;

                return successors;
            }
        }

        return successors;
    }
}