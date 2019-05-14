package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.Logger.LogService;
import Workout.ORM.Model.AnulacionBajaConsolidada;
import Workout.ORM.Repository.ProcessStatusRepository;
import org.openqa.selenium.By;

public class AnulacionBajaConsolidadaBot extends OperationManager {
    private AnulacionBajaConsolidada op;

    public AnulacionBajaConsolidadaBot(LogService logger, AnulacionBajaConsolidada op, String envProfile, Integer operationTimeout, String screenshootPath,
                                       ProcessStatusRepository processRepository) {
        super(logger, op, envProfile, operationTimeout, screenshootPath, processRepository);
        this.op = op;
    }

    protected void initialNavigate() {
        this.navigate(SSUrls.ANULACIONALTACONSOLIDADA);
    }

    protected boolean firstForm() {
        /*
         * Rellenar número de afiliación
         * Primer campo, dos dígitos INT
         * Segundo campo, diez dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFIDPRONAF")).sendKeys(this.op.getNaf().substring(0, 2));
        this.getDriver().findElement(By.name("txt_SDFIDNAFCON")).sendKeys(this.op.getNaf().substring(2, 10));

        /*
         * Rellenar identificación de personas físicas
         * Primer campo, un dígito INT
         * Segundo campo, diez dígitos INT
         */

        this.getDriver().findElement(By.name("txt_SDFIDTIPOPF_ayuda")).sendKeys(this.op.getIpt());
        this.getDriver().findElement(By.name("txt_SDFIDNIDEPF")).sendKeys(this.op.getIpf());

        /*
         * Rellenar régimen
         * Un sólo campo de 4 dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFIDREGIMEN_ayuda")).sendKeys(this.op.getCca().getReg());

        /*
         * Rellenar cuenta de cotización
         * Primer campo, dos dígitos INT
         * Segundo campo, nueve dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFIDTESCTA")).sendKeys(this.op.getCca().getCcc().substring(0, 2));
        this.getDriver().findElement(By.name("txt_SDFIDCTACON")).sendKeys(this.op.getCca().getCcc().substring(2, 9));

        /*
         * Rellenar fecha real de la baja.
         * Tres campos (día, mes, año)
         */
        this.getDriver().findElement(By.name("txt_SDFIDFREALDD")).sendKeys(String.valueOf(this.op.getFrb().getDayOfMonth()));
        this.getDriver().findElement(By.name("txt_SDFIDFREALMM")).sendKeys(String.valueOf(this.op.getFrb().getMonthValue()));
        this.getDriver().findElement(By.name("txt_SDFIDFREALAA")).sendKeys(String.valueOf(this.op.getFrb().getYear()));


        /*
         * Clickar en el botón de enviar
         * Aquí concluye la primera parte del formulario
         */
        if (this.waitFormSubmit(By.name("btn_Sub2207401004"))) {
            return this.secondForm();
        }
        return false;
    }


    private boolean secondForm() {
        return this.waitFormSubmit(By.name("btn_Sub2205301005"));
    }

}
