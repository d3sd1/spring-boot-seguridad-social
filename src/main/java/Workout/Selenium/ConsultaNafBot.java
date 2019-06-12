package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.ORM.Model.ConsultaNaf;
import Workout.ORM.Model.Operation;
import org.openqa.selenium.By;

import java.util.HashMap;

public class ConsultaNafBot extends BaseBot {
    private ConsultaNaf op;

    public ConsultaNafBot(Operation op, HashMap<String, Object> config) {
        super(op, config);
        this.op = (ConsultaNaf) op;
        this.logger.info("Processing operation " + this.op.getId());
        if (this.configure()) {
            this.manageOperation();
            this.destroy();
        }
    }
    protected void initialNavigate() {
        this.navigate(SSUrls.CONSULTA_NAF);
    }

    protected boolean firstForm() {

        /*
         * **************************************
         * Rellenar primera parte del formulario.
         * **************************************
         */

        /*
         * Rellenar identificación de personas físicas
         * Primer campo, un dígito INT
         * Segundo campo, diez dígitos INT
         */
        this.getDriver().findElement(By.name("txt_Sub0802501055_42")).sendKeys(String.valueOf(Integer.parseInt(this.op.getIpt())));
        this.getDriver().findElement(By.name("txt_Sub0802501055_43")).sendKeys(this.op.getIpf());

        /*
         * Rellenar apellidos.
         * Primer campo, primer apellido.
         * Segundo campo, segundo apellido.
         */
        this.getDriver().findElement(By.name("txt_Sub0802501055_44")).sendKeys(this.op.getAp1());
        this.getDriver().findElement(By.name("txt_Sub0802501055_45")).sendKeys(this.op.getAp2());

        /*
         * Clickar en el botón de enviar
         * Aquí concluye la primera parte del formulario
         */
        if (this.waitFormSubmit(By.name("btn_Sub2102101004_41"))) {
            return this.secondForm();
        }
        return false;
    }

    private boolean secondForm() {

        /*
         * Comprobar si hay errores específicos, ya que en esta consulta
         * tienen otra excepción que no tiene nada que ver con los demás.
         */
        this.op.setData(this.getDriver().findElement(By.id("Sub0200101079_11")).getText() +
                this.getDriver().findElement(By.id("Sub0200101079_42")).getText());
        this.queueService.saveOp(this.op);

        /*
         * Enviar formulario.
         */
        return true;
    }
}
