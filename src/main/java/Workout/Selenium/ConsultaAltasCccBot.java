package Workout.Selenium;

import Workout.Config.SSUrls;
import Workout.ORM.Model.ConsultaAltasCcc;
import Workout.ORM.Model.Operation;
import com.google.gson.Gson;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.HashMap;

public class ConsultaAltasCccBot extends BaseBot {
    private ConsultaAltasCcc op;

    public ConsultaAltasCccBot(Operation op, HashMap<String, Object> config) {
        super(op, config);
        this.op = (ConsultaAltasCcc) op;
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
         * Rellenar régimen
         * Un sólo campo de 4 dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFREGCTA_ayuda")).sendKeys(this.op.getCca().getReg());

        /*
         * Rellenar cuenta de cotización
         * Primer campo, dos dígitos INT
         * Segundo campo, nueve dígitos INT
         */
        this.getDriver().findElement(By.name("txt_SDFTESCTA")).sendKeys(this.op.getCca().getCcc().substring(0, 2));
        this.getDriver().findElement(By.name("txt_SDFNUMCTA")).sendKeys(this.op.getCca().getCcc().substring(2, 9));

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
         * Marcar todas las opciones para que salgan mas cosas en el formulario.
         *
         */
        this.getDriver().findElement(By.name("chk_SDFCONSSALDOS")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSCOLE")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSSII")).click();
        if (this.waitFormSubmit(By.name("btn_Sub2207601004"))) {
            return this.thirdForm();
        }
        return false;
    }

    private boolean thirdForm() {
        /*
         * Marcar todas las opciones para que salgan mas cosas en el formulario.
         */
        this.getDriver().findElement(By.name("chk_SDFCONSALTA")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSAC")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSPREV")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSHUELGA")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSASIMI")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSSUBCONOTRO")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSSUBCON")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSEMPRE")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSATEP")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSCONVCOL")).click();
        this.getDriver().findElement(By.name("chk_SDFCONSFUNCI")).click();
        if (this.waitFormSubmit(By.name("btn_Sub2207601004"))) {
            return this.fourthForm();
        }
        return false;
    }

    private boolean fourthForm() {
        /*
         * Empezar iteraciones para recoger datos.
         */
        String baseElName = "Sub0900113079_{{COL}}_{{ROW}}";
        boolean workersRemaining = true;
        ArrayList<HashMap<String, String>> workers = new ArrayList<>();
        int actualPage = 1;
        while (workersRemaining) {
            for (int row = 0; row < 13; row++) {
                try {
                    HashMap<String, String> worker = new HashMap<>();
                    worker.put("NAF", this.getDriver().findElement(By.id(baseElName.
                            replace("{{ROW}}", String.valueOf(row)).replace("{{COL}}", "1")
                    )).getText());
                    worker.put("SIT", this.getDriver().findElement(By.id(baseElName.
                            replace("{{ROW}}", String.valueOf(row)).replace("{{COL}}", "2")
                    )).getText());
                    worker.put("NAP", this.getDriver().findElement(By.id(baseElName.
                            replace("{{ROW}}", String.valueOf(row)).replace("{{COL}}", "3")
                    )).getText());
                    worker.put("IFT", this.getDriver().findElement(By.id(baseElName.
                            replace("{{ROW}}", String.valueOf(row)).replace("{{COL}}", "4")
                    )).getText());
                    workers.add(worker);
                } catch (Exception e) {
                    this.logger.info("Deteniendo recogida de datos. No hay más usuarios.");
                    workersRemaining = false;
                    break;
                }
            }

            this.getDriver().findElement(By.name("btn_Sub2207901001")).click();

            /*
             * Esperar a que se envíe el formulario.
             */
            this.logger.info("Cargando más registros...");
            this.waitFormSubmit(By.name("Sub0900113079"));
            this.logger.info("Procesada página " + actualPage + ", intentando avanzar...");
            actualPage++;
        }
        /*
         * Guardar su trabajadores en la base de datos.
         */

        Gson gson = new Gson();
        String json = gson.toJson(workers);
        this.op.setData(json);
        this.queueService.saveOp(this.op);

        return true;
    }
}
