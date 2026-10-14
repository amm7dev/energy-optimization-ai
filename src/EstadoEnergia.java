import IA.Energia.Centrales;
import IA.Energia.Clientes;
import IA.Energia.Central;
import IA.Energia.Cliente;
import IA.Energia.VEnergia;

public class EstadoEnergia {

    // ===============
    // DATOS ESCENARIO
    // ===============

    /* Estructura de datos que representa las centrales de cada instancia
       concreta del problema. */
    private static Centrales centrales;

    /* Estructura de datos que representa los clientes de cada instancia
       concreta del problema. */
    private static Clientes clientes;

    /* Enumeracion para elegir como se construye la solucion inicial. */
    public enum EstrategiaInicial {
        ESTRATEGIA_1,
        ESTRATEGIA_2
    }

    /* Constante para representar que un cliente no esta asignado. */
    private static final int NO_ASIGNADO = -1;


    // ============
    // DATOS ESTADO
    // ============

    // infoClientes[cliente] = central
    // algun valor especial si no esta servido
    private int[] infoClientes;

    // ocupacionCentrales[central] = MW utilizados
    private double[] ocupacionCentrales;


    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    /*
        Crea un estado inicial valido del problema dados unos clientes, centrales
        y una estrategia

            Params: centrales, clientes, estrategia

            Pre: El esenario debe estar correctamente generado y la estrategia
            debe existir en la enumeracion de estrategias.

            Post: Se ha construido el primer estado desde el que comenzara la
            busqueda local
    */
    public EstadoEnergia(Centrales centrales, Clientes clientes,
    EstrategiaInicial estrategia) {

        EstadoEnergia.centrales = centrales;
        EstadoEnergia.clientes = clientes;

        infoClientes = new int[clientes.size()];
        ocupacionCentrales = new double[centrales.size()];

        // Iniciamos todos los clientes a NO_ASIGNADO representando que no estan asignados
        for (int i = 0; i < infoClientes.length; ++i) {
            infoClientes[i] = NO_ASIGNADO;
        }

        // Construimos solucion inicial segun el valor del parametro
        if (estrategia == EstrategiaInicial.ESTRATEGIA_1) {
            generarInicial1();
        }
        else {
            generarInicial2();
        }

        if(!esValido()) {
            System.out.println("\nError: la solucion inicial no es valida.");
            System.exit(1);
        } else {
            System.out.println("\nLa solucion inicial es valida!");
        }
    }

    /*
        Crea una copia independiente de un estado existente.

            Params: e

            Pre: El estado e debe estar correctamente inicializado.

            Post: Se ha creado un nuevo estado con la misma asignacion de clientes
                y ocupacion de centrales que e, pero con estructuras de datos
                independientes.
    */
    public EstadoEnergia(EstadoEnergia e) {
        this.infoClientes = e.infoClientes.clone();
        this.ocupacionCentrales = e.ocupacionCentrales.clone();
    }

    /*
        Construye una solucion inicial para el problema de manera aleatoria. Simplemente,
        para cada cliente busca la primera central disponible para asignarse, y asi con
        todos.
    */
    private void generarInicial1() {
        int central = 0;
        for(int i = 0; i < clientes.size(); i++) {
            if(esGarantizado(i)) {
                while(central < centrales.size() && infoClientes[i] == NO_ASIGNADO) {
                    if(puedeAsignarse(i, central)) asignar(i, central);
                    else central++;
                }
                central = 0;
            }
        }
    }

    /*
        Construye una solucion inicial para el problema de manera aleatoria. Simplemente,
        para cada cliente busca la central disponible que tenga mas cerca, tratando de 
        minimizar los costes por perdidas. Sin embargo, esto puede comportar que abra 
        muchas mas centrales de lo necesario.
    */
    private void generarInicial2() {
        for(int i = 0; i < clientes.size(); i++) {
            if(esGarantizado(i)) {
                Cliente cl = clientes.get(i);
                int centralBona = -1;
                double dmin = Double.POSITIVE_INFINITY;
                for(int j = 0; j < centrales.size(); j++) {
                    Central ct = centrales.get(j);
                    double d = distancia(cl.getCoordX(), cl.getCoordY(), ct.getCoordX(), ct.getCoordY());
                    if(puedeAsignarse(i, j)) {
                        if(d < dmin) {
                            dmin = d;
                            centralBona = j;
                        }
                    }
                }
                asignar(i, centralBona);
            }
        }
    }


    // ==========================================
    // OPERADORES
    // ==========================================

