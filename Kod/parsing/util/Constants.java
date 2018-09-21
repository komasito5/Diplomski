package parsing.util;

public class Constants {

    public static final String COMPONENT_START = "***";
    public static final String FIELD_VALUE_SEPARATOR = ":";
    public static final String WHITESPACE_REGEX = "\\s";
    public static final String EMPTY_STRING = "";
    public static final int FIELD_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    public static final String TYPE = "Tip";
    public static final String QUEUE = "Red";
    public static final String SERVER = "Server";
    public static final String JOIN = "Spoj";
    public static final String BRANCHING = "Grananje";
    public static final String SIMULATION = "Simulacija";

    public static final String ID = "Id";

    public static final String PRINCIPLE = "PrincipRada";
    public static final String FIFO = "FIFO";
    public static final String LIFO = "LIFO";
    public static final String PRIORITY_QUEUE = "PrioritetniRed";
    public static final String RANDOM = "SlucajanIzbor";

    public static final String SERVER_PRINCIPLE = "PrincipOdabiraServera";
    public static final String FIRST_FREE = "PrviSlobodan";
    public static final String LONGEST_FREE = "NajduzeSlobodan";

    public static final String EXIT = "Izlaz";

    public static final String BRANCH = "Grana";
    public static final String OUTGOING_TRAFFIC_SEPARATOR = "-";
    public static final int OUTGOING_TRAFFIC_ID_INDEX = 0;
    public static final int OUTGOING_TRAFFIC_PROBABILITY_INDEX = 1;
    public static final String BRANCH_ALREADY_DEFINED = "Grana prema komponenti ciji je id ";
    public static final String DEFINED = " je vec definisana";

    public static final String DISTRIBUTION = "Raspodela";
    public static final String EXPONENTIAL_DISTRIBUTION = "Eksponencijalna";
    public static final String UNIFORM_DISTRIBUTION = "Uniformna";

    public static final String MIN_EXECUTION_TIME = "MinimalnoVremeObrade";
    public static final String MIN_EXECUTION_TIME_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Minimalno vreme obrade je vec definisano u deskriptoru ove komponente";
    public static final String MIN_EXECUTION_TIME_NOT_DEFINED = "Minimalno vreme obrade nije definisano u deskriptoru ove komponente";

    public static final String MAX_EXECUTION_TIME = "MaksimalnoVremeObrade";
    public static final String MAX_EXECUTION_TIME_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Maksimalno vreme obrade je vec definisano u deskriptoru ove komponente";
    public static final String MAX_EXECUTION_TIME_NOT_DEFINED = "Maksimalno vreme obrade nije definisano u deksriptoru ove komponente";
    public static final String MIN_EXECUTION_TIME_GREATER_THAN_MAX = "Minimalno vreme obrade je vece od maksimalnog vremena obrade";

    public static final String EXECUTION_TIME = "ProsecnoVremeObrade";

    public static final String REQUEST_NUMBER = "BrojZahteva";

    public static final String REQUEST_PRIORITY = "PrioritetZahteva";
    public static final String REQUEST_PRIORITY_SEPARATOR = ",";
    public static final String REQUEST_PRIORITY_ERROR = "Prioriteti nisu definisani za sve zahteve";

    public static final String REQUEST_STARTING_QUEUE = "InicijalniRedZahteva";
    public static final String REQUEST_STARTING_QUEUE_SEPARATOR = ",";
    public static final String REQUEST_STARTING_QUEUE_ERROR = "Inicijalni redovi nisu definisani za sve zahteve";

    public static final String WARMUP_TIME = "VremeZagrevanja";

    public static final String SIMULATION_TIME = "VremeSimulacije";


    // Parsing error constants
    public static final String TYPE_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Tip je vec definisan u deskriptoru ove komponente";
    public static final String TYPE_NOT_DEFINED = "Tip nije definisan u deskriptoru ove komponente";

    public static final String ID_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Id je vec definisan u deskriptoru ove komponente";
    public static final String ID_NOT_DEFINED = "Id nije definisan u deskriptoru ove komponente";

    public static final String QUEUE_PRINCIPLE_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Princip rada je vec definisan u deskriptoru ove komponente";
    public static final String SERVING_DISCIPLINE_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Princip odabira servera je vec definisan u deskriptoru ove komponente";
    public static final String UNKNOWN_QUEUE_PRINCIPLE = "Nepoznat princip rada";
    public static final String UNKNOWN_SERVING_DISCIPLINE = "Nepoznat princip odabira servera";

    public static final String EXIT_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Izlaz je vec definisan u deskriptoru ove komponente";
    public static final String EXIT_NOT_DEFINED = "Izlaz nije definisan u deskriptoru ove komponente";

    public static final String DISTRIBUTION_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Raspodela je vec definisana u deskriptoru ove komponente";
    public static final String UNKNOWN_DISTRIBUTION = "Nepoznata raspodela";

    public static final String EXECUTION_TIME_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Prosecno vreme obrade je vec definisano u deskriptoru ove komponente";
    public static final String EXECUTION_TIME_NOT_DEFINED = "Prosecno vreme obrade nije definisano u deskriptoru ove komponente";

    public static final String REQUEST_NUMBER_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR = "Broj zahteva je vec definisan";
    public static final String REQUEST_NUMBER_NOT_DEFINED = "Broj zahteva nije definisan";

    public static final String REQUEST_PRIORITY_ALREADY_DEFINED = "Prioritet zahteva je vec definisan";

    public static final String REQUEST_START_POINT_ALREADY_DEFINED = "Polazne tacke zahteva su vec definisane";

    public static final String WARMUP_TIME_ALREADY_DEFINED = "Vreme trajanja zagrevanja simulacije je vec definisano";
    public static final String WARMUP_TIME_NOT_DEFINED = "Vreme trajanja zagrevanja simulacije nije definisano";

    public static final String SIMULATION_TIME_ALREADY_DEFINED = "Vreme trajanja simulacije je vec definisano";
    public static final String SIMULATION_TIME_NOT_DEFINED = "Vreme trajanja simulacije nije definisano";

    public static final String SIMULATION_PARAMETERS_NOT_DEFINED = "Simulacioni parametri nisu definisani";
    public static final String SIMULATION_PARAMETERS_ALREADY_DEFINED = "Simulacioni parametri su visestruko definisani";

    public static final String BRANCHES_NOT_DEFINED = "Grane nisu definisane u deskriptoru ove komponente";

    public static final String UNKNOWN_TOKEN = "Nepoznat token";
    public static final String INVALID_TOKEN = "Nekompatibilan token sa tipom komponente";

    public static final String NO_ERRORS = "Nema gresaka";

    public static final String GLOBAL_ANALYSIS = "***GLOBALNA ANALIZA***";

    public static final String COMPONENT_WITH_ID = "Komponenta ciji je id ";
    public static final String NOT_DEFINED = " nije definisana";
    public static final String MORE_THAN_ONE_PREDECESSOR = " ima vise od jednog prethodnika";
    public static final String EXIT_ERROR = " nema sledbenika odgovarajuceg tipa";
    public static final String BRANCHES_SUM_ERROR = " nema grane sa zbirom verovatnoca jednakim 100";
    public static final String MULTIPLE_DEFINITIONS = " je identifikator veceg broja komponenti";
    public static final String COMPONENT_HANGING = " nije povezana sa ostatkom sistema";

}
