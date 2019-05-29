package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.ORM.Model.CambioContratoConsolidado;
import Workout.ORM.Model.Operation;
import org.openqa.selenium.By;

import java.util.HashMap;

public class CambioContratoConsolidadoBot extends BaseBot {
    private CambioContratoConsolidado op;

    public CambioContratoConsolidadoBot(Operation op, HashMap<String, Object> config) {
        super(op, config);
        this.op = (CambioContratoConsolidado) op;
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
         * **************************************
         * Rellenar primera parte del formulario.
         * **************************************
         */

        /*
         * Rellenar número de afiliación
         * Primer campo, dos dígitos INT
         * Segundo campo, diez dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFPROAFI")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(0, 2) : "");
        this.getDriver().findElement(By.name("txt_SDFCODAFI")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(2, 12) : "");

        /*
         * Rellenar identificación de personas físicas
         * Primer campo, un dígito INT
         * Segundo campo, diez dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFTIPPFI_ayuda")).sendKeys(String.valueOf(Integer.parseInt(this.op.getIpt())));
        this.getDriver().findElement(By.name("txt_SDFNUMPFI")).sendKeys(this.op.getIpf());

        /*
         * Rellenar régimen
         * Un sólo campo de 4 dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFREGAFI_ayuda")).sendKeys(this.op.getCca().getReg());

        /*
         * Rellenar cuenta de cotización
         * Primer campo, dos dígitos INT
         * Segundo campo, nueve dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFTESCTACOT")).sendKeys(this.op.getCca().getCcc().substring(0, 2));
        this.getDriver().findElement(By.name("txt_SDFCTACOT")).sendKeys(this.op.getCca().getCcc().substring(2, 11));

        /*
         * Clickar en el botón de enviar
         * Aquí concluye la primera parte del formulario
         */
        if (this.waitFormSubmit(By.name("btn_Sub2207601004"))) {
            return this.secondForm();
        }
        return false;
    }

    private boolean secondForm() {

        /*
         * **************************************
         * Rellenar segunda parte del formulario.
         * **************************************
         */

        /*
         * Rellenar fecha real del alta.
         * Tres campos (día, mes, año)
         */
        this.getDriver().findElement(By.name("txt_SDFFREALDD")).sendKeys(String.valueOf(this.op.getFrc().getDayOfMonth()));
        this.getDriver().findElement(By.name("txt_SDFFREALMM")).sendKeys(String.valueOf(this.op.getFrc().getMonthValue()));
        this.getDriver().findElement(By.name("txt_SDFFREALAA")).sendKeys(String.valueOf(this.op.getFrc().getYear()));

        /*
         * Rellenar tipo de contrato.
         * Tres dígitos INT.
         */
        this.getDriver().findElement(By.name("txt_SDFTICO_ayuda")).sendKeys(String.valueOf(this.op.getTco().getCkey()));

        /*
         * Rellenar coeficiente de tiempo parcial,
         * sólo si el contrato es de tiempo parcial.
         * Tres dígitos INT.
         */
        this.getDriver().findElement(By.name("txt_SDFCOEFCO_ayuda")).clear();
        if (this.op.getTco().getTimeType().getTimeType().equalsIgnoreCase("TIEMPO_PARCIAL")) {
            this.getDriver().findElement(By.name("txt_SDFCOEFCO_ayuda")).sendKeys(String.valueOf(this.op.getCoe().getCoefficient()));
        }

        /*
         * Enviar formulario.
         */
        return this.waitFormSubmit(By.name("btn_Sub2207401004"));
    }
}