    /*
        Asigna un cliente a una central.

            Params: ID_CLIENTE, ID_CENTRAL

            Pre: El cliente ID_CLIENTE no esta asignado y la asignacion no
                 excede la capacidad maxima de la central
            Post: Se ha asignado el cliente ID_CLIENTE a la central ID_CENTRAL
    */
    public void asignar(int id_cliente, int id_central) {
        infoClientes[id_cliente] = id_central;
        ocupacionCentrales[id_central] += consumoEfectivo(id_cliente, id_central);
    }

    /*
        Desasigna un cliente de una central.

            Params: ID_CLIENTE

            Pre: El cliente esta asignado y no esta garantizado.
            Post: Se ha desasignado el cliente ID_CLIENTE de la central infoClientes[ID_CLIENTE]
    */
    public void desasignar(int id_cliente) {
        int id_central = infoClientes[id_cliente];

        ocupacionCentrales[id_central] -= consumoEfectivo(id_cliente, id_central);
        infoClientes[id_cliente] = NO_ASIGNADO;
    }

    /*
        Reasigna un cliente de una central a otra.

            Params: ID_CLIENTE, ID_CENTRAL_NUEVA

            Pre: El cliente esta asignado y la asignacion no excede la capacidad 
                 maxima de la central.
            Post: Se ha asignado el cliente ID_CLIENTE a la central ID_CENTRAL_NUEVA
    */
    public void reasignar(int id_cliente, int id_central_nueva) {
        int id_central_vieja = infoClientes[id_cliente];

        ocupacionCentrales[id_central_vieja] -= consumoEfectivo(id_cliente, id_central_vieja);

        ocupacionCentrales[id_central_nueva] += consumoEfectivo(id_cliente, id_central_nueva);

        infoClientes[id_cliente] = id_central_nueva;
    }


    // ====================
    // GETTERS
    // ====================

    /*
        Devuelve la central a la que esta asignado el cliente id_cliente
        o NO_ASIGNADO si no lo esta
    */
    public int getCentralCliente(int id_cliente) {
        return infoClientes[id_cliente];
    }

    /*
        Devuelve la ocupacion de la central id_central
    */
    public double getOcupacionCentral(int id_central) {
        return ocupacionCentrales[id_central];
    }

    /*
        Devuelve el numero de clientes del problema
    */
    public int getNumClientes() {
        return clientes.size();
    }

    /*
        Devuelve el numero de centrales del problema
    */
    public int getNumCentrales() {
        return centrales.size();
    }

    /*
        Devuelve el beneficio obtenido con la configuracion del estado

        Consiste en dos bucles:
            1) Iteramos sobre clientes para calcular ingresos y indemnizaciones
            2) Iteramos sobre centrales para calcular gastos por funcionamiento y parada
    */
    public double getBeneficio() {
        try {
            double ingresos = 0.0, gastosCentrales = 0.0, indemnizaciones = 0.0;

            // calculamos ingresos e indemnizaciones
            for(int cliente = 0; cliente < clientes.size(); cliente++) {
                if(infoClientes[cliente] != NO_ASIGNADO) {
                    // cliente asignado
                    ingresos += calcularIngreso(cliente);
                } else {
                    // cliente no asignado
                    if(!esGarantizado(cliente)) {
                        indemnizaciones += calcularIndemnizacion(cliente);
                    }
                }
            }
            // calculamos costes por funcionamiento y parada
            for(int central = 0; central < centrales.size(); central++) {
                gastosCentrales += calcularGastoCentral(central);
            }

            return ingresos - (gastosCentrales + indemnizaciones);
        }
        catch (Exception e) {
            System.out.println("Error calculando beneficio.");
            System.exit(1);
            return 0.0;
        }
    }


    // ===============================
    // HELPERS - para agilizar metodos
    // ===============================

    /*
        Calcula la distancia euclidea entre los puntos (x1, y1) y (x2, y2)
    */
    private double distancia(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;

        return Math.sqrt(dx * dx + dy * dy);
    }

    /*
        Informa sobre si un cliente es garantizado o no
    */
    private boolean esGarantizado(int id_cliente) {
        return clientes.get(id_cliente).getContrato() == Cliente.GARANTIZADO;
    }

    /*
        Devuelve el consumoEfectivo del cliente id_cliente si se asignara a la
        central id_central teniendo en cuenta la perdida por distancia
    */
    private double consumoEfectivo(int id_cliente, int id_central) {
        Cliente cl = clientes.get(id_cliente);
        Central ct = centrales.get(id_central);

        double consumo = cl.getConsumo();
        double d = distancia(cl.getCoordX(), cl.getCoordY(), ct.getCoordX(), ct.getCoordY());
        double perdida = VEnergia.getPerdida(d);
        return consumo * (1.0 + perdida);
    }

