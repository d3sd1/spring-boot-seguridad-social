package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.Logger.LogService;
import Workout.ORM.Model.ConsultaNaf;
import Workout.ORM.Repository.ProcessStatusRepository;
import org.openqa.selenium.By;

public class ConsultaNafBot extends OperationManager {
    private ConsultaNaf op;

    public ConsultaNafBot(LogService logger, ConsultaNaf op, String envProfile, Integer operationTimeout, String screenshootPath,
                          ProcessStatusRepository processRepository) {
        super(logger, op, envProfile, operationTimeout, screenshootPath, processRepository);
        this.op = op;
    }

    protected void initialNavigate() {
        this.navigate(SSUrls.CONSULTAIPF);
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
        this.getDriver().findElement(By.name("txt_SDFTIPO_ayuda")).sendKeys(this.op.getIpt());
        this.getDriver().findElement(By.name("txt_SDFNUMERO")).sendKeys(this.op.getIpf());

        /*
         * Rellenar apellidos.
         * Primer campo, primer apellido.
         * Segundo campo, segundo apellido.
         */
        this.getDriver().findElement(By.name("txt_SDFAPELL1")).sendKeys(this.op.getAp1());
        this.getDriver().findElement(By.name("txt_SDFAPELL2")).sendKeys(this.op.getAp2());

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
         * Comprobar si hay errores específicos, ya que en esta consulta
         * tienen otra excepción que no tiene nada que ver con los demás.
         */
        this.op.setData(this.getDriver().findElement(By.id("SDFPROVNAF")).getText() +
                this.getDriver().findElement(By.id("SDFNUMNAF")).getText());
        //TODO: actualizar registro contra la db

        /*
         * Enviar formulario.
         */
        return true;
    }
}
