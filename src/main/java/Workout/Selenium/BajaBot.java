package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.Logger.LogService;
import Workout.ORM.Model.Baja;
import Workout.ORM.Repository.ProcessStatusRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

public class BajaBot extends OperationManager {
    private Baja op;

    public BajaBot(LogService logger, Baja op, String envProfile, Integer operationTimeout, String screenshootPath,
                   ProcessStatusRepository processRepository) {
        super(logger, op, envProfile, operationTimeout, screenshootPath, processRepository);
        this.op = op;
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
         * Primero, seleccionar baja en el tipo de formulario.
         */
        Select selectBaja = new Select(this.getDriver().findElement(By.id("ListaAltasBajas")));
        selectBaja.selectByVisibleText("Baja");

        /*
         * Rellenar número de afiliación
         * Primer campo, dos dígitos INT
         * Segundo campo, diez dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFPROAFI")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(0, 2) : "");
        this.getDriver().findElement(By.name("txt_SDFCODAFI")).sendKeys(this.op.getNaf() != null ? this.op.getNaf().substring(2, 10) : "");

        /*
         * Rellenar identificación de personas físicas
         * Primer campo, un dígito INT
         * Segundo campo, diez dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFTIPPFI_ayuda")).sendKeys(this.op.getIpt());
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
        this.getDriver().findElement(By.name("txt_SDFCTACOT")).sendKeys(this.op.getCca().getCcc().substring(2, 9));

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

        /*
         * **************************************
         * Rellenar segunda parte del formulario.
         * **************************************
         */

        /*
         * Rellenar situación. Dos dígitos int.
         */
        this.getDriver().findElement(By.name("txt_SDFSITAFI_ayuda")).sendKeys(String.valueOf(this.op.getSit()));

        /*
         * Rellenar fecha real de la baja.
         * Tres campos (día, mes, año)
         */
        this.getDriver().findElement(By.name("txt_SDFFREALDD")).sendKeys(String.valueOf(this.op.getFrb().getDayOfMonth()));
        this.getDriver().findElement(By.name("txt_SDFFREALMM")).sendKeys(String.valueOf(this.op.getFrb().getMonthValue()));
        this.getDriver().findElement(By.name("txt_SDFFREALAA")).sendKeys(String.valueOf(this.op.getFrb().getYear()));

        if (this.op.getFfv() != null && !this.op.getFfv().equals("")) {
            this.getDriver().findElement(By.name("txt_SDFFFINVDD")).sendKeys(String.valueOf(this.op.getFfv().getDayOfMonth()));
            this.getDriver().findElement(By.name("txt_SDFFFINVMM")).sendKeys(String.valueOf(this.op.getFfv().getMonthValue()));
            this.getDriver().findElement(By.name("txt_SDFFFINVAA")).sendKeys(String.valueOf(this.op.getFfv().getYear()));
        }

        /*
         * Enviar formulario.
         */
        return this.waitFormSubmit(By.name("btn_Sub2207401004"));
    }
}
