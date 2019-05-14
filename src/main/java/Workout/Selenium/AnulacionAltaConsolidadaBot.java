package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.Logger.LogService;
import Workout.ORM.Model.Alta;
import Workout.ORM.Repository.ProcessStatusRepository;
import org.openqa.selenium.By;

public class AnulacionAltaConsolidadaBot extends OperationManager {
    private Alta op;

    public AnulacionAltaConsolidadaBot(LogService logger, Alta op, String envProfile, Integer operationTimeout, String screenshootPath,
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
        this.getDriver().findElement(By.name("txt_SDFTESORNAF")).sendKeys(this.op.getNaf().substring(0, 2));
        this.getDriver().findElement(By.name("txt_SDFNUMNAF")).sendKeys(this.op.getNaf().substring(2, 10));

        /*
         * Rellenar régimen
         * Un sólo campo de 4 dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFREGCC_ayuda")).sendKeys(this.op.getCca().getReg());

        /*
         * Rellenar cuenta de cotización
         * Primer campo, dos dígitos INT
         * Segundo campo, nueve dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFTESCC")).sendKeys(this.op.getCca().getCcc().substring(0, 2));
        this.getDriver().findElement(By.name("txt_SDFNUMCC")).sendKeys(this.op.getCca().getCcc().substring(2, 9));

        /*
         * Clickar en el botón de enviar
         * Aquí concluye la primera parte del formulario
         */
        return this.waitFormSubmit(By.name("btn_Sub2207501004"));
    }

}
