package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.ORM.Model.ConsultaAlta;
import Workout.ORM.Model.Operation;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import java.util.HashMap;

public class ConsultaAltaBot extends BaseBot {
    private ConsultaAlta op;

    public ConsultaAltaBot(Operation op, HashMap<String, Object> config) {
        super(op, config);
        this.op = (ConsultaAlta) op;
        this.logger.info("Processing operation " + this.op.getId());
        if (this.configure()) {
            this.manageOperation();
            this.destroy();
        }
    }
    protected void initialNavigate() {
        this.navigate(SSUrls.ALTA);
    }

    protected boolean firstForm() {

        /*
         * Rellenar número de afiliación
         * Primer campo, dos dígitos INT
         * Segundo campo, diez dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFTESNAF")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(0, 2) : "");
        this.getDriver().findElement(By.name("txt_SDFNAF")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(2, 10) : "");

        /*
         * Rellenar régimen
         * Un sólo campo de 4 dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFREGCTA_NH")).sendKeys(this.op.getCca().getReg());

        /*
         * Rellenar cuenta de cotización
         * Primer campo, dos dígitos INT
         * Segundo campo, nueve dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFTESCTA")).sendKeys(this.op.getCca().getCcc().substring(0, 2));
        this.getDriver().findElement(By.name("txt_SDFCUENTA")).sendKeys(this.op.getCca().getCcc().substring(2, 9));

        /*
         * Rellenar fecha real de la consulta.
         * Tres campos (día, mes, año)
         */
        this.getDriver().findElement(By.name("txt_SDFDIA")).sendKeys(String.valueOf(this.op.getFrc().getDayOfMonth()));
        this.getDriver().findElement(By.name("txt_SDFMES")).sendKeys(String.valueOf(this.op.getFrc().getMonthValue()));
        this.getDriver().findElement(By.name("txt_SDFAO")).sendKeys(String.valueOf(this.op.getFrc().getYear()));

        /*
         * Seleccionar del listado como impresión online.
         */
        Select selectBaja = new Select(this.getDriver().findElement(By.id("cbo_ListaTipoImpresion")));
        selectBaja.selectByVisibleText("OnLine");


        if (this.waitFormSubmit(By.name("btn_Sub2207601004"))) {
            return this.secondForm();
        }

        return false;
    }

    private boolean secondForm() {
        // TODO: Esta parte falla. Habrá que retocarla cuando sea necesario.
        /*

            $this->clearTmpFolder();

            /*
             * Doble click para descargar el TA, sobre el primer elemento de la tabla (más próximo a la fecha indicada).
             * Esperar a que cargue antes, ya que es asíncrono.
             *
            if (!$this->waitFormSubmit(WebDriverBy::id('Sub0900112079_1_0'))) {
            $this->container->get("app.dblogger")->info("No se encontró la tabla.");
            return false;
        }
            $this->container->get("app.dblogger")->info("Clickando elemento...");


            $op = new \stdClass();
            $op->alta = new \stdClass();
            $op->baja = new \stdClass();

            $fil = 0;
            while(true) {
                $elKeyname = "Sub0900112079_{{col}}_{{fil}}";
                if(count($this->driver->findElements(WebDriverBy::id(str_replace(array("{{col}}","{{fil}}"),array("1",$fil),$elKeyname)))) === 0) {
                    break;
                }
                $elSsName = $this->driver->findElement(WebDriverBy::id(str_replace(array("{{col}}","{{fil}}"),array("1",$fil),$elKeyname)))->getText();
                if(stristr($elSsName, 'alta') !== false) {
                    $op->alta->nombreSS = $elSsName;
                    $op->alta->fecha = $this->driver->findElement(WebDriverBy::id(str_replace(array("{{col}}","{{fil}}"),array("2",$fil),$elKeyname)))->getText();
                }
                else if(stristr($elSsName, 'baja') !== false) {
                    $op->alta->nombreSS = $elSsName;
                    $op->alta->fecha = $this->driver->findElement(WebDriverBy::id(str_replace(array("{{col}}","{{fil}}"),array("2",$fil),$elKeyname)))->getText();
                }

                $fil++;
            }
            /*
             * Guardar en la DB.
             *
            $this->updateConsultaData(
                    json_encode($op)
            );

            return true;
         */
        return false;
    }

}
