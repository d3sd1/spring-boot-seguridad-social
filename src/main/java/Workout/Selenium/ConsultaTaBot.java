package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.ORM.Model.ConsultaTa;
import Workout.ORM.Model.Operation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.HashMap;

public class ConsultaTaBot extends BaseBot {
    private ConsultaTa op;

    public ConsultaTaBot(Operation op, HashMap<String, Object> config) {
        super(op, config);
        this.op = (ConsultaTa) op;
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
        this.getDriver().findElement(By.name("txt_SDFNAF")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(2, 12) : "");

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
        this.getDriver().findElement(By.name("txt_SDFCUENTA")).sendKeys(this.op.getCca().getCcc().substring(2, 11));

        /*
         * Rellenar fecha real de la consulta.
         * Tres campos (día, mes, año)
         */
        this.getDriver().findElement(By.name("txt_SDFDIA")).sendKeys(String.valueOf(this.op.getFrc().getDayOfMonth()));
        this.getDriver().findElement(By.name("txt_SDFMES")).sendKeys(String.valueOf(this.op.getFrc().getMonthValue()));
        this.getDriver().findElement(By.name("txt_SDFAO")).sendKeys(String.valueOf(this.op.getFrc().getYear()));

        /*
         * Rellenar siempre en opción online.
         */
        Select selectBaja = new Select(this.getDriver().findElement(By.name("cbo_ListaTipoImpresion")));
        selectBaja.selectByVisibleText("OnLine");

        if (this.waitFormSubmit(By.name("btn_Sub2207601004"))) {
            return this.secondForm();
        }
        return false;
    }

    public boolean secondForm() {

        /*
         * Doble click para descargar el TA, sobre el primer elemento de la tabla (más próximo a la fecha indicada).
         * Esperar a que cargue antes, ya que es asíncrono.
         */

        if (!this.waitFormSubmit(By.id("Sub0900112079_1_0"))) {
            this.logger.info("No se encontró la tabla.");
            return false;
        }

        WebElement formSubmit = this.getDriver().findElement(By.name("Sub0900112079_1_0"));
        Actions actions = new Actions(this.getDriver());
        actions.doubleClick(formSubmit).perform();
        /*
         * Esperar a se descargue el TA.
         * TODO:
         *  if (!$this -> waitTmpDownload()) {
            $this -> container -> get("app.dblogger")->info("No se pudo descargar el TA.");
            return false;
        }
         */


        return true;
    }

}
