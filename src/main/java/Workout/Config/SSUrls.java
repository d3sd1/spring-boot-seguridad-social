package Workout.Config;

public class SSUrls {

    /*
     * Base URL
     */
    public static final String TEST_STATUS_URL = "https://w2.seg-social.es/GetAccess/ResourceList";
    public static final String BASE_URL = "https://w2.seg-social.es/";

    /*
     * AltaBot
     */
    public static final String ALTA = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR01&E=I&AP=AFIR";
    /*
     * Baja
     */
    public static final String BAJA = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR01&E=I&AP=AFIR";
    /*
     * Anulación alta previa
     */
    public static final String ANULACIONALTAPREVIA = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR42&E=I&AP=AFIR";
    /*
     * Anulación alta consolidada
     */
    public static final String ANULACIONALTACONSOLIDADA = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR41&E=I&AP=AFIR";
    /*
     * Anulación baja previa
     */
    public static final String ANULACIONBAJAPREVIA = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR42&E=I&AP=AFIR";
    /*
     * Anulación baja consolidada
     */
    public static final String ANULACIONBAJACONSOLIDADA = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR02&E=I&AP=AFIR";
    /*
     * Cambio de contrato
     */
    public static final String CAMBIOCONTRATOCONSOLIDADO = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR45&E=I&AP=AFIR";
    public static final String CAMBIOCONTRATOPREVIO = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR42&E=I&AP=AFIR";
    /*
     * Consultar IPF por NAF
     */
    public static final String CONSULTAIPF = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR66&E=I&AP=AFIR";
    /*
     * Consultar NAF por IPF
     */
    public static final String CONSULTANAF = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR67&E=I&AP=AFIR";
    /*
     * Consultar afiliados actualmente
     */
    public static final String CONSULTAALTASCCC = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR";
    /*
     * Duplicados de TA
     */
    public static final String CONSULTATA = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR";
    /*
     * Consulta de alta contra la seguridad social
     */
    public static final String CONSULTAALTA = BASE_URL + "Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR";
}