    /* 
        Consulta si el cliente id_cliente puede asignarse a la central id_central
    */
    public boolean puedeAsignarse(int id_cliente, int id_central) {
        Central ct = centrales.get(id_central);

        double prod = ct.getProduccion();
        double restante = prod - ocupacionCentrales[id_central];

        return consumoEfectivo(id_cliente, id_central) <= restante;
    }

    /*
        Imprime el estado actual del problema
    */
    public void imprimirEstado() {
        for (int i = 0; i < clientes.size(); ++i) {
            System.out.println(
                "Cliente " + i +
                " garantizado=" + esGarantizado(i) +
                " central=" + infoClientes[i]
            );
        }
        System.out.println("Asignaciones:");

        for (int i = 0; i < infoClientes.length; i++) {
            System.out.println(
                "Cliente " + i + " -> Central " + infoClientes[i]
            );
        }

        System.out.println("\nOcupacion centrales:");

        for (int i = 0; i < ocupacionCentrales.length; i++) {
            System.out.println(
                "Central " + i + " -> " + ocupacionCentrales[i]
            );
        }
    }

    /*
        Comprueba si una solucion inicial es valida
    */
    private boolean esValido() {
        for (int i = 0; i < clientes.size(); i++) {
            if (esGarantizado(i) && infoClientes[i] == NO_ASIGNADO)
                return false;
        }

        for (int c = 0; c < centrales.size(); c++) {
            if (ocupacionCentrales[c] > centrales.get(c).getProduccion())
                return false;
        }

        return true;
    }

    /*
        Helper para generadora de sucesores. Comprueba si el operador se puede aplicar
        a un par de cliente y central concreto
    */
    public boolean puedeAplicarAsignar(int id_cliente, int id_central) {
        return infoClientes[id_cliente] == NO_ASIGNADO && !esGarantizado(id_cliente)
        && puedeAsignarse(id_cliente, id_central);
    }

    /*
        Helper para generadora de sucesores. Comprueba si el operador se puede aplicar
        a un par de cliente y central concreto
    */
    public boolean puedeAplicarDesasignar(int id_cliente) {
        return infoClientes[id_cliente] != NO_ASIGNADO && !esGarantizado(id_cliente);
    }

    /*
        Helper para generadora de sucesores. Comprueba si el operador se puede aplicar
        a un par de cliente y central concreto
    */
    public boolean puedeAplicarReasignar(int id_cliente, int id_central_nueva) {
        int id_central_vieja = infoClientes[id_cliente];

        return id_central_vieja != NO_ASIGNADO && id_central_vieja != id_central_nueva
        && puedeAsignarse(id_cliente, id_central_nueva);
    }

    /*
        Calcula el ingreso de un cliente. Helper para la funcion getBeneficio()
    */
    public double calcularIngreso(int idCliente) throws Exception {
        Cliente cliente = clientes.get(idCliente);

        double consumo = cliente.getConsumo();
        double tarifa; // € / MW
        int tipo = cliente.getTipo();

        if(esGarantizado(idCliente)) {
            tarifa = VEnergia.getTarifaClienteGarantizada(tipo);
        } else {
            tarifa = VEnergia.getTarifaClienteNoGarantizada(tipo);
        }

        return consumo * tarifa;
    }

    /*
        Calcula la indemnizacion a pagar a un cliente. Helper para la funcion
        getBeneficio()
    */
    public double calcularIndemnizacion(int idCliente) throws Exception {
        Cliente cliente = clientes.get(idCliente);

        double consumo = cliente.getConsumo();
        int tipo = cliente.getTipo();

        return consumo * VEnergia.getTarifaClientePenalizacion(tipo);
    }

    /*
        Calcula el gasto de una central. Helper para la funcion getBeneficio()
    */
    public double calcularGastoCentral(int idCentral) throws Exception {
        Central central = centrales.get(idCentral);

        double gasto = 0.0;
        int tipo = central.getTipo();

        if(ocupacionCentrales[idCentral] == 0) {
            gasto = VEnergia.getCosteParada(tipo);
        } else {
            gasto = VEnergia.getCosteMarcha(tipo);

            double costeProd = VEnergia.getCosteProduccionMW(tipo);

            gasto += costeProd * central.getProduccion();
        }

        return gasto;
    }
}